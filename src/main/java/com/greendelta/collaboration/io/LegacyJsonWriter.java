package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;

import org.openlca.git.find.Datasets;
import org.openlca.git.find.References;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;

public class LegacyJsonWriter implements DatasetWriter {

	private final JsonWriter jsonWriter;

	public LegacyJsonWriter(References references, Datasets datasets, Commit commit) throws IOException {
		this.jsonWriter = new JsonWriter(references, datasets, commit);
	}

	@Override
	public void write(Reference ref) {
		jsonWriter.write(ref);
	}
	
	@Override
	public void withReferences() {
		jsonWriter.withReferences();
	}
	
	@Override
	public File close() throws IOException {
		var file = jsonWriter.close();
		try (var converter = new LegacyJsonConverter(file)) {
			return converter.run();
		}
	}

}
