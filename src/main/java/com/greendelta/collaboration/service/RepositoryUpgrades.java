package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.gson.reflect.TypeToken;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry;
import com.greendelta.collaboration.service.upgrade.IUpgrade;
import com.greendelta.collaboration.service.upgrade.Upgrade1;
import com.greendelta.collaboration.util.IndexEntryCreator;

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
					IndexConversion.runOn(repo, oldIndexDir, searchService);
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

	private static JsonObject getJson(Repository repo, IndexEntry indexEntry) {
		File dsFile = repo.getDatasetFile(indexEntry.type, indexEntry.refId, indexEntry.commitId, false);
		if (dsFile == null)
			return null;
		byte[] data = dataAccessor.read(dsFile);
		if (data == null || data.length == 0)
			return null;
		String json = new String(data, Charset.forName("utf-8"));
		return new Gson().fromJson(json, JsonObject.class);
	}

	private static void putJson(Repository repo, IndexEntry indexEntry, JsonObject obj) {
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
			List<IndexEntry> entries = searchService.getAll(repo);
			Map<String, List<IndexEntry>> byCategoryId = new HashMap<>();
			for (IndexEntry entry : entries) {
				if (Strings.nullOrEmpty(entry.categoryRefId))
					continue;
				List<IndexEntry> list = byCategoryId.get(entry.categoryRefId);
				if (list == null) {
					byCategoryId.put(entry.categoryRefId, list = new ArrayList<>());
				}
				list.add(entry);
			}
			List<IndexEntry> all = new ArrayList<>();
			for (IndexEntry entry : entries) {
				if (entry.type != ModelType.CATEGORY)
					continue;
				all.add(entry);
				String newRefId = KeyGen.get((entry.categoryType.name() + "/" + entry.fullPath).split("/"));
				if (entry.refId.equals(newRefId))
					continue;
				List<IndexEntry> elements = byCategoryId.get(entry.refId);
				entry.refId = newRefId;
				if (elements != null) {
					for (IndexEntry element : elements) {
						element.categoryRefId = newRefId;
					}
					all.addAll(elements);
				}
			}
			searchService.index(all);
		}

	}

	private static class IndexConversion {

		private static final Gson gson = new Gson();

		private static void runOn(Repository repo, File indexDir, SearchService searchService) {
			List<IndexEntry> entries = getIndexEntries(repo, indexDir);
			if (entries.isEmpty())
				return;
			searchService.index(entries);
		}

		private static List<IndexEntry> getIndexEntries(Repository repo, File indexDir) {
			List<IndexEntry> all = new ArrayList<>();
			try {
				IndexSearcher searcher = getSearcher(indexDir);
				if (searcher == null)
					return new ArrayList<>();
				MatchAllDocsQuery query = new MatchAllDocsQuery();
				TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
				if (topDocs.totalHits == 0)
					return new ArrayList<>();
				for (ScoreDoc doc : topDocs.scoreDocs) {
					IndexEntry entry = convert(repo, searcher.doc(doc.doc));
					all.add(entry);
				}
			} catch (IOException e) {
				LoggerFactory.getLogger(RepositoryUpgrades.class).error("Error retrieving dataset identifiers", e);
			}
			return all;
		}

		private static IndexEntry convert(Repository repo, Document document) {
			ModelType type = ModelType.valueOf(document.get("type"));
			IndexEntry entry = type == ModelType.PROCESS ? new ProcessIndexEntry() : new IndexEntry();
			entry.refId = document.get("refId");
			entry.type = type;
			entry.name = document.get("name");
			entry.categoryRefId = document.get("categoryRefId");
			if (!Strings.nullOrEmpty(document.get("categoryType")))
				entry.categoryType = ModelType.valueOf(document.get("categoryType"));
			entry.commitId = document.get("commitId");
			entry.commitMessage = document.get("commitMessage");
			entry.fullPath = document.get("fullPath");
			entry.lastUpdate = Long.parseLong(document.get("lastUpdate"));
			entry.repositoryId = document.get("repositoryId");
			if (type == ModelType.PROCESS) {
				putProcessMetaInfo(repo, (ProcessIndexEntry) entry);
			}
			return entry;
		}

		private static void putProcessMetaInfo(Repository repo, ProcessIndexEntry entry) {
			File file = repo.getDatasetFile(entry.type, entry.refId, entry.commitId, false);
			if (file == null || !file.exists())
				return;
			IndexEntryCreator.fillProcess(entry, readData(file));
		}

		private static Map<String, Object> readData(File file) {
			try {
				return gson.fromJson(new FileReader(file), new TypeToken<Map<String, Object>>() {
				}.getType());
			} catch (IOException e) {
				e.printStackTrace();
				return new HashMap<>();
			}
		}

		public static IndexSearcher getSearcher(File indexDir) throws IOException {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir.toPath()));
			if (reader == null)
				return null;
			return new IndexSearcher(reader);
		}

	}

}
