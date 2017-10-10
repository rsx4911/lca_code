package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.greendelta.collaboration.util.Bytes;

class Repositories {

	static boolean clone(Repository from, Repository to, List<Commit> commits) {
		try {
			for (ModelType type : ModelType.values()) {
				cloneTypeContents(from, to, commits, type);
				cloneTypeBinContents(from, to, commits, type);
			}
			cloneHistoryContents(from, to, commits);
			cloneDirectFiles(from, to);
			return true;
		} catch (IOException e) {
			LoggerFactory.getLogger(Repositories.class).error("Error cloning repository contents", e);
			return false;
		}
	}

	private static void cloneTypeContents(Repository from, Repository to, List<Commit> commits, ModelType type)
			throws IOException {
		File modelDir = from.getModelDir(type, false);
		if (!modelDir.exists())
			return;
		Set<String> commitIds = toIds(commits);
		for (File subDir : modelDir.listFiles()) {
			for (File datasetDir : subDir.listFiles()) {
				for (File file : datasetDir.listFiles()) {
					String commitId = file.getName().substring(0, file.getName().indexOf(".json"));
					if (!commitIds.contains(commitId))
						continue;
					File copy = to.getDatasetFile(type, datasetDir.getName(), commitId, true);
					Files.copy(file, copy);
				}
			}
		}
	}

	private static void cloneTypeBinContents(Repository from, Repository to, List<Commit> commits, ModelType type)
			throws IOException {
		File binModelDir = from.getBinDir(type, false);
		if (!binModelDir.exists())
			return;
		Set<String> commitIds = toIds(commits);
		for (File subDir : binModelDir.listFiles()) {
			for (File datasetDir : subDir.listFiles()) {
				for (File dir : datasetDir.listFiles()) {
					String commitId = dir.getName();
					if (!commitIds.contains(commitId))
						continue;
					File[] files = dir.listFiles();
					if (files == null || files.length == 0)
						continue;
					File copyDir = to.getBinDir(type, datasetDir.getName(), commitId, true);
					for (File file : files) {
						File copy = new File(copyDir, file.getName());
						copy.createNewFile();
						Files.copy(file, copy);
					}
				}
			}
		}
	}

	private static void cloneHistoryContents(Repository from, Repository to, List<Commit> commits) throws IOException {
		File historyDir = from.getHistoryDir(false);
		if (!historyDir.exists())
			return;
		File copy = to.getHistoryFile(true);
		for (Commit commit : commits)
			Bytes.appendTo(copy, commit.toString());
	}

	private static void cloneDirectFiles(Repository from, Repository to) throws IOException {
		for (File file : from.repoDir.listFiles()) {
			if (file.isDirectory())
				continue;
			File copy = new File(to.repoDir, file.getName());
			copy.createNewFile();
			Files.copy(file, copy);
		}
	}

	private static Set<String> toIds(List<Commit> commits) {
		Set<String> commitIds = new HashSet<>();
		for (Commit commit : commits)
			commitIds.add(commit.id);
		return commitIds;
	}
}
