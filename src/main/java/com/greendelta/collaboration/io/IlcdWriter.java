package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
//import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.convert.jsonld.ilcd.Config;
import org.openlca.convert.jsonld.ilcd.Json2IlcdStore;
import org.openlca.convert.jsonld.ilcd.JsonStore;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.ilcd.commons.DataSetType;
import org.openlca.ilcd.io.DataStore;
import org.openlca.ilcd.io.ZipStore;
import org.openlca.util.Strings;

import com.greendelta.collaboration.service.Repository;

public class IlcdWriter implements DatasetWriter {

	private final Logger log = LogManager.getLogger(getClass());
	private final String serverUrl;
	private final Repository repo;
	private final JsonStore jsonStore;
	private final DataStore ilcdStore;
	private final Json2IlcdStore converter;
	private final File tmpFile;
	private final Commit commit;
	private final Set<String> processed = new HashSet<>();
	private Set<Reference> collectedRefs;

	public IlcdWriter(String serverUrl, Repository repo, Commit commit) throws IOException {
		this.serverUrl = serverUrl;
		this.repo = repo;
		this.commit = commit;
		var tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, UUID.randomUUID().toString() + ".zip");
		this.ilcdStore = new ZipStore(tmpFile);
		this.jsonStore = new JsonStoreImpl();
		this.converter = new Json2IlcdStore(ilcdStore,
				new Config(jsonStore, this::collectRef, this::createPublicationLink));
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		if (processed.contains(type.name() + refId))
			return;
		processed.add(type.name() + refId);
		this.collectedRefs = new HashSet<>();
		var ref = repo.references().get(type, refId, commit.id);
		if (ref == null)
			return;
		log.trace("Exporting {} {} to ilcd", type, refId);
		var obj = jsonStore.get(type.getModelClass().getSimpleName(), refId);
		if (obj == null)
			return;
		converter.convertAndPut(obj);
		for (var next : new ArrayList<>(collectedRefs)) {
			write(next.type, next.refId);
		}
	}

	@Override
	public File close() throws IOException {
		ilcdStore.close();
		return tmpFile;
	}

	private void collectRef(String type, String refId) {
		var mType = getType(type);
		if (mType == null)
			return;
		collectedRefs.add(repo.references().get(mType, refId, refId));
	}

	private ModelType getType(String type) {
		if (type == null)
			return null;
		for (var t : ModelType.values()) {
			if (t.getModelClass() == null)
				continue;
			if (t.getModelClass().getSimpleName().equals(type))
				return t;
		}
		return null;
	}

	private String createPublicationLink(DataSetType type, String refId) {
		var baseUrl = serverUrl;
		if (Strings.nullOrEmpty(baseUrl)) {
			baseUrl = "http://openlca.org/ilcd/resource";
		}
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		baseUrl += repo.path();
		baseUrl += "/dataset/";
		baseUrl += getUriPart(type);
		baseUrl += "/" + refId;
		if (commit != null) {
			baseUrl += "?commit=" + commit.id;
		}
		return baseUrl;
	}

	private String getUriPart(DataSetType type) {
		switch (type) {
		case CONTACT:
			return "ACTOR";
		case FLOW, FLOW_PROPERTY, LCIA_METHOD, PROCESS, SOURCE, UNIT_GROUP:
			return type.name();
		default:
			return "UNKNOWN";
		}
	}

	private class JsonStoreImpl implements JsonStore {

		@Override
		public String get(String type, String refId) {
			var modelType = getType(type);
			var ref = repo.references().get(modelType, refId, commit.id);
			if (ref == null)
				return null;
			return repo.datasets().get(ref);
		}

		@Override
		public byte[] getExternalFile(String sourceRefId, String filepath) {
			var ref = repo.references().get(ModelType.SOURCE, sourceRefId, commit.id);
			if (ref == null)
				return null;
			return repo.datasets().getBinary(ref, filepath);
		}

		@Override
		public List<String> getGlobalParameters() {
			return repo.references().find()
					.type(ModelType.PARAMETER).commit(commit.id)
					.all().stream()
					.map(ref -> repo.datasets().get(ref))
					.filter(data -> data != null)
					.toList();
		}

	}

}
