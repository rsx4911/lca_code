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
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.model.User;

public class CommitService {

	private final static Logger log = LoggerFactory
			.getLogger(CommitService.class);

	private final UserService userService;
	private final AccessService accessService;
	private final RepositoryIndices repositoryIndices;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public CommitService(UserService userService, RepositoryIndices repositoryIndices, AccessService accessService) {
		this.userService = userService;
		this.repositoryIndices = repositoryIndices;
		this.accessService = accessService;
	}

	public String put(Repository repo, InputStream data) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canWrite(currentUser, repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "WRITE");
		Path dir = null;
		CommitReader reader = null;
		try {
			dir = Files.createTempDirectory("commitReader");
			File zip = new File(dir.toFile(), "commit.zip");
			Files.copy(data, zip.toPath(), StandardCopyOption.REPLACE_EXISTING);
			reader = new CommitReader(zip);
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

	private void write(Repository repo, String commitId, CommitReader reader)
			throws IOException {
		Commit commit = writeCommit(repo, commitId, reader);
		writeDatasets(repo, commit, reader);
		writeReferences(repo, commitId, reader.getDescriptors());
	}

	private Commit writeCommit(Repository repo, String commitId,
			CommitReader reader) {
		String username = userService.getCurrentUser().username;
		long timestamp = Calendar.getInstance().getTimeInMillis();
		Commit commit = new Commit();
		commit.id = commitId;
		commit.message = reader.getCommitMessage();
		commit.user = username;
		commit.timestamp = timestamp;
		File historyFile = repo.getHistoryFile(true);
		dataAccessor.appendToHistory(historyFile, commit);
		return commit;
	}

	private void writeDatasets(Repository repo, Commit commit,
			CommitReader reader) throws IOException {
		List<Dataset> datasets = reader.getDescriptors();
		DatasetIndex index = repositoryIndices.get(repo);
		for (Dataset dataset : datasets) {
			ModelType type = dataset.type;
			String refId = dataset.refId;
			File file = repo.getDatasetFile(type, refId, commit.id, true);
			String data = reader.getData(dataset);
			dataAccessor.writeDataset(file, data);
			File binDir = repo.getBinDir(type, refId, commit.id, false);
			reader.copyBinaries(dataset, binDir);
		}
		index.index(datasets, commit);
	}

	private void writeReferences(Repository repo, String commitId,
			List<Dataset> datasets) throws IOException {
		File file = repo.getCommitFile(commitId, true);
		String json = new Gson().toJson(datasets);
		Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE);
	}

}
