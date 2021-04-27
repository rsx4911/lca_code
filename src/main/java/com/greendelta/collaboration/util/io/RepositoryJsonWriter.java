package com.greendelta.collaboration.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.git.Binary;
import org.openlca.cloud.api.git.Reference;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;

import com.greendelta.collaboration.service.Repository;

public class RepositoryJsonWriter implements Closeable {

	private final static Logger log = LogManager.getLogger(RepositoryJsonWriter.class);
	private final ZipStore zipStore;
	private final Repository repo;

	public static void writeCurrentAsync(Repository repo) {
		List<Reference> refs = repo.references.find().all();
		new Thread(() -> {
			try {
				RepositoryJsonWriter writer = new RepositoryJsonWriter(repo, repo.getCachedJsonFile());
				for (Reference ref : refs) {
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
		String data = repo.datasets.get(ref);
		if (data == null)
			return null;
		zipStore.put(ModelPath.get(ref.type, ref.refId), data.getBytes("utf-8"));
		List<Binary> binaries = repo.datasets.getBinaries(ref);
		for (Binary binary : binaries) {
			zipStore.putBin(ref.type, ref.refId, binary.filename, binary.data);
		}
		return data;
	}

	@Override
	public void close() throws IOException {
		zipStore.close();
	}

}
