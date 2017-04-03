package com.greendelta.collaboration.service;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.index.DatasetIndex;
import com.greendelta.collaboration.index.DatasetIndexEntry;
import com.greendelta.collaboration.service.upgrade.IUpgrade;
import com.greendelta.collaboration.service.upgrade.Upgrade1;

public class RepositoryUpgrades {

	private static final List<IUpgrade> UPGRADES = Arrays.asList(new IUpgrade[] {
			new Upgrade1()
	});
	private static final RepositoryIndices repositoryIndices = new RepositoryIndices(null);
	private static final DataAccessor dataAccessor = new DataAccessor();

	private static List<Repository> getOutdated(String rootPath) {
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
				restructure(repo);
				if (repo.getSchemaVersion().equals(Schema.URI))
					continue;
				repos.add(repo);
			}
		}
		return repos;
	}

	public static void upgrade(String rootPath) {
		List<Repository> outdated = getOutdated(rootPath);
		if (outdated.isEmpty())
			return;
		for (Repository repo : outdated) {
			DatasetIndex index = repositoryIndices.get(repo);
			String repoSchema = repo.getSchemaVersion();
			for (IUpgrade upgrade : getUpgrades(repoSchema)) {
				upgrade.run(repo, index, RepositoryUpgrades::getJson, RepositoryUpgrades::putJson);
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

	private static void restructure(Repository repo) {
		for (ModelType type : ModelType.values()) {
			boolean changedBefore = restructure(repo.getModelDir(type, false));
			if (changedBefore)
				return;
			restructure(repo.getBinDir(type, false));
		}
		repositoryIndices.get(repo).updateCategoryRefIds();
	}

	private static boolean restructure(File dir) {
		for (File child : getFiles(dir)) {
			if (child.length() == 2)
				// This was already done in this repository, so stop searching
				return true;
			File moveTo = new File(child.getParentFile(), child.getName().substring(0, 2));
			moveTo.mkdir();
			child.renameTo(new File(moveTo, child.getName()));
		}
		return false;
	}

	public static void main(String[] args) {
		File file = new File("/opt/tests/test/abcd");
		new File("/opt/tests/test/ab").mkdirs();
		file.renameTo(new File("/opt/tests/test/ab/abcd"));
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

}
