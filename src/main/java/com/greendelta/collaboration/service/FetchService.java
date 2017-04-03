package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.StreamingOutput;

import org.openlca.cloud.api.data.ModelStream;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;

public class FetchService {

	private final HistoryService historyService;
	private final DataAccessor dataAccessor;

	@Inject
	public FetchService(HistoryService historyService, DataAccessor dataAccessor) {
		this.historyService = historyService;
		this.dataAccessor = dataAccessor;
	}

	public FetchRequestData toRequestData(Repository repo, String commitId, Dataset dataset) {
		FetchRequestData value = new FetchRequestData(dataset);
		ModelType type = dataset.type;
		String refId = dataset.refId;
		value.setDeleted(wasDeleted(repo, type, refId, commitId));
		value.setAdded(wasAdded(repo, type, refId, commitId));
		return value;
	}

	private boolean wasDeleted(Repository repo, ModelType type, String refId, String commitId) {
		String data = getDataset(repo, type, refId, commitId);
		if (data == null)
			return true;
		return data.isEmpty();
	}

	private boolean wasAdded(Repository repo, ModelType type, String refId, String commitId) {
		List<Commit> previous = historyService.getCommitsBefore(repo, type, refId, commitId);
		if (previous.isEmpty())
			return true;
		Commit commit = previous.get(previous.size() - 1);
		String previousData = getDataset(repo, type, refId, commit.id);
		if (previousData == null)
			return true;
		return previousData.isEmpty();
	}

	public boolean hasDataset(Repository repo, ModelType type, String refId, String commitId) {
		return repo.getDatasetFile(type, refId, commitId, false).exists();
	}

	public String getDataset(Repository repo, ModelType type, String refId, String commitId) {
		File file = repo.getDatasetFile(type, refId, commitId, false);
		byte[] data = dataAccessor.read(file);
		if (data == null)
			return null;
		return new String(data, Charset.forName("utf-8"));
	}

	public File getBinDir(Repository repo, ModelType type, String refId, String commitId) {
		return repo.getBinDir(type, refId, commitId, false);
	}

	public StreamingOutput prepareData(Repository repo, List<FileReference> requested, List<Commit> commits) {
		return prepareData(repo, requested, commits, false);
	}

	public StreamingOutput prepareData(Repository repo, List<Commit> commits) {
		return prepareData(repo, null, commits, true);
	}

	private StreamingOutput prepareData(Repository repo, List<FileReference> requested, List<Commit> commits,
			boolean skipEmpty) {
		Collections.reverse(commits);
		Set<Dataset> empty = new HashSet<>();
		Set<Dataset> datasets = new HashSet<>();
		Map<Dataset, String> dsToCommit = new HashMap<>();
		for (Commit commit : commits) {
			for (Dataset dataset : historyService.getReferences(repo, commit.id)) {
				if (requested != null && !requested.contains(dataset.asFileReference()))
					continue;
				if (skipEmpty && empty.contains(dataset))
					continue;
				if (datasets.contains(dataset))
					continue;
				if (skipEmpty && !hasDataset(repo, dataset.type, dataset.refId, commit.id)) {
					empty.add(dataset);
					continue;
				}
				dsToCommit.put(dataset, commit.id);
				datasets.add(dataset);
			}
		}
		if (datasets.isEmpty())
			return null;
		return new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				int read = -1;
				String commitId = commits.get(0).id;
				try (FetchStream stream = new FetchStream(repo, commitId, datasets, dsToCommit)) {
					while ((read = stream.read()) != -1) {
						output.write(read);
					}
				}
			}
		};

	}

	private class FetchStream extends ModelStream {

		private final Repository repo;
		private final Map<Dataset, String> dsToCommitId;

		private FetchStream(Repository repo, String commitId, Set<Dataset> datasets, Map<Dataset, String> dsToCommitId) {
			super(commitId, datasets);
			this.repo = repo;
			this.dsToCommitId = dsToCommitId;
		}

		@Override
		protected byte[] getData(Dataset dataset) throws IOException {
			String data = getDataset(repo, dataset.type, dataset.refId, dsToCommitId.get(dataset));
			if (data == null)
				return new byte[0];
			return data.getBytes(ModelStream.CHARSET);
		}

		@Override
		protected File getBinaryFilesLocation(Dataset dataset) {
			return getBinDir(repo, dataset.type, dataset.refId, dsToCommitId.get(dataset));
		}
	}
}
