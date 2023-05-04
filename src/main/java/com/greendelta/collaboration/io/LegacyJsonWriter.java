package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
	public File writeAll() throws IOException {
		var file = jsonWriter.writeAll();
		try (var converter = new LegacyJsonConverter(file)) {
			return converter.run();
		}
	}

	@Override
	public File write(Collection<Reference> refs) throws IOException {
		var file = jsonWriter.write(refs);
		try (var converter = new LegacyJsonConverter(file)) {
			return converter.run();
		}
	}

}
