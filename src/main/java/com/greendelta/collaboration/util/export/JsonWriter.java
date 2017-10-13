package com.greendelta.collaboration.util.export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.ZipStore;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SearchService;

public class JsonWriter implements DatasetWriter {

	private final FetchService fetchService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final Repository repo;
	private final ZipStore zipStore;
	private final File tmpFile;
	private final Set<String> written = new HashSet<>();

	public JsonWriter(FetchService fetchService, HistoryService historyService, SearchService searchService,
			Repository repo) throws IOException {
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.repo = repo;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.zipStore = ZipStore.open(tmpFile);
	}

	@Override
	public void write(ModelType type, String refId, String commitId) throws IOException {
		if (written.contains(type.name() + refId))
			return;
		String dataset = fetchService.getDataset(repo, type, refId, commitId);
		if (dataset == null)
			return;
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		zipStore.put(type, json);
		File binDir = fetchService.getBinDir(repo, type, refId, commitId);
		if (binDir.exists())
			for (File file : binDir.listFiles())
				zipStore.putBin(type, refId, file.getName(), Files.readAllBytes(file.toPath()));
		written.add(type.name() + refId);
		writeReferences(json, commitId);
		for (IndexEntry entry : getGlobalParameters(commitId)) {
			write(ModelType.PARAMETER, entry.refId, entry.commitId);
		}
	}

	private List<IndexEntry> getGlobalParameters(String untilCommitId) {
		Set<String> relevantCommits = new HashSet<>();
		for (Commit commit : historyService.getCommitsUntil(repo, untilCommitId)) {
			relevantCommits.add(commit.id);
		}
		List<IndexEntry> all = searchService.getAll(repo, ModelType.PARAMETER);
		List<IndexEntry> entries = new ArrayList<>();
		Set<String> added = new HashSet<>();
		for (IndexEntry entry : all) {
			if (added.contains(entry.refId))
				continue;
			if (!relevantCommits.contains(entry.commitId))
				continue;
			entries.add(entry);
			added.add(entry.refId);
		}
		return entries;
	}

	@Override
	public File close() throws IOException {
		zipStore.close();
		return tmpFile;
	}

	private void writeReferences(JsonObject object, String commitId) throws IOException {
		if (object == null)
			return;
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			JsonElement element = entry.getValue();
			if (element.isJsonArray()) {
				for (JsonElement child : element.getAsJsonArray())
					if (child.isJsonObject())
						writeReferences(child.getAsJsonObject(), commitId);
				continue;
			}
			if (!element.isJsonObject())
				continue;
			JsonObject child = element.getAsJsonObject();
			if (!(child.has("@type") && child.has("@id"))) {
				writeReferences(child, commitId);
				continue;
			}
			ModelType type = getType(child.get("@type").getAsString());
			if (type == null)
				continue;
			String id = child.get("@id").getAsString();
			if (written.contains(type.name() + id))
				continue;
			Commit commit = historyService.getLastCommit(repo, type, id, commitId);
			if (commit == null)
				continue;
			write(type, id, commit.id);
		}
	}

	private ModelType getType(String name) {
		for (ModelType type : ModelType.categorized()) {
			if (type == ModelType.UNIT || type == ModelType.UNKNOWN)
				continue;
			if (!type.getModelClass().getSimpleName().equals(name))
				continue;
			return type;
		}
		return null;
	}

}
