package com.greendelta.collaboration.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.convert.jsonld.ilcd.Json2IlcdStore;
import org.openlca.convert.jsonld.ilcd.JsonStore;
import org.openlca.core.model.ModelType;
import org.openlca.ilcd.io.DataStore;
import org.openlca.ilcd.io.ZipStore;
import org.openlca.util.BinUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.Collections;

public class IlcdWriter implements DatasetWriter {

	private final Logger log = LogManager.getLogger(getClass());
	private final FetchService fetchService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final Repository repo;
	private final JsonStore jsonStore;
	private final DataStore ilcdStore;
	private final Json2IlcdStore converter;
	private final Gson gson = new Gson();
	private final File tmpFile;
	private final String commitId;
	private Set<FileReference> collectedRefs;

	public IlcdWriter(FetchService fetchService, HistoryService historyService, SearchService searchService,
			Repository repo, String commitId) throws IOException {
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.repo = repo;
		this.commitId = commitId;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, UUID.randomUUID().toString() + ".zip");
		this.ilcdStore = new ZipStore(tmpFile);
		this.jsonStore = new JsonStoreImpl();
		this.converter = new Json2IlcdStore(this.jsonStore, ilcdStore, this::collectRefs);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		this.collectedRefs = new HashSet<>();
		IndexEntry entry = searchService.get(repo, refId, commitId);
		boolean exists = entry != null && entry.action != IndexAction.DELETE;
		if (!exists) {
			Commit lastCommit = historyService.getLastCommitBefore(repo, type, refId, commitId);
			if (lastCommit == null)
				return;
		}
		log.trace("Exporting {} {} to ilcd", type, refId);
		JsonObject obj = jsonStore.get(type.getModelClass().getSimpleName(), refId);
		if (obj == null)
			return;
		converter.convertAndPut(obj);
		for (FileReference ref : new ArrayList<>(collectedRefs)) {
			write(ref.type, ref.refId);
		}
	}

	@Override
	public File close() throws IOException {
		ilcdStore.close();
		return tmpFile;
	}

	private void collectRefs(String type, String refId) {
		FileReference ref = new FileReference();
		ref.type = getType(type);
		ref.refId = refId;
		collectedRefs.add(ref);
	}

	private ModelType getType(String type) {
		if (type == null)
			return null;
		for (ModelType t : ModelType.values()) {
			if (t.getModelClass() == null)
				continue;
			if (t.getModelClass().getSimpleName().equals(type))
				return t;
		}
		return null;
	}

	private class JsonStoreImpl implements JsonStore {

		@Override
		public JsonObject get(String type, String refId) {
			ModelType modelType = getType(type);
			String data = fetchService.getDataset(repo, modelType, refId, commitId);
			if (data != null)
				return gson.fromJson(data, JsonObject.class);
			Commit lastCommit = historyService.getLastCommitBefore(repo, modelType, refId, commitId);
			if (lastCommit == null)
				return null;
			data = fetchService.getDataset(repo, modelType, refId, lastCommit.id);
			if (data == null)
				return null;
			return gson.fromJson(data, JsonObject.class);
		}

		@Override
		public byte[] getExternalFile(String sourceRefId, String filename) {
			Commit lastCommit = historyService.getLastCommit(repo, ModelType.SOURCE, sourceRefId, commitId);			
			if (lastCommit == null)
				return null;
			File file = fetchService.getBinFile(repo, ModelType.SOURCE, sourceRefId, lastCommit.id, filename);
			if (!file.exists())
				return null;
			try {
				return BinUtils.gunzip(Files.readAllBytes(file.toPath()));
			} catch (IOException e) {
				log.error("Error reading bin file", e);
				return null;
			}
		}

		@Override
		public List<JsonObject> getGlobalParameters() {
			List<JsonObject> parameters = new ArrayList<>();
			List<IndexEntry> entries = searchService.getMostRecentUntil(repo, ModelType.PARAMETER, null, commitId);
			entries = Collections.filter(entries, entry -> entry.action == IndexAction.DELETE);
			for (IndexEntry entry : entries) {
				String data = fetchService.getDataset(repo, entry.type, entry.refId, entry.commitId);
				if (data == null)
					continue;
				parameters.add(gson.fromJson(data, JsonObject.class));
			}
			return parameters;
		}

	}

}
