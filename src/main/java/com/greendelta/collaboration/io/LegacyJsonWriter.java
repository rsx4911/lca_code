package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;

import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;

import com.greendelta.collaboration.service.Repository;

public class LegacyJsonWriter implements DatasetWriter {

	private final JsonWriter jsonWriter;

	public LegacyJsonWriter(Repository repo, Commit commit) throws IOException {
		this.jsonWriter = new JsonWriter(repo, null, commit);
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
