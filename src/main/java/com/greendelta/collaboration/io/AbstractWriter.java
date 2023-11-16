package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.git.util.TypedRefIdMap;
import org.openlca.util.Strings;

import com.greendelta.collaboration.service.Repository;

abstract class AbstractWriter implements DatasetWriter {

	private final static Logger log = LogManager.getLogger(AbstractWriter.class);
	private final TypedRefIdMap<Reference> references;
	protected final File tmpFile;
	protected boolean collectReferences;

	protected AbstractWriter(Repository repo, Commit commit) throws IOException {
		var tmpDir = Files.createTempDirectory("lca-collaboration-writer").toFile();
		this.tmpFile = new File(tmpDir, "temp.zip");
		this.references = new TypedRefIdMap<Reference>();
		repo.references.find().commit(commit.id).iterate(ref -> references.put(ref, ref));
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
		write(ref);
	}

	public void withReferences() {
		references.get(ModelType.PARAMETER).forEach(this::write);
		collectReferences = true;
	}
	
	@Override
	public File close() throws IOException {
		return tmpFile;
	}

}
