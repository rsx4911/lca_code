package com.greendelta.collaboration.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.service.repository.Descriptors.Descriptor;
import com.greendelta.collaboration.service.repository.Repository;

public class JsonWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(JsonWriter.class);
	private final Repository repo;
	private final Commit commit;
	private final RepositoryJsonWriter writer;
	private final File tmpFile;
	private final Set<FileReference> processed = new HashSet<>();
	private final Stack<FileReference> refStack = new Stack<>();

	public JsonWriter(Repository repo, Commit commit) throws IOException {
		this.repo = repo;
		this.commit = commit;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.writer = new RepositoryJsonWriter(repo, tmpFile);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		FileReference ref = ref(type, refId);
		refStack.add(ref);
		Iterator<Descriptor> descriptors = repo.descriptors.get(ModelType.PARAMETER, commit);
		while (descriptors.hasNext()) {
			refStack.add(ref(ModelType.PARAMETER, descriptors.next().refId));
		}
		while (!refStack.isEmpty()) {
			write(refStack.pop());
		}
	}

	private void write(FileReference ref) throws IOException {
		if (processed.contains(ref))
			return;
		processed.add(ref);
		Descriptor descriptor = repo.descriptors.get(ref.type, ref.refId, commit);
		if (descriptor == null) {
			log.trace("No data set found: " + ref.type.name() + " " + ref.refId);
			return;
		}
		log.trace("Exporting {} {} to json", ref.type, ref.refId);
		String dataset = writer.put(descriptor);
		if (dataset == null) {
			log.trace("No data set found: " + ref.type.name() + " " + ref.refId + " (commit " + commit.id + ")");
			return;
		}
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		collectReferences(json);
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
		writer.close();
		return tmpFile;
	}

}
