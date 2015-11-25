package com.greendelta.cloud.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.openlca.cloud.api.data.CommitReader;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class CommitService {

	private final static Logger log = LoggerFactory
			.getLogger(CommitService.class);
	private final UserService userService;
	private final RepositoryService repoService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public CommitService(UserService userService, RepositoryService repoService) {
		this.userService = userService;
		this.repoService = repoService;
	}

	public String put(String repoId, InputStream data) {
		Path dir = null;
		CommitReader reader = null;
		try {
			dir = Files.createTempDirectory("commitReader");
			File zip = new File(dir.toFile(), "commit.zip");
			Files.copy(data, zip.toPath(), StandardCopyOption.REPLACE_EXISTING);
			reader = new CommitReader(zip);
			String commitId = UUID.randomUUID().toString();
			write(repoId, commitId, reader);
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

	private void write(String repoId, String commitId, CommitReader reader)
			throws IOException {
		Repository repo = repoService.getForId(repoId);
		writeDatasets(repo, commitId, reader);
		writeCommit(repo, commitId, reader);
		writeReferences(repo, commitId, reader.getDescriptors());
	}

	private void writeCommit(Repository repo, String commitId,
			CommitReader reader) {
		String username = userService.getCurrentUser().getName();
		long timestamp = Calendar.getInstance().getTimeInMillis();
		Commit descriptor = new Commit();
		descriptor.setId(commitId);
		descriptor.setMessage(reader.getCommitMessage());
		descriptor.setUser(username);
		descriptor.setTimestamp(timestamp);
		File historyFile = repo.getHistoryFile(true);
		dataAccessor.appendToHistory(historyFile, descriptor);
	}

	private void writeDatasets(Repository repo, String commitId,
			CommitReader reader) throws IOException {
		List<Dataset> descriptors = reader.getDescriptors();
		for (Dataset descriptor : descriptors) {
			ModelType type = descriptor.getType();
			String refId = descriptor.getRefId();
			File file = repo.getDatasetFile(type, refId, commitId, true);
			String data = reader.getData(descriptor);
			dataAccessor.writeDataset(file, data);
			File binDir = repo.getBinDir(type, refId, commitId, false);
			reader.copyBinaries(descriptor, binDir);
		}
	}

	private void writeReferences(Repository repo, String commitId,
			List<Dataset> descriptors) throws IOException {
		File file = repo.getCommitFile(commitId, true);
		String json = new Gson().toJson(descriptors);
		Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE);
	}

}
