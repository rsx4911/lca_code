package com.greendelta.cloud.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

public class FetchService {

	private final static Logger log = LoggerFactory
			.getLogger(FetchService.class);
	private final static Charset charset = Charset.forName("utf-8");
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final DataAccessor dataAccessor = new DataAccessor();

	@Inject
	public FetchService(RepositoryService repoService,
			HistoryService historyService) {
		this.repoService = repoService;
		this.historyService = historyService;
	}

	public FetchRequestData toRequestData(String repoId, String commitId,
			Dataset descriptor) {
		FetchRequestData value = new FetchRequestData(descriptor);
		ModelType type = descriptor.getType();
		String refId = descriptor.getRefId();
		value.setDeleted(wasDeleted(repoId, type, refId, commitId));
		value.setAdded(wasAdded(repoId, type, refId, commitId));
		return value;
	}

	private boolean wasDeleted(String repoId, ModelType type, String refId,
			String commitId) {
		String data = getDataset(repoId, type, refId, commitId);
		if (data == null)
			return true;
		return data.isEmpty();
	}

	private boolean wasAdded(String repoId, ModelType type, String refId,
			String commitId) {
		List<Commit> previous = historyService.getCommits(repoId, type, refId,
				commitId);
		if (previous.isEmpty())
			return true;
		Commit commit = previous.get(previous.size() - 1);
		String previousData = getDataset(repoId, type, refId, commit.getId());
		if (previousData == null)
			return true;
		return previousData.isEmpty();
	}

	public String getDataset(String repoId, ModelType type, String refId,
			String commitId) {
		Repository repo = repoService.getForId(repoId);
		File file = repo.getDatasetFile(type, refId, commitId, false);
		return dataAccessor.readDataset(file);
	}

	public File getBinDir(String repoId, ModelType type, String refId,
			String commitId) {
		Repository repo = repoService.getForId(repoId);
		return repo.getBinDir(type, refId, commitId, false);
	}

	public List<Dataset> getReferences(String repoId, String commitId) {
		Repository repo = repoService.getForId(repoId);
		File file = repo.getCommitFile(commitId, false);
		try {
			String json = new String(Files.readAllBytes(file.toPath()), charset);
			return new Gson().fromJson(json, new TypeToken<List<Dataset>>() {
			}.getType());
		} catch (IOException e) {
			log.error("Unexpected error while parsing commit history entry", e);
			return Collections.emptyList();
		}
	}

}
