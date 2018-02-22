package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.data.ModelStreamReader;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.search.IndexEntryCreator;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Bytes;

public class CommitService {

	private final static Logger log = LogManager.getLogger(CommitService.class);

	private final UserService userService;
	private final AccessService accessService;
	private final SearchService searchService;

	@Inject
	public CommitService(UserService userService, AccessService accessService, SearchService searchService) {
		this.userService = userService;
		this.searchService = searchService;
		this.accessService = accessService;
	}

	public Commit put(Repository repo, InputStream data) {
		if (!accessService.canWrite(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "WRITE");
		try (ModelStreamReader reader = new ModelStreamReader(data)) {
			return write(repo, reader);
		} catch (IOException e) {
			log.error("Error handling commit data", e);
			return null;
		}
	}

	private Commit write(Repository repo, ModelStreamReader reader) throws IOException {
		Commit commit = createCommit(repo, reader.readNextPartAsString());
		DatasetWriter writer = new DatasetWriter(repo, commit, reader);
		writer.writeDatasets();
		File historyFile = repo.getHistoryFile(true);
		Bytes.appendTo(historyFile, commit.toString());
		return commit;
	}

	private Commit createCommit(Repository repo, String commitMessage) {
		String username = userService.getCurrentUser().username;
		long timestamp = Calendar.getInstance().getTimeInMillis();
		Commit commit = new Commit();
		commit.id = UUID.randomUUID().toString();
		commit.message = commitMessage.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
		commit.user = username;
		commit.timestamp = timestamp;
		return commit;
	}

	public class InsufficientStorageException extends RuntimeException {

		private static final long serialVersionUID = 543921197834005033L;

		private InsufficientStorageException(String message) {
			super(message);
		}

	}

	private class DatasetWriter {

		private final Repository repo;
		private final Commit commit;
		private final ModelStreamReader reader;
		private final List<Dataset> datasets = new ArrayList<>();
		private final List<IndexEntry> indexEntries = new ArrayList<>();
		private final IndexEntryCreator indexEntryCreator;
		private final boolean isOwnNamespace;
		private final User user;
		private long currentRepoSize;
		private long currentUserGroupSize;

		private DatasetWriter(Repository repo, Commit commit, ModelStreamReader reader) {
			this.repo = repo;
			this.commit = commit;
			this.reader = reader;
			this.indexEntryCreator = new IndexEntryCreator(repo, commit);
			this.currentRepoSize = repo.getSize();
			this.user = userService.getCurrentUser();
			this.isOwnNamespace = accessService.isOwnNamespace(user, repo.toId());
			this.currentUserGroupSize = isOwnNamespace ? userService.getUserGroupSize() : 0;
		}

		private void writeDatasets() throws IOException {
			try {
				while (reader.hasMore()) {
					Dataset dataset = reader.readNextPartAsDataset();
					datasets.add(dataset);
					File file = repo.getDatasetFile(dataset.type, dataset.refId, commit.id, true);
					if (!write(dataset, file)) {
						indexEntries.add(indexEntryCreator.create(dataset));
						continue;
					}
					createIndexEntry(dataset, file);
					writeBinaries(dataset);
				}
				searchService.index(repo.toId(), indexEntries);
				repo.updateSize(currentRepoSize);
			} catch (Exception e) {
				cleanup(repo, datasets, commit);
				throw e;
			}
		}

		private boolean write(Dataset dataset, File file) throws IOException {
			int size = 0;
			try (OutputStream out = new FileOutputStream(file)) {
				size = reader.readNextPartToStream(out);
				out.close();
			}
			if (size == 0)
				return false;
			currentRepoSize += size;
			currentUserGroupSize += size;
			checkSize();
			return true;
		}

		private void createIndexEntry(Dataset dataset, File file) {
			IndexAction lastAction = searchService.getMostRecentAction(repo.toId(), dataset.refId);
			Map<String, Object> data = new HashMap<>();
			if (dataset.type == ModelType.PROCESS || dataset.type == ModelType.FLOW)
				data = IndexEntryCreator.readData(file);
			indexEntries.add(indexEntryCreator.create(dataset, lastAction, data));
		}

		private void checkSize() {
			if (repo.settings.maxSize > 0 && currentRepoSize > repo.settings.maxSize)
				throw new InsufficientStorageException("Insufficient storage in repository");
			if (!isOwnNamespace)
				return;
			if (user.settings.maxSize > 0 && currentUserGroupSize > user.settings.maxSize)
				throw new InsufficientStorageException("Insufficient storage in user group");
		}

		private void writeBinaries(Dataset dataset) throws IOException {
			File binDir = repo.getBinDir(dataset.type, dataset.refId, commit.id, false);
			int count = 0;
			int noOfFiles = reader.readNextInt();
			while (count++ < noOfFiles) {
				String path = reader.readNextPartAsString();
				File binFile = new File(binDir, path);
				binFile.getParentFile().mkdirs();
				try (OutputStream out = new FileOutputStream(binFile)) {
					int size = reader.readNextPartToStream(out);
					out.close();
					currentRepoSize += size;
					currentUserGroupSize += size;
				}
				checkSize();
			}
		}

		private void cleanup(Repository repo, List<Dataset> datasets, Commit commit) {
			for (Dataset dataset : datasets) {
				File file = repo.getDatasetFile(dataset.type, dataset.refId, commit.id, false);
				if (file.exists()) {
					file.delete();
				}
				while (!(file = file.getParentFile()).equals(repo.repoDir) && file.listFiles().length == 0) {
					file.delete();
				}
				file = repo.getBinDir(dataset.type, dataset.refId, commit.id, false);
				if (file.exists()) {
					Directories.delete(file);
					while (!(file = file.getParentFile()).equals(repo.repoDir) && file.listFiles().length == 0) {
						file.delete();
					}
				}
			}
		}
	}

}
