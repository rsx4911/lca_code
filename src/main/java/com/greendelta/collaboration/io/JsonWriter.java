package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.git.find.Datasets;
import org.openlca.git.find.References;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;

import com.greendelta.collaboration.util.Maps;

public class JsonWriter extends AbstractWriter {

	private final RepositoryJsonWriter writer;
	private final Set<String> processed = new HashSet<String>();

	public JsonWriter(References references, Datasets datasets, Commit commit) throws IOException {
		super(references, commit);
		this.writer = new RepositoryJsonWriter(references, datasets, tmpFile);
	}

	@Override
	public void write(Reference ref) {
		if (processed.contains(ref.type.name() + ref.refId))
			return;
		processed.add(ref.type.name() + ref.refId);
		var dataset = writer.put(ref);
		if (dataset == null || !collectReferences)
			return;
		var json = Maps.of(dataset);
		collectReferences(json);
	}

	@Override
	public File close() throws IOException {
		writer.close();
		return super.close();
	}

	@SuppressWarnings("unchecked")
	private void collectReferences(Map<String, Object> object) {
		if (object == null)
			return;
		for (var key : object.keySet()) {
			if (Maps.isArray(object, key)) {
				for (var arrayElement : Maps.getArray(object, key)) {
					if (!Maps.is(arrayElement))
						continue;
					collectReference((Map<String, Object>) arrayElement);
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
		if (processed.contains(type.name() + refId))
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

}
