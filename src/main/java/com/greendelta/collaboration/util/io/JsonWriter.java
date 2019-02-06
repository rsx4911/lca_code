package com.greendelta.collaboration.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.ZipStore;
import org.openlca.util.BinUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.SearchService;

public class JsonWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(JsonWriter.class);
	private final FetchService fetchService;
	private final HistoryService historyService;
	private final SearchService searchService;
	private final Repository repo;
	private final String commitId;
	private final ZipStore zipStore;
	private final File tmpFile;
	private final Set<String> written = new HashSet<>();

	public JsonWriter(FetchService fetchService, HistoryService historyService, SearchService searchService,
			Repository repo, String commitId) throws IOException {
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.searchService = searchService;
		this.repo = repo;
		this.commitId = commitId;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.zipStore = ZipStore.open(tmpFile);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		if (written.contains(type.name() + refId))
			return;
		String dataset = fetchService.getDataset(repo, type, refId, commitId);
		if (dataset == null) {
			Commit commit = historyService.getLastCommit(repo, type, refId, commitId);
			if (commit == null)
				return;
			dataset = fetchService.getDataset(repo, type, refId, commit.id);
			if (dataset == null)
				return;
		}
		log.trace("Exporting {} {} to json", type, refId);
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		zipStore.put(type, json);
		File binDir = fetchService.getBinDir(repo, type, refId, commitId);
		if (binDir.exists()) {
			for (File file : binDir.listFiles()) {
				String filename = file.getName();
				if (filename.endsWith(".gz")) {
					filename = filename.substring(0, filename.lastIndexOf(".gz"));
				}
				zipStore.putBin(type, refId, filename, BinUtils.gunzip(Files.readAllBytes(file.toPath())));
			}
		}
		written.add(type.name() + refId);
		writeReferences(json);
		for (IndexEntry entry : getGlobalParameters(commitId)) {
			write(ModelType.PARAMETER, entry.refId);
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

	private void writeReferences(JsonObject object) throws IOException {
		if (object == null)
			return;
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			JsonElement element = entry.getValue();
			if (element.isJsonArray()) {
				for (JsonElement arrayElement : element.getAsJsonArray())
					if (arrayElement.isJsonObject())
						write(arrayElement.getAsJsonObject());
				continue;
			}
			if (!element.isJsonObject())
				continue;
			write(element.getAsJsonObject());
		}
	}

	private void write(JsonObject object) throws IOException {
		if (!(object.has("@type") && object.has("@id"))) {
			writeReferences(object);
			return;
		}
		ModelType type = getType(object.get("@type").getAsString());
		if (type == null)
			return;
		String id = object.get("@id").getAsString();
		if (written.contains(type.name() + id))
			return;
		write(type, id);
	}

	private ModelType getType(String name) {
		for (ModelType type : ModelType.values()) {
			if (type == ModelType.UNIT || type == ModelType.UNKNOWN)
				continue;
			if (!type.getModelClass().getSimpleName().equals(name))
				continue;
			return type;
		}
		return null;
	}

}
