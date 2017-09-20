package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.util.KeyGen;
import org.openlca.util.Strings;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.DatasetIndexEntry;
import com.greendelta.collaboration.service.upgrade.IUpgrade;
import com.greendelta.collaboration.service.upgrade.Upgrade1;

public class RepositoryUpgrades {

	private static final List<IUpgrade> UPGRADES = Arrays.asList(new IUpgrade[] {
			new Upgrade1()
	});
	private static final DataAccessor dataAccessor = new DataAccessor();

	private static List<Repository> getOutdated(String rootPath, SearchService searchService) {
		File root = new File(rootPath);
		List<Repository> repos = new ArrayList<>();
		for (File group : root.listFiles()) {
			if (group.listFiles() == null)
				continue;
			for (File name : group.listFiles()) {
				if (!name.isDirectory())
					continue;
				Repository repo = Repository.getIgnoreSchema(rootPath, group.getName(), name.getName());
				// restructure repository directory if old version
				File oldIndexDir = new File(name, "ds_index");
				if (oldIndexDir.exists()) {
					IndexConversion.runOn(oldIndexDir, searchService);
					Directories.delete(oldIndexDir);
				}
				Restructuring.restructure(repo, searchService);
				if (repo.getSchemaVersion().equals(Schema.URI))
					continue;
				repos.add(repo);
			}
		}
		return repos;
	}

	public static void upgrade(String rootPath, SearchService searchService) {
		List<Repository> outdated = getOutdated(rootPath, searchService);
		if (outdated.isEmpty())
			return;
		for (Repository repo : outdated) {
			String repoSchema = repo.getSchemaVersion();
			for (IUpgrade upgrade : getUpgrades(repoSchema)) {
				upgrade.run(repo, searchService, RepositoryUpgrades::getJson, RepositoryUpgrades::putJson);
				repo.setSchemaVersion(upgrade.toSchema());
			}
		}
	}

	private static JsonObject getJson(Repository repo, DatasetIndexEntry indexEntry) {
		File dsFile = repo.getDatasetFile(indexEntry.type, indexEntry.refId, indexEntry.commitId, false);
		if (dsFile == null)
			return null;
		byte[] data = dataAccessor.read(dsFile);
		if (data == null || data.length == 0)
			return null;
		String json = new String(data, Charset.forName("utf-8"));
		return new Gson().fromJson(json, JsonObject.class);
	}

	private static void putJson(Repository repo, DatasetIndexEntry indexEntry, JsonObject obj) {
		File dsFile = repo.getDatasetFile(indexEntry.type, indexEntry.refId, indexEntry.commitId, false);
		dataAccessor.write(dsFile, new Gson().toJson(obj).getBytes(Charset.forName("utf-8")));
	}

	private static List<IUpgrade> getUpgrades(String repoSchema) {
		List<IUpgrade> upgrades = new ArrayList<>();
		boolean stillNewer = true;
		for (IUpgrade upgrade : UPGRADES) {
			if (upgrade.fromSchema().equals(repoSchema)) {
				stillNewer = false;
			}
			if (!stillNewer) {
				upgrades.add(upgrade);
			}
		}
		return upgrades;
	}

	private static class Restructuring {

		private static void restructure(Repository repo, SearchService searchService) {
			for (ModelType type : ModelType.values()) {
				boolean changedBefore = restructure(repo.getModelDir(type, false));
				if (changedBefore)
					return;
				restructure(repo.getBinDir(type, false));
			}
			updateCategoryRefIds(repo, searchService);
		}

		private static boolean restructure(File dir) {
			for (File child : getFiles(dir)) {
				if (child.length() == 2)
					// This was already done in this repository, so stop
					// searching
					return true;
				File moveTo = new File(child.getParentFile(), child.getName().substring(0, 2));
				moveTo.mkdir();
				child.renameTo(new File(moveTo, child.getName()));
			}
			return false;
		}

		private static File[] getFiles(File dir) {
			if (dir == null)
				return new File[0];
			if (!dir.exists())
				return new File[0];
			if (!dir.isDirectory())
				return new File[0];
			File[] files = dir.listFiles();
			if (files == null)
				return new File[0];
			return files;
		}

		private static void updateCategoryRefIds(Repository repo, SearchService searchService) {
			List<DatasetIndexEntry> entries = searchService.getAll(repo, ModelType.CATEGORY);
			List<DatasetIndexEntry> referencing = new ArrayList<>();
			for (DatasetIndexEntry entry : entries) {
				String newRefId = KeyGen.get((entry.categoryType.name() + "/" + entry.fullPath).split("/"));
				if (entry.refId.equals(newRefId))
					continue;
				entry.refId = newRefId;
				List<DatasetIndexEntry> elements = searchService.getForCategory(repo, entry.refId);
				for (DatasetIndexEntry element : elements) {
					element.categoryRefId = newRefId;
				}
				referencing.addAll(elements);
			}
			List<DatasetIndexEntry> all = new ArrayList<>();
			all.addAll(entries);
			all.addAll(referencing);
			searchService.index(all);
		}

	}

	private static class IndexConversion {

		private static void runOn(File indexDir, SearchService searchService) {
			List<DatasetIndexEntry> entries = getIndexEntries(indexDir);
			if (entries.isEmpty())
				return;
			searchService.index(entries);
		}

		private static List<DatasetIndexEntry> getIndexEntries(File indexDir) {
			List<DatasetIndexEntry> all = new ArrayList<>();
			try {
				IndexSearcher searcher = getSearcher(indexDir);
				if (searcher == null)
					return new ArrayList<>();
				MatchAllDocsQuery query = new MatchAllDocsQuery();
				TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
				if (topDocs.totalHits == 0)
					return new ArrayList<>();
				for (ScoreDoc doc : topDocs.scoreDocs) {
					DatasetIndexEntry entry = convert(searcher.doc(doc.doc));
					all.add(entry);
				}
			} catch (IOException e) {
				LoggerFactory.getLogger(RepositoryUpgrades.class).error("Error retrieving dataset identifiers", e);
			}
			return all;
		}

		private static DatasetIndexEntry convert(Document document) {
			DatasetIndexEntry entry = new DatasetIndexEntry();
			entry.refId = document.get("refId");
			entry.type = ModelType.valueOf(document.get("type"));
			entry.name = document.get("name");
			entry.categoryRefId = document.get("categoryRefId");
			if (!Strings.nullOrEmpty(document.get("categoryType")))
				entry.categoryType = ModelType.valueOf(document.get("categoryType"));
			entry.commitId = document.get("commitId");
			entry.commitMessage = document.get("commitMessage");
			entry.fullPath = document.get("fullPath");
			entry.lastUpdate = Long.parseLong(document.get("lastUpdate"));
			entry.repositoryId = document.get("repositoryId");
			return entry;
		}

		public static IndexSearcher getSearcher(File indexDir) throws IOException {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir.toPath()));
			if (reader == null)
				return null;
			return new IndexSearcher(reader);
		}

	}

}
