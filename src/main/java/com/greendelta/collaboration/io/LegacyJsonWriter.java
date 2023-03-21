package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Commit;

import com.greendelta.collaboration.service.Repository;

public class LegacyJsonWriter implements DatasetWriter {

	private final JsonWriter jsonWriter;

	public LegacyJsonWriter(Repository repo, Commit commit) throws IOException {
		this.jsonWriter = new JsonWriter(repo, commit);
	}

	@Override
	public void write(ModelType type, String refId) throws IOException {
		jsonWriter.write(type, refId);
	}

	@Override
	public File close() throws IOException {
		var file = jsonWriter.close();
		try (var converter = new LegacyJsonConverter(file)) {
			return converter.run();
		}
	}

}
