package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openlca.cloud.api.data.ModelStreamReader;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
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
			log.error("Error reading commit data", e);
			return null;
		}
	}

	private Commit write(Repository repo, ModelStreamReader reader) throws IOException {
		Commit commit = createCommit(repo, reader.readNextPartAsString());
		writeDatasets(repo, commit, reader);
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

	private List<Dataset> writeDatasets(Repository repo, Commit commit, ModelStreamReader reader) throws IOException {
		List<Dataset> datasets = new ArrayList<>();
		List<IndexEntry> indexEntries = new ArrayList<>();
		IndexEntryCreator indexEntryCreator = new IndexEntryCreator(repo, commit);
		while (reader.hasMore()) {
			Dataset dataset = reader.readNextPartAsDataset();
			datasets.add(dataset);
			File file = repo.getDatasetFile(dataset.type, dataset.refId, commit.id, true);
			boolean hadData = false;
			try (OutputStream out = new FileOutputStream(file)) {
				hadData = reader.readNextPartToStream(out);
			}
			if (!hadData) {
				indexEntries.add(indexEntryCreator.create(dataset));
				continue;
			}
			IndexEntry last = searchService.getLast(repo, dataset.refId);
			indexEntries.add(indexEntryCreator.create(dataset, last, file));
			File binDir = repo.getBinDir(dataset.type, dataset.refId, commit.id, false);
			writeBinaries(reader, binDir);
		}
		searchService.index(indexEntries);
		return datasets;
	}

	private void writeBinaries(ModelStreamReader reader, File binDir) throws IOException {
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

}
