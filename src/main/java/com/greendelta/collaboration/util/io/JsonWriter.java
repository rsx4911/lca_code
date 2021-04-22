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
import com.greendelta.collaboration.service.repository.Commits.Commit;
import org.openlca.core.model.ModelType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.service.repository.References.CommitReference;
import com.greendelta.collaboration.service.repository.Repository;

public class JsonWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(JsonWriter.class);
	private final Repository repo;
	private final Commit commit;
	private final RepositoryJsonWriter writer;
	private final File tmpFile;
	private final Set<String> processed = new HashSet<>();
	private final Stack<CommitReference> refStack = new Stack<>();

	public JsonWriter(Repository repo, Commit commit) throws IOException {
		this.repo = repo;
		this.commit = commit;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.writer = new RepositoryJsonWriter(repo, tmpFile);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		CommitReference ref = repo.references.get(type, refId, commit);
		if (ref == null)
			return;
		refStack.add(ref);
		List<CommitReference> refs = repo.references.getForType(ModelType.PARAMETER, commit);
		refStack.addAll(refs);
		while (!refStack.isEmpty()) {
			write(refStack.pop());
		}
	}

	private void write(CommitReference ref) throws IOException {
		if (processed.contains(ref.refId))
			return;
		processed.add(ref.refId);
		log.trace("Exporting {} {} to json", ref.type, ref.refId);
		String dataset = writer.put(ref);
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
				for (JsonElement arrayElement : element.getAsJsonArray()) {
					if (!arrayElement.isJsonObject())
						continue;
					collectReference(arrayElement.getAsJsonObject());
				}
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
		CommitReference ref = repo.references.get(type, refId, commit);
		if (ref == null) {
			log.trace("No data set found: " + type.name() + " " + refId);
			return;
		}
		if (processed.contains(ref.refId))
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

	@Override
	public File close() throws IOException {
		writer.close();
		return tmpFile;
	}

}
