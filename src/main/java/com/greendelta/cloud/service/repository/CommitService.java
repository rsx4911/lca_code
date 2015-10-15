package com.greendelta.cloud.service.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.cloud.api.data.CommitReader;
import com.greendelta.cloud.index.CommitIndexer;
import com.greendelta.cloud.model.data.CommitDescriptor;
import com.greendelta.cloud.model.data.DatasetDescriptor;
import com.greendelta.cloud.model.data.FileReference;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Directories;

public class CommitService {

	private final static Logger log = LoggerFactory
			.getLogger(CommitService.class);
	private final DatasetService datasetService;
	private final UserService userService;
	private final RepositoryService repositoryService;

	@Inject
	public CommitService(DatasetService datasetService,
			UserService userService, RepositoryService repositoryService) {
		this.datasetService = datasetService;
		this.userService = userService;
		this.repositoryService = repositoryService;
	}

	public String push(String repositoryId, InputStream data) {
		Path dir = null;
		CommitReader reader = null;
		try {
			dir = Files.createTempDirectory("commitReader");
			File zipFile = new File(dir.toFile(), "commit.zip");
			Files.copy(data, zipFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			reader = new CommitReader(zipFile);
			String id = UUID.randomUUID().toString();
			List<DatasetDescriptor> descriptors = reader.getDescriptors();
			for (DatasetDescriptor descriptor : descriptors)
				datasetService.put(repositoryId, id, descriptor,
						reader.getData(descriptor));
			String username = userService.getCurrentUser().getName();
			long timestamp = Calendar.getInstance().getTimeInMillis();
			CommitDescriptor descriptor = new CommitDescriptor();
			descriptor.setId(id);
			descriptor.setMessage(reader.getCommitMessage());
			descriptor.setUser(username);
			descriptor.setTimestamp(timestamp);
			appendCommitToFile(repositoryId, descriptor);
			getIndexer(repositoryId).index(id, convert(descriptors));
			return id;
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

	private List<FileReference> convert(List<DatasetDescriptor> descriptors) {
		List<FileReference> references = new ArrayList<>();
		for (DatasetDescriptor descriptor : descriptors)
			references.add(convert(descriptor));
		return references;
	}

	private FileReference convert(DatasetDescriptor descriptor) {
		FileReference reference = new FileReference();
		reference.setType(descriptor.getType());
		reference.setRefId(descriptor.getRefId());
		return reference;
	}

	public CommitIndexer getIndexer(String repositoryId) {
		File indexDirectory = repositoryService.getForId(repositoryId)
				.getCommitIndexDirectory();
		return new CommitIndexer(indexDirectory);
	}

	public void streamIndex(String repositoryId, OutputStream stream)
			throws IOException {
		File indexDirectory = repositoryService.getForId(repositoryId)
				.getCommitIndexDirectory();
		Directories.streamZipped(indexDirectory, stream);
	}

	public List<CommitDescriptor> getCommitHistory(String repositoryId) {
		return getCommitHistory(repositoryId, null);
	}

	public List<CommitDescriptor> getCommitHistory(String repositoryId,
			String afterCommitId) {
		List<CommitDescriptor> history = new ArrayList<>();
		List<String> historyEntries = readCommitHistory(repositoryId);
		boolean afterCommit = afterCommitId == null;
		for (String entry : historyEntries) {
			if (entry.trim().isEmpty())
				continue;
			CommitDescriptor descriptor = CommitDescriptor.parse(entry);
			if (!afterCommit) {
				afterCommit = descriptor.getId().equals(afterCommitId);
				continue;
			}
			history.add(descriptor);
		}
		return history;
	}

	public List<CommitDescriptor> getCommitHistoryForDataset(
			String repositoryId, ModelType type, String refId) {
		return getCommitHistoryForDataset(repositoryId, type, refId, null);
	}

	public List<CommitDescriptor> getCommitHistoryForDataset(
			String repositoryId, ModelType type, String refId,
			String beforeCommitId) {
		List<CommitDescriptor> history = new ArrayList<>();
		List<String> historyEntries = readCommitHistory(repositoryId);
		for (String entry : historyEntries) {
			if (entry.trim().isEmpty())
				continue;
			CommitDescriptor descriptor = CommitDescriptor.parse(entry);
			if (descriptor.getId().equals(beforeCommitId))
				break;
			boolean found = false;
			for (FileReference reference : getModifiedFiles(repositoryId,
					descriptor.getId())) {
				if (reference.getType() != type)
					continue;
				if (!reference.getRefId().equals(refId))
					continue;
				found = true;
				break;
			}
			if (found)
				history.add(descriptor);
		}
		return history;
	}

	public CommitDescriptor getLatestCommit(String repositoryId) {
		List<CommitDescriptor> entries = getCommitHistory(repositoryId);
		if (entries.isEmpty())
			return null;
		return entries.get(entries.size() - 1);
	}

	public String getLatestCommitId(String repositoryId, ModelType type,
			String refId) {
		List<CommitDescriptor> commits = getCommitHistoryForDataset(
				repositoryId, type, refId);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1).getId();
	}

	public List<FileReference> getModifiedFiles(String repositoryId,
			String commitId) {
		return getIndexer(repositoryId).get(commitId);
	}

	private List<String> readCommitHistory(String repositoryId) {
		List<String> historyEntries = new ArrayList<>();
		File commitFile = repositoryService.getForId(repositoryId)
				.getCommitHistoryFile();
		if (commitFile == null)
			return Collections.emptyList();
		if (!commitFile.exists())
			return Collections.emptyList();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				commitFile))) {
			String line = null;
			while ((line = reader.readLine()) != null)
				historyEntries.add(line);
		} catch (IOException e) {
			log.error("Unexpected error reading commit history", e);
		}
		return historyEntries;
	}

	private void appendCommitToFile(String repositoryId, CommitDescriptor commit) {
		File commitFile = repositoryService.getForId(repositoryId)
				.getCommitHistoryFile();
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(commitFile, true)))) {
			out.println(commit.toString());
		} catch (IOException e) {
			log.error("Unexpected error appending to commit history", e);
		}
	}

}
