package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.data.ModelStream;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;
import org.openlca.util.BinUtils;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.Bytes;
import com.greendelta.collaboration.util.Collections;

public class FetchService {

	private static final Logger log = LogManager.getLogger(FetchService.class);
	private final SearchService searchService;
	private final HistoryService historyService;

	@Inject
	public FetchService(SearchService searchService, HistoryService historyService) {
		this.searchService = searchService;
		this.historyService = historyService;
	}

	public String getDataset(Repository repo, ModelType type, String refId, String commitId) {
		log.trace("Loading {} {} from repository {} (commit id {})", type.name(), refId, repo.toId(), commitId);
		File file = repo.getDatasetFile(type, refId, commitId, false);
		try {
			byte[] data = BinUtils.gunzip(Bytes.read(file));
			if (data == null || data.length == 0)
				return null;
			log.trace("Loaded {} bytes of data", data.length);
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

	public StreamingOutput prepareDataForDownload(Repository repo, List<FileReference> requested, Commit commit) {
		String commitId = commit != null ? commit.id : null;
		List<IndexEntry> entries = searchService.getMostRecentUntil(repo, commitId);
		if (requested != null && !requested.isEmpty()) {
			entries = Collections.filter(entries, entry -> !requested.contains(entry.asFileReference()));
		}
		if (commitId == null) {
			commitId = historyService.getLastCommit(repo).id;
		}
		return prepareData(repo, entries, commitId);
	}

	public StreamingOutput prepareDataForFetch(Repository repo, List<FileReference> requested, Commit commit) {
		String commitId = historyService.getLastCommit(repo).id;
		if (requested == null || requested.isEmpty())
			return prepareData(repo, new ArrayList<>(), commitId);
		List<IndexEntry> entries = searchService.getMostRecentAfter(repo, commit);
		entries = Collections.filter(entries, entry -> !requested.contains(entry.asFileReference()));
		return prepareData(repo, entries, commitId);
	}

	public StreamingOutput prepareData(Repository repo, List<IndexEntry> entries, String commitId) {
		log.debug("Starting to stream fetch data, total of {} data sets", entries.size());

		return new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				int read = -1;
				Map<String, String> commitIds = new HashMap<>();
				for (IndexEntry entry : entries) {
					commitIds.put(entry.refId, entry.commitId);
				}
				try (FetchStream stream = new FetchStream(repo, commitId, entries) {
					protected Map<String, String> getCommitIds() {
						return commitIds;
					}
				}) {
					while ((read = stream.read()) != -1) {
						output.write(read);
					}
				}
			}
		};
	}

	private abstract class FetchStream extends ModelStream {

		private final Repository repo;

		private FetchStream(Repository repo, String commitId, List<IndexEntry> entries) {
			super(commitId, Collections.convertToSet(entries, entry -> entry.asDataset()));
			this.repo = repo;
		}

		@Override
		protected byte[] getData(Dataset dataset) throws IOException {
			File file = repo.getDatasetFile(dataset.type, dataset.refId, getCommitIds().get(dataset.refId), false);
			log.trace("Loading data for {} {}", dataset.type, dataset.refId);
			return Bytes.read(file);
		}

		@Override
		protected File getBinaryFilesLocation(Dataset dataset) {
			return getBinDir(repo, dataset.type, dataset.refId, getCommitIds().get(dataset.refId));
		}

		@Override
		protected byte[] getBinaryData(Path file) throws IOException {
			return Files.readAllBytes(file);
		}

		protected abstract Map<String, String> getCommitIds();

	}
}
