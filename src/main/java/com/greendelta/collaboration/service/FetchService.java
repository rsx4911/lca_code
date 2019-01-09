package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.openlca.util.BinUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.Bytes;

public class FetchService {

	private static final Logger log = LoggerFactory.getLogger(FetchService.class);
	private final SearchService searchService;

	@Inject
	public FetchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public String getDataset(Repository repo, ModelType type, String refId, String commitId) {
		File file = repo.getDatasetFile(type, refId, commitId, false);
		try {
			byte[] data = BinUtils.gunzip(Bytes.read(file));
			if (data == null || data.length == 0)
				return null;
			return new String(data, Charset.forName("utf-8"));
		} catch (IOException e) {
			log.error("Error gunzipping data set", e);
			return null;
		}
	}

	public File getBinDir(Repository repo, ModelType type, String refId, String commitId) {
		return repo.getBinDir(type, refId, commitId, false);
	}

	public File getBinFile(Repository repo, ModelType type, String refId, String commitId, String filename) {
		return repo.getBinFile(type, refId, commitId, filename);
	}

	public StreamingOutput prepareData(Repository repo, List<Commit> commits, List<FileReference> requested) {
		Set<FileReference> added = new HashSet<>();
		Set<Dataset> datasets = new HashSet<>();
		Map<Dataset, String> dsToCommit = new HashMap<>();
		for (Commit commit : commits) {
			for (IndexEntry entry : searchService.getAll(repo, commit)) {
				FileReference ref = entry.asFileReference();
				if (requested != null && !requested.contains(ref))
					continue;
				Dataset ds = entry.asDataset();
				if (added.contains(ref))
					continue;
				dsToCommit.put(ds, commit.id);
				datasets.add(ds);
				added.add(ref);
			}
		}
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
			File file = repo.getDatasetFile(dataset.type, dataset.refId, dsToCommitId.get(dataset), false);
			return Bytes.read(file);
		}

		@Override
		protected File getBinaryFilesLocation(Dataset dataset) {
			return getBinDir(repo, dataset.type, dataset.refId, dsToCommitId.get(dataset));
		}

		@Override
		protected byte[] getBinaryData(Path file) throws IOException {
			return Files.readAllBytes(file);
		}
		
	}
}
