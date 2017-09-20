package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.openlca.cloud.api.data.ModelStreamReader;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.DatasetIndexEntry;

public class CommitService {

	private final static Logger log = LoggerFactory
			.getLogger(CommitService.class);

	private final UserService userService;
	private final AccessService accessService;
	private final SearchService searchService;
	private final DataAccessor dataAccessor;

	@Inject
	public CommitService(UserService userService, AccessService accessService, SearchService searchService,
			DataAccessor dataAccessor) {
		this.userService = userService;
		this.searchService = searchService;
		this.accessService = accessService;
		this.dataAccessor = dataAccessor;
	}

	public String put(Repository repo, InputStream data) {
		if (!accessService.canWrite(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "WRITE");
		Path dir = null;
		ModelStreamReader reader = null;
		try {
			reader = new ModelStreamReader(data);
			String commitId = UUID.randomUUID().toString();
			write(repo, commitId, reader);
			return commitId;
		} catch (IOException e) {
			log.error("Error reading commit data", e);
			return null;
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (dir != null && dir.toFile().exists())
					Directories.delete(dir.toFile());
			} catch (IOException e) {
				log.error("Error closing reader and deleting files", e);
			}
		}
	}

	private void write(Repository repo, String commitId, ModelStreamReader reader) throws IOException {
		Commit commit = writeCommit(repo, commitId, reader.readNextPartAsString());
		List<Dataset> datasets = writeDatasets(repo, commit, reader);
		writeReferences(repo, commitId, datasets);
	}

	private Commit writeCommit(Repository repo, String commitId, String commitMessage) {
		String username = userService.getCurrentUser().username;
		long timestamp = Calendar.getInstance().getTimeInMillis();
		Commit commit = new Commit();
		commit.id = commitId;
		commit.message = commitMessage.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
		commit.user = username;
		commit.timestamp = timestamp;
		File historyFile = repo.getHistoryFile(true);
		dataAccessor.appendToHistory(historyFile, commit);
		return commit;
	}

	private List<Dataset> writeDatasets(Repository repo, Commit commit, ModelStreamReader reader) throws IOException {
		List<Dataset> datasets = new ArrayList<>();
		while (reader.hasMore()) {
			Dataset dataset = reader.readNextPartAsDataset();
			datasets.add(dataset);
			ModelType type = dataset.type;
			String refId = dataset.refId;
			File file = repo.getDatasetFile(type, refId, commit.id, true);
			boolean hadData = false;
			try (OutputStream out = new FileOutputStream(file)) {
				hadData = reader.readNextPartToStream(out);
			}
			if (!hadData)
				continue;
			File binDir = repo.getBinDir(type, refId, commit.id, false);
			int count = 0;
			int noOfFiles = reader.readNextInt();
			while (count++ < noOfFiles) {
				String path = reader.readNextPartAsString();
				File binFile = new File(binDir, path);
				binFile.getParentFile().mkdirs();
				try (OutputStream out = new FileOutputStream(binFile)) {
					reader.readNextPartToStream(out);
				}
			}
		}
		index(repo, datasets, commit);
		return datasets;
	}

	private void index(Repository repo, List<Dataset> datasets, Commit commit) {
		List<DatasetIndexEntry> entries = new ArrayList<>();
		for (Dataset dataset : datasets) {
			DatasetIndexEntry entry = new DatasetIndexEntry();
			entry.repositoryId = repo.toId();
			entry.type = dataset.type;
			entry.refId = dataset.refId;
			entry.name = dataset.name;
			entry.categoryRefId = dataset.categoryRefId;
			entry.fullPath = dataset.fullPath;
			entry.categoryType = dataset.categoryType;
			entry.commitId = commit.id;
			entry.commitMessage = commit.message;
			entries.add(entry);
		}
		searchService.index(entries);
	}

	private void writeReferences(Repository repo, String commitId, List<Dataset> datasets) throws IOException {
		File file = repo.getCommitFile(commitId, true);
		String json = new Gson().toJson(datasets);
		Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE);
	}

}
