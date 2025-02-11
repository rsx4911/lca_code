package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Strings;
import org.openlca.util.TypedRefIdMap;

import com.google.gson.JsonArray;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;

public class JsonWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(JsonWriter.class);
	private final TypedRefIdMap<Reference> references;
	private final File tmpFile;
	private boolean collectReferences;
	private final RepositoryJsonWriter writer;
	private final Set<String> processed = new HashSet<String>();
	private final JsonArray categories = new JsonArray();
	private final Stack<Reference> toProcess = new Stack<>();

	public JsonWriter(File file, Repository repo, List<LibraryLink> libraries, Commit commit) throws IOException {
		this.tmpFile = file;
		this.references = new TypedRefIdMap<Reference>();
		repo.references.find().commit(commit.id).iterate(ref -> references.put(ref, ref));
		this.writer = new RepositoryJsonWriter(repo, libraries, repo.getInfo(commit.id).schemaVersion(), tmpFile);
	}

	@Override
	public void write(Reference ref) {
		if (ref.isModelType || ref.isRepositoryInfo || ref.isLibrary)
			return;
		if (processed.contains(keyOf(ref)))
			return;
		if (ref.isCategory) {
			categories.add(ref.path);
			processed.add(ref.path);
			return;
		}
		toProcess.add(ref);
		while (!toProcess.isEmpty()) {
			writeRef(toProcess.pop());
		}
	}

	private void writeRef(Reference ref) {
		processed.add(keyOf(ref));
		var dataset = writer.put(ref);
		if (dataset == null || !collectReferences)
			return;
		var json = Maps.of(dataset);
		collectReferences(json);
	}

	private void queue(ModelType type, String refId) {
		if (type == null || Strings.nullOrEmpty(refId))
			return;
		var ref = references.get(type, refId);
		if (ref == null) {
			log.trace("No data set found: " + type.name() + " " + refId);
			return;
		}
		toProcess.add(ref);
	}

	@Override
	public void withReferences() {
		references.get(ModelType.PARAMETER).forEach(this::writeRef);
		collectReferences = true;
	}

	@Override
	public File close() throws IOException {
		writer.writeCategoriesJson(categories);
		writer.close();
		return tmpFile;
	}

	private void collectReferences(Map<String, Object> object) {
		if (object == null)
			return;
		for (var key : object.keySet()) {
			if (Maps.isArray(object, key)) {
				for (var arrayElement : Maps.getArray(object, key)) {
					if (!Maps.is(arrayElement))
						continue;
					collectReference(Maps.of(arrayElement));
				}
				continue;
			}
			if (!Maps.isObject(object, key))
				continue;
			collectReference(Maps.getObject(object, key));
		}
	}

	private void collectReference(Map<String, Object> object) {
		if (!(object.containsKey("@type") && object.containsKey("@id"))) {
			collectReferences(object);
			return;
		}
		var type = getType(Maps.getString(object, "@type"));
		if (type == null)
			return;
		var refId = Maps.getString(object, "@id");
		if (refId == null)
			return;
		if (processed.contains(keyOf(type, refId)))
			return;
		queue(type, refId);
	}

	private ModelType getType(String name) {
		for (var type : ModelType.values()) {
			if (!type.getModelClass().getSimpleName().equals(name))
				continue;
			return type;
		}
		return null;
	}
	
	private String keyOf(Reference ref) {
		if (ref.isCategory)
			return ref.path;
		return keyOf(ref.type, ref.refId);
	}
	
	private String keyOf(ModelType type, String refId) {
		return type.name() + refId;		
	}

}
