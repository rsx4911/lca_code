package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.jsonld.Schema;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.collaboration.index.DatasetIndex;
import com.greendelta.collaboration.index.DatasetIndexEntry;
import com.greendelta.collaboration.service.upgrade.IUpgrade;
import com.greendelta.collaboration.service.upgrade.Upgrade1;
import com.greendelta.collaboration.util.IProgressMonitor;

public class UpgradeService {

	private static final List<IUpgrade> UPGRADES = Arrays.asList(new IUpgrade[] {
			new Upgrade1()
	});
	private static boolean upgrading;
	private final String rootPath;
	private final RepositoryIndices repositoryIndices;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public UpgradeService(@Named("repository.path") String rootPath, RepositoryIndices repositoryIndices) {
		this.rootPath = rootPath;
		this.repositoryIndices = repositoryIndices;
	}

	public boolean upgradeAvailable() {
		if (UpgradeService.isUpgrading())
			return false;
		return !getOutdated().isEmpty();
	}

	private List<Repository> getOutdated() {
		File root = new File(rootPath);
		List<Repository> repos = new ArrayList<>();
		for (File group : root.listFiles()) {
			if (group.listFiles() == null)
				continue;
			for (File name : group.listFiles()) {
				if (!name.isDirectory())
					continue;
				Repository repo = Repository.getIgnoreSchema(rootPath, group.getName(), name.getName());
				if (repo.getSchemaVersion().equals(Schema.URI))
					continue;
				repos.add(repo);
			}
		}
		return repos;
	}

	public void upgrade(IProgressMonitor monitor) {
		List<Repository> outdated = getOutdated();
		if (outdated.isEmpty()) {
			monitor.done();
			return;
		}
		if (upgrading) {
			monitor.done();
			return;
		}
		upgrading = true;
		monitor.started(outdated.size());
		for (Repository repo : outdated) {
			if (monitor.canceled())
				continue;
			monitor.task("Upgrading repository " + repo.toId());
			DatasetIndex index = repositoryIndices.get(repo);
			String repoSchema = repo.getSchemaVersion();
			for (IUpgrade upgrade : getUpgrades(repoSchema)) {
				upgrade.run(repo, index, this::getJson, this::putJson);
				repo.setSchemaVersion(upgrade.toSchema());
			}
			monitor.worked();
		}
		monitor.done();
		upgrading = false;
	}

	private JsonObject getJson(Repository repo, DatasetIndexEntry indexEntry) {
		File dsFile = repo.getDatasetFile(indexEntry.type, indexEntry.refId, indexEntry.commitId, false);
		if (dsFile == null)
			return null;
		String json = dataAccessor.readDataset(dsFile);
		if (json == null)
			return null;
		if (Strings.isNullOrEmpty(json))
			return null;
		return new Gson().fromJson(json, JsonObject.class);
	}

	private void putJson(Repository repo, DatasetIndexEntry indexEntry, JsonObject obj) {
		File dsFile = repo.getDatasetFile(indexEntry.type, indexEntry.refId, indexEntry.commitId, false);
		dataAccessor.writeDataset(dsFile, new Gson().toJson(obj));
	}

	public static boolean isUpgrading() {
		return upgrading;
	}

	private List<IUpgrade> getUpgrades(String repoSchema) {
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

}
