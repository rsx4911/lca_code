package com.greendelta.collaboration.util.export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.openlca.cloud.model.data.Commit;
import org.openlca.convert.jsonld.ilcd.Json2IlcdStore;
import org.openlca.convert.jsonld.ilcd.JsonStore;
import org.openlca.core.model.ModelType;
import org.openlca.ilcd.io.DataStore;
import org.openlca.ilcd.io.ZipStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SearchService;

public class IlcdWriter implements DatasetWriter {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final FetchService fetchService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final Repository repo;
	private final JsonStore jsonStore;
	private final DataStore ilcdStore;
	private final Json2IlcdStore converter;
	private final Gson gson = new Gson();
	private final File tmpFile;
	private String currentCommitId;
	private Set<Ref> collectedRefs;

	public IlcdWriter(FetchService fetchService, HistoryService historyService, SearchService searchService,
			Repository repo) throws IOException {
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.repo = repo;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, UUID.randomUUID().toString() + ".zip");
		this.ilcdStore = new ZipStore(tmpFile);
		this.jsonStore = new JsonStoreImpl();
		this.converter = new Json2IlcdStore(this.jsonStore, ilcdStore, this::collectRefs);
	}

	@Override
	public void write(ModelType type, String refId, String commitId) throws IOException {
		this.currentCommitId = commitId;
		this.collectedRefs = new HashSet<>();
		if (!fetchService.hasDataset(repo, type, refId, currentCommitId)) {
			Commit lastCommit = historyService.getLastCommitBefore(repo, type, refId, currentCommitId);
			if (lastCommit == null)
				return;
			this.currentCommitId = lastCommit.id;
		}
		JsonObject obj = jsonStore.get(type.getModelClass().getSimpleName(), refId);
		if (obj == null)
			return;
		converter.convertAndPut(obj);
		for (Ref ref : new ArrayList<>(collectedRefs)) {
			write(ref.modelType, ref.refId, ref.commitId);
		}
	}

	@Override
	public File close() throws IOException {
		ilcdStore.close();
		return tmpFile;
	}

	private void collectRefs(String type, String refId) {
		collectedRefs.add(new Ref(getType(type), refId, currentCommitId));
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
			String data = fetchService.getDataset(repo, modelType, refId, currentCommitId);
			if (data != null)
				return gson.fromJson(data, JsonObject.class);
			Commit lastCommit = historyService.getLastCommitBefore(repo, modelType, refId, currentCommitId);
			if (lastCommit != null) {
				data = fetchService.getDataset(repo, modelType, refId, currentCommitId);
			}
			return gson.fromJson(data, JsonObject.class);
		}

		@Override
		public byte[] getExternalFile(String sourceRefId, String filename) {
			File binDir = fetchService.getBinDir(repo, ModelType.SOURCE, sourceRefId, currentCommitId);
			if (!binDir.exists())
				return null;
			File file = new File(binDir, filename);
			if (!file.exists())
				return null;
			try {
				return Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				log.error("Error reading bin file", e);
				return null;
			}
		}

		@Override
		public List<JsonObject> getGlobalParameters() {
			List<JsonObject> parameters = new ArrayList<>();
			List<IndexEntry> entries = searchService.getAll(repo, ModelType.PARAMETER);
			Set<String> added = new HashSet<>();
			List<Commit> commits = historyService.getCommitsUntil(repo, currentCommitId);
			List<IndexEntry> filtered = new ArrayList<>();
			for (IndexEntry entry : entries) {
				for (Commit commit : commits) {
					if (entry.commitId.equals(commit.id)) {
						filtered.add(entry);
					}
				}
			}
			Collections.sort(filtered, new EntryComparator(commits));
			for (IndexEntry entry : filtered) {
				if (added.contains(entry.refId))
					continue;
				String data = fetchService.getDataset(repo, entry.type, entry.refId, entry.commitId);
				if (data == null)
					continue;
				parameters.add(gson.fromJson(data, JsonObject.class));
				added.add(entry.refId);
			}
			return parameters;
		}

	}

	private class EntryComparator implements Comparator<IndexEntry> {

		private Map<String, Integer> commitOrder = new HashMap<>();

		private EntryComparator(List<Commit> commits) {
			int count = 1;
			for (Commit commit : commits) {
				commitOrder.put(commit.id, count++);
			}
		}

		@Override
		public int compare(IndexEntry o1, IndexEntry o2) {
			return commitOrder.get(o2.commitId) - commitOrder.get(o1.commitId);
		}

	}

	private class Ref {

		private ModelType modelType;
		private String refId;
		private String commitId;

		private Ref(ModelType modelType, String refId, String commitId) {
			this.modelType = modelType;
			this.refId = refId;
			this.commitId = commitId;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof Ref))
				return false;
			Ref other = (Ref) obj;
			if (other.modelType != modelType)
				return false;
			if (refId == null)
				return other.refId == null;
			return other.refId.equals(refId);
		}

	}

}
