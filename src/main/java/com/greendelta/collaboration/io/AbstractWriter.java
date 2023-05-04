package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.find.References;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.git.util.TypedRefIdMap;
import org.openlca.git.util.TypedRefIdSet;
import org.openlca.util.Strings;

abstract class AbstractWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(AbstractWriter.class);
	private final TypedRefIdSet processed = new TypedRefIdSet();
	private final Stack<Reference> collectedRefs = new Stack<>();
	private final TypedRefIdMap<Reference> references;
	protected final File tmpFile;
	protected boolean collectReferences;
	
	protected AbstractWriter(References references, Commit commit) throws IOException {
		this.references = new TypedRefIdMap<>();
		references.find().commit(commit.id).all().forEach(ref -> this.references.put(ref, ref));
		var tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
	}

	@Override
	public final File writeAll() throws IOException {
		this.collectReferences = false;
		for (var ref : references.values()) {
			this.write(ref);
		}
		return close();
	}

	@Override
	public final File write(Collection<Reference> refs) throws IOException {
		this.collectReferences = true;
		collectedRefs.addAll(refs);
		collectedRefs.addAll(references.get(ModelType.PARAMETER));		
		while (!collectedRefs.isEmpty()) {
			var next = collectedRefs.pop();
			if (processed.contains(next))
				continue;
			processed.add(next);
			write(next);
		}
		return close();
	}

	protected final Reference getRef(ModelType type, String refId) {
		return references.get(type, refId);
	}

	protected final void queue(ModelType type, String refId) {
		if (type == null || Strings.nullOrEmpty(refId))
			return;
		var ref = references.get(type, refId);
		if (ref == null) {
			log.trace("No data set found: " + type.name() + " " + refId);
			return;
		}
		collectedRefs.add(ref);
	}

	protected abstract void write(Reference reference) throws IOException;

	protected File close() throws IOException {
		return tmpFile;
	}

}
