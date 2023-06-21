package com.greendelta.collaboration.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.openlca.git.find.Datasets;
import org.openlca.git.find.References;
import org.openlca.git.model.Reference;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;

public class RepositoryJsonWriter implements Closeable {

	private final static Logger log = LogManager.getLogger(RepositoryJsonWriter.class);
	private final ZipStore zipStore;
	private final References references;
	private final Datasets datasets;

	public static void writeCurrent(File gitDir, File cachedJsonFile) {
		try (var repo = new FileRepository(gitDir)) {
			var references = References.of(repo);
			var datasets = Datasets.of(repo);
			var writer = new RepositoryJsonWriter(references, datasets, cachedJsonFile);
			references.find().iterate(ref -> writer.put(ref));
			writer.close();
		} catch (IOException e) {
			log.error("Error writing json-ld archive", e);
		}
	}

	public RepositoryJsonWriter(References references, Datasets datasets, File file) throws IOException {
		this.zipStore = ZipStore.open(file);
		this.references = references;
		this.datasets = datasets;
	}

	public String put(Reference ref) {
		var data = datasets.get(ref);
		if (data == null)
			return null;
		zipStore.put(ModelPath.jsonOf(ref.type, ref.refId), data.getBytes(StandardCharsets.UTF_8));
		references.getBinaries(ref).forEach(binary -> {
			zipStore.putBin(ref.type, ref.refId, binary, datasets.getBinary(ref, binary));
		});
		return data;
	}

	@Override
	public void close() throws IOException {
		zipStore.close();
	}

}
