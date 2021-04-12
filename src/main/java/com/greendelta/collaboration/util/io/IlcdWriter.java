package com.greendelta.collaboration.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.greendelta.collaboration.service.repository.Datasets.Binary;
import com.greendelta.collaboration.service.repository.Descriptors.Descriptor;
import com.greendelta.collaboration.service.repository.Repository;

public class IlcdWriter implements DatasetWriter {

	private final Logger log = LogManager.getLogger(getClass());
	private final Repository repo;
	private final JsonStore jsonStore;
	private final DataStore ilcdStore;
	private final Json2IlcdStore converter;
	private final Gson gson = new Gson();
	private final File tmpFile;
	private final Commit commit;
	private final Set<String> processed = new HashSet<>();
	private Set<FileReference> collectedRefs;

	public IlcdWriter(Repository repo, Commit commit) throws IOException {
		this.repo = repo;
		this.commit = commit;
		File tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, UUID.randomUUID().toString() + ".zip");
		this.ilcdStore = new ZipStore(tmpFile);
		this.jsonStore = new JsonStoreImpl();
		this.converter = new Json2IlcdStore(this.jsonStore, ilcdStore, this::collectRefs);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		if (processed.contains(type.name() + refId))
			return;
		processed.add(type.name() + refId);
		this.collectedRefs = new HashSet<>();
		Descriptor desciptor = repo.descriptors.get(type, refId, commit);
		if (desciptor == null)
			return;
		// TODO check if this is required still
		// boolean exists = entry != null && entry.action != IndexAction.DELETE;
		// if (!exists) {
		// Commit lastCommit = repo.commits.getLastBefore(type, refId,
		// commit.id);
		// if (lastCommit == null)
		// return;
		// }
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
			String data = repo.datasets.get(modelType, refId, commit.id);
			if (data != null)
				return gson.fromJson(data, JsonObject.class);
			Commit lastCommit = repo.commits.find().model(modelType, refId).before(commit.id).last();
			if (lastCommit == null)
				return null;
			data = repo.datasets.get(modelType, refId, lastCommit.id);
			if (data == null)
				return null;
			return gson.fromJson(data, JsonObject.class);
		}

		@Override
		public byte[] getExternalFile(String sourceRefId, String filename) {
			Commit lastCommit = repo.commits.find().model(ModelType.SOURCE, sourceRefId).until(commit.id).last();
			if (lastCommit == null)
				return null;
			Binary binary = repo.datasets.getBinary(ModelType.SOURCE, sourceRefId, lastCommit.id, filename);
			if (binary == null)
				return null;
			return binary.data;
		}

		@Override
		public List<JsonObject> getGlobalParameters() {
			List<JsonObject> parameters = new ArrayList<>();
			Iterator<Descriptor> entries = repo.descriptors.get(ModelType.PARAMETER, commit);
			while (entries.hasNext()) {
				Descriptor descriptor = entries.next();
				String data = repo.datasets.get(descriptor.type, descriptor.refId, descriptor.commitId);
				if (data == null)
					continue;
				parameters.add(gson.fromJson(data, JsonObject.class));
			}
			return parameters;
		}

	}

}
