package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.convert.jsonld.ilcd.Config;
import org.openlca.convert.jsonld.ilcd.Json2IlcdStore;
import org.openlca.convert.jsonld.ilcd.JsonStore;
import org.openlca.core.model.ModelType;
import org.openlca.git.find.Datasets;
import org.openlca.git.find.References;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.ilcd.commons.DataSetType;
import org.openlca.ilcd.io.DataStore;
import org.openlca.ilcd.io.ZipStore;

public class IlcdWriter extends AbstractWriter {

	private final String repoUrl;
	private final References references;
	private final Datasets datasets;
	private final Commit commit;
	private final JsonStore jsonStore = new JsonStoreImpl();
	private final DataStore ilcdStore;
	private final Json2IlcdStore converter;
	private final Set<String> processed = new HashSet<String>();

	public IlcdWriter(String repoUrl, References references, Datasets datasets, Commit commit) throws IOException {
		super(references, commit);
		this.repoUrl = repoUrl;
		this.references = references;
		this.datasets = datasets;
		this.commit = commit;
		this.ilcdStore = new ZipStore(tmpFile);
		this.converter = new Json2IlcdStore(ilcdStore, new Config(jsonStore, this::collectRef, this::createPublicationLink));
	}

	@Override
	public void write(Reference ref) {
		if (processed.contains(ref.type.name() + ref.refId))
			return;
		processed.add(ref.type.name() + ref.refId);
		var obj = jsonStore.get(ref.type.getModelClass().getSimpleName(), ref.refId);
		if (obj == null)
			return;
		converter.convertAndPut(obj);
	}

	@Override
	public File close() throws IOException {
		ilcdStore.close();
		return super.close();
	}

	private void collectRef(String type, String refId) {
		if (!collectReferences || processed.contains(getType(type).name() + refId))
			return;
		queue(getType(type), refId);
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
		var baseUrl = repoUrl;
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
			var ref = getRef(modelType, refId);
			if (ref == null)
				return null;
			return datasets.get(ref);
		}

		@Override
		public byte[] getExternalFile(String sourceRefId, String filepath) {
			var ref = getRef(ModelType.SOURCE, sourceRefId);
			if (ref == null)
				return null;
			return datasets.getBinary(ref, filepath);
		}

		@Override
		public List<String> getGlobalParameters() {
			var params = new ArrayList<String>();
			references.find().type(ModelType.PARAMETER).commit(commit.id).iterate(ref -> {
				var dataset = datasets.get(ref);
				if (dataset != null) {
					params.add(dataset);
				}
			});
			return params;
		}

	}

}
