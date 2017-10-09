package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.StreamingOutput;

import org.openlca.cloud.api.data.ModelStream;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;

public class FetchService {

	private final SearchService searchService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public FetchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public boolean hasDataset(Repository repo, ModelType type, String refId, String commitId) {
		File file = repo.getDatasetFile(type, refId, commitId, false);
		if (!file.exists())
			return false;
		try {
			long size = Files.size(file.toPath());
			return size > 0;
		} catch (IOException e) {
			return false;
		}
	}

	public String getDataset(Repository repo, ModelType type, String refId, String commitId) {
		File file = repo.getDatasetFile(type, refId, commitId, false);
		byte[] data = dataAccessor.read(file);
		if (data == null || data.length == 0)
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
		Set<FileReference> empty = new HashSet<>();
		Set<Dataset> datasets = new HashSet<>();
		Map<Dataset, String> dsToCommit = new HashMap<>();
		for (Commit commit : commits) {
			for (IndexEntry entry : searchService.getAll(repo, commit)) {
				if (requested != null && !requested.contains(entry))
					continue;
				if (skipEmpty && empty.contains(entry))
					continue;
				if (datasets.contains(entry))
					continue;
				if (skipEmpty && entry.action == IndexAction.DELETE) {
					empty.add(entry);
					continue;
				}
				dsToCommit.put(entry, commit.id);
				datasets.add(entry);
			}
		}
		return new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				int read = -1;
				String commitId = commits.get(commits.size() - 1).id;
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
