package com.greendelta.cloud.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.openlca.cloud.api.data.CommitReader;
import org.openlca.cloud.model.data.CommitDescriptor;
import org.openlca.cloud.model.data.DatasetDescriptor;
import org.openlca.cloud.util.Directories;
import org.openlca.cloud.util.Strings;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

public class CommitService {

	private final static Logger log = LoggerFactory
			.getLogger(CommitService.class);
	private final static Charset charset = Charset.forName("utf-8");
	private final UserService userService;
	private final RepositoryService repositoryService;

	@Inject
	public CommitService(UserService userService,
			RepositoryService repositoryService) {
		this.userService = userService;
		this.repositoryService = repositoryService;
	}

	public String put(String repositoryId, InputStream data) {
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
				writeDataset(repositoryId, id, descriptor,
						reader.getData(descriptor));
			String username = userService.getCurrentUser().getName();
			long timestamp = Calendar.getInstance().getTimeInMillis();
			CommitDescriptor descriptor = new CommitDescriptor();
			descriptor.setId(id);
			descriptor.setMessage(reader.getCommitMessage());
			descriptor.setUser(username);
			descriptor.setTimestamp(timestamp);
			appendToHistory(repositoryId, descriptor);
			writeReferences(repositoryId, id, descriptors);
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

	private void writeDataset(String repositoryId, String commitId,
			DatasetDescriptor descriptor, String data) {
		File file = getOrCreateDatasetfile(repositoryId, commitId, descriptor);
		try {
			if (data == null)
				file.createNewFile();
			else
				Files.write(file.toPath(), data.getBytes(charset));
		} catch (IOException e) {
			log.error(
					Strings.concat("Error writing json data to file ",
							file.getAbsolutePath()), e);
		}
	}

	private File getOrCreateDatasetfile(String repositoryId, String commitId,
			DatasetDescriptor descriptor) {
		Repository repository = repositoryService.getForId(repositoryId);
		File datasetDirectory = repository.getDatasetDirectory(
				descriptor.getType(), descriptor.getRefId());
		if (!datasetDirectory.exists())
			datasetDirectory.mkdir();
		return repository.getDatasetFile(descriptor.getType(),
				descriptor.getRefId(), commitId);
	}

	private void writeReferences(String repositoryId, String commitId,
			List<DatasetDescriptor> descriptors) throws IOException {
		File commitFile = repositoryService.getForId(repositoryId)
				.getCommitFile(commitId);
		String json = new Gson().toJson(descriptors);
		Files.write(commitFile.toPath(), json.getBytes(),
				StandardOpenOption.CREATE_NEW);
	}

	public String getData(String repositoryId, ModelType type, String refId,
			String commitId) {
		File file = repositoryService.getForId(repositoryId).getDatasetFile(
				type, refId, commitId);
		return read(file);
	}

	private String read(File file) {
		if (file == null)
			return null;
		if (!file.exists())
			return null;
		if (file.length() == 0)
			return "";
		try {
			byte[] jsonData = Files.readAllBytes(file.toPath());
			return new String(jsonData, charset);
		} catch (IOException e) {
			log.error(
					Strings.concat("Error reading json data from file ",
							file.getAbsolutePath()), e);
			return null;
		}
	}

	public CommitDescriptor getLatestCommit(String repositoryId) {
		List<CommitDescriptor> commits = getCommits(repositoryId);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public CommitDescriptor getLatestCommitForDataset(String repositoryId,
			ModelType type, String refId) {
		List<CommitDescriptor> commits = getCommitsForDataset(repositoryId,
				type, refId);
		if (commits.isEmpty())
			return null;
		return commits.get(commits.size() - 1);
	}

	public List<CommitDescriptor> getCommits(String repositoryId) {
		return getCommits(repositoryId, null);
	}

	public List<CommitDescriptor> getCommits(String repositoryId,
			String afterCommitId) {
		MutableBoolean reachedId = new MutableBoolean();
		return readHistory(repositoryId, (element) -> {
			if (element.getId().equals(afterCommitId)) {
				reachedId.value = true;
				return false;
			}
			if (!reachedId.value)
				return false;
			return true;
		});
	}

	public List<CommitDescriptor> getCommitsForDataset(String repositoryId,
			ModelType type, String refId) {
		return getCommitsForDataset(repositoryId, type, refId, null);
	}

	public List<CommitDescriptor> getCommitsForDataset(String repositoryId,
			ModelType type, String refId, String beforeCommitId) {
		MutableBoolean reachedId = new MutableBoolean();
		return readHistory(
				repositoryId,
				(element) -> {
					if (element.getId().equals(beforeCommitId))
						reachedId.value = true;
					if (reachedId.value)
						return false;
					for (DatasetDescriptor dataset : getReferences(
							repositoryId, element.getId())) {
						if (dataset.getType() != type)
							continue;
						if (!dataset.getRefId().equals(refId))
							continue;
						return true;
					}
					return false;
				});
	}

	public List<DatasetDescriptor> getReferences(String repositoryId,
			String commitId) {
		File commitFile = repositoryService.getForId(repositoryId)
				.getCommitFile(commitId);
		try {
			String json = new String(Files.readAllBytes(commitFile.toPath()),
					charset);
			return new Gson().fromJson(json,
					new TypeToken<List<DatasetDescriptor>>() {
					}.getType());
		} catch (IOException e) {
			log.error("Unexpected error while parsing commit history entry", e);
			return Collections.emptyList();
		}
	}

	private List<CommitDescriptor> readHistory(String repositoryId,
			Filter<CommitDescriptor> filter) {
		File historyFile = repositoryService.getForId(repositoryId)
				.getCommitHistoryFile();
		if (historyFile == null)
			return Collections.emptyList();
		if (!historyFile.exists())
			return Collections.emptyList();
		try {
			List<String> lines = Files.readAllLines(historyFile.toPath());
			if (lines.isEmpty())
				return Collections.emptyList();
			List<CommitDescriptor> descriptors = new ArrayList<>();
			for (String entry : lines) {
				if (entry.trim().isEmpty())
					continue;
				CommitDescriptor descriptor = CommitDescriptor.parse(entry);
				if (!filter.filter(descriptor))
					descriptors.add(descriptor);
			}
			return descriptors;

		} catch (IOException e) {
			log.error("Unexpected error appending to commit history", e);
			return Collections.emptyList();
		}
	}

	private void appendToHistory(String repositoryId, CommitDescriptor commit) {
		File historyFile = repositoryService.getForId(repositoryId)
				.getCommitHistoryFile();
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(historyFile, true)))) {
			out.println(commit.toString());
		} catch (IOException e) {
			log.error("Unexpected error appending to commit history", e);
		}
	}

	private interface Filter<T> {
		boolean filter(T element);
	}

	private class MutableBoolean {

		private boolean value;

	}

}
