package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;

import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;

public class JsonWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(JsonWriter.class);
	private final Repository repo;
	private final Commit commit;
	private final RepositoryJsonWriter writer;
	private final File tmpFile;
	private final Set<String> processed = new HashSet<>();
	private final Stack<Reference> refStack = new Stack<>();

	public JsonWriter(Repository repo, Commit commit) throws IOException {
		this.repo = repo;
		this.commit = commit;
		var tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.writer = new RepositoryJsonWriter(repo, tmpFile);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		var ref = repo.references().get(type, refId, commit.id);
		if (ref == null)
			return;
		refStack.add(ref);
		var refs = repo.references().find().type(ModelType.PARAMETER).commit(commit.id).all();
		refStack.addAll(refs);
		while (!refStack.isEmpty()) {
			write(refStack.pop());
		}
	}

	private void write(Reference ref) throws IOException {
		if (processed.contains(ref.refId))
			return;
		processed.add(ref.refId);
		log.trace("Exporting {} {} to json", ref.type, ref.refId);
		var dataset = writer.put(ref);
		if (dataset == null) {
			log.trace("No data set found: " + ref.type.name() + " " + ref.refId + " (commit " + commit.id + ")");
			return;
		}
		var json = Maps.of(dataset);
		collectReferences(json);
	}

	@SuppressWarnings("unchecked")
	private void collectReferences(Map<String, Object> object) throws IOException {
		if (object == null)
			return;
		for (var key : object.keySet()) {
			if (Maps.isArray(object, key)) {
				for (var arrayElement : Maps.getArray(object, key)) {
					if (!(arrayElement instanceof Map))
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

	private void collectReference(Map<String, Object> object) throws IOException {
		if (!(object.containsKey("@type") && object.containsKey("@id"))) {
			collectReferences(object);
			return;
		}
		var type = getType(Maps.getString(object, "@type"));
		if (type == null)
			return;
		var refId = Maps.getString(object, "@id");
		var ref = repo.references().get(type, refId, commit.id);
		if (ref == null) {
			log.trace("No data set found: " + type.name() + " " + refId);
			return;
		}
		if (processed.contains(ref.refId))
			return;
		refStack.add(ref);
	}

	private ModelType getType(String name) {
		for (var type : ModelType.values()) {
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
