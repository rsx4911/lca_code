package com.greendelta.collaboration.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;
import org.openlca.util.BinUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.Collections;

public class JsonWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(JsonWriter.class);
	private final FetchService fetchService;
	private final SearchService searchService;
	private final Repository repo;
	private final String commitId;
	private final ZipStore zipStore;
	private final File tmpFile;
	private final Set<FileReference> processed = new HashSet<>();
	private final Stack<FileReference> refStack = new Stack<>();

	public JsonWriter(FetchService fetchService, SearchService searchService, Repository repo, String commitId)
			throws IOException {
		this.fetchService = fetchService;
		this.searchService = searchService;
		this.repo = repo;
		this.commitId = commitId;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.zipStore = ZipStore.open(tmpFile);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		FileReference ref = ref(type, refId);
		refStack.add(ref);
		for (IndexEntry entry : getGlobalParameters(commitId)) {
			refStack.add(ref(ModelType.PARAMETER, entry.refId));
		}
		while (!refStack.isEmpty()) {
			write(refStack.pop());
		}
	}

	private void write(FileReference ref) throws IOException {
		if (processed.contains(ref))
			return;
		processed.add(ref);
		IndexEntry entry = searchService.getMostRecentUntil(repo, ref.type, ref.refId, commitId);
		if (entry == null) {
			log.trace("No data set found: " + ref.type.name() + " " + ref.refId);
			return;
		}
		String dataset = fetchService.getDataset(repo, ref.type, ref.refId, entry.commitId);
		if (dataset == null) {
			log.trace("No data set found: " + ref.type.name() + " " + ref.refId + " (commit " + commitId + ")");
			return;
		}
		log.trace("Exporting {} {} to json", ref.type, ref.refId);
		zipStore.put(ModelPath.get(ref.type, ref.refId), dataset.getBytes("utf-8"));
		File binDir = fetchService.getBinDir(repo, ref.type, ref.refId, entry.commitId);
		if (binDir.exists()) {
			for (File file : binDir.listFiles()) {
				String filename = file.getName();
				if (filename.endsWith(".gz")) {
					filename = filename.substring(0, filename.lastIndexOf(".gz"));
				}
				zipStore.putBin(ref.type, ref.refId, filename, BinUtils.gunzip(Files.readAllBytes(file.toPath())));
			}
		}
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		collectReferences(json);
	}

	private List<IndexEntry> getGlobalParameters(String untilCommitId) {
		List<IndexEntry> entries = searchService.getMostRecentUntilForPath(repo, ModelType.PARAMETER, null, commitId);
		return Collections.filter(entries, entry -> entry.action == IndexAction.DELETE);
	}

	private void collectReferences(JsonObject object) throws IOException {
		if (object == null)
			return;
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			JsonElement element = entry.getValue();
			if (element.isJsonArray()) {
				for (JsonElement arrayElement : element.getAsJsonArray())
					if (arrayElement.isJsonObject())
						collectReference(arrayElement.getAsJsonObject());
				continue;
			}
			if (!element.isJsonObject())
				continue;
			collectReference(element.getAsJsonObject());
		}
	}

	private void collectReference(JsonObject object) throws IOException {
		if (!(object.has("@type") && object.has("@id"))) {
			collectReferences(object);
			return;
		}
		ModelType type = getType(object.get("@type").getAsString());
		if (type == null)
			return;
		String refId = object.get("@id").getAsString();
		FileReference ref = ref(type, refId);
		if (processed.contains(ref))
			return;
		refStack.add(ref);
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

	private FileReference ref(ModelType type, String refId) {
		FileReference ref = new FileReference();
		ref.type = type;
		ref.refId = refId;
		return ref;
	}

	@Override
	public File close() throws IOException {
		zipStore.close();
		return tmpFile;
	}

}
