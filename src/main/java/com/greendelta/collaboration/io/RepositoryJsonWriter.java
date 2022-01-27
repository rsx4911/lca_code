package com.greendelta.collaboration.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Reference;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;

import com.greendelta.collaboration.service.Repository;

public class RepositoryJsonWriter implements Closeable {

	private final static Logger log = LogManager.getLogger(RepositoryJsonWriter.class);
	private final ZipStore zipStore;
	private final Repository repo;

	public static void writeCurrentAsync(Repository repo) {
		var refs = repo.references().find().all();
		new Thread(() -> {
			try {
				var writer = new RepositoryJsonWriter(repo, repo.getCachedJsonFile());
				for (var ref : refs) {
					writer.put(ref);
				}
				writer.close();
			} catch (IOException e) {
				log.error("Error writing json-ld archive", e);
			}
		}).start();
	}

	public RepositoryJsonWriter(Repository repo, File file) throws IOException {
		this.zipStore = ZipStore.open(file);
		this.repo = repo;
	}

	public String put(Reference ref) throws IOException {
		var data = repo.datasets().get(ref);
		if (data == null)
			return null;
		zipStore.put(ModelPath.get(ref.type, ref.refId), data.getBytes("utf-8"));
		repo.references().getBinaries(ref).forEach(binary -> {
			zipStore.putBin(ref.type, ref.refId, binary, repo.datasets().getBinary(ref, binary));
		});
		return data;
	}

	@Override
	public void close() throws IOException {
		zipStore.close();
	}

}
