package com.greendelta.collaboration.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.openlca.cloud.model.data.Commit;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;

import com.greendelta.collaboration.service.repository.Datasets.Binary;
import com.greendelta.collaboration.service.repository.Descriptors.Descriptor;
import com.greendelta.collaboration.service.repository.Repository;

public class RepositoryJsonWriter implements Closeable {

	private final ZipStore zipStore;
	private final Repository repo;

	public static void writeCurrent(Repository repo) throws IOException {
		Commit commit = repo.commits.find().last();
		if (commit == null)
			return;
		RepositoryJsonWriter writer = new RepositoryJsonWriter(repo, repo.getCachedJsonFile());
		Iterator<Descriptor> iterator = repo.descriptors.get(commit);
		while (iterator.hasNext()) {
			writer.put(iterator.next());
		}
		writer.close();
	}

	public RepositoryJsonWriter(Repository repo, File file) throws IOException {
		this.zipStore = ZipStore.open(file);
		this.repo = repo;
	}

	public String put(Descriptor descriptor) throws IOException {
		String data = repo.datasets.get(descriptor.type, descriptor.refId, descriptor.commitId);
		if (data == null)
			return null;
		zipStore.put(ModelPath.get(descriptor.type, descriptor.refId), data.getBytes("utf-8"));
		List<Binary> binaries = repo.datasets.getBinaries(descriptor.type, descriptor.refId, descriptor.commitId);
		for (Binary binary : binaries) {
			zipStore.putBin(descriptor.type, descriptor.refId, binary.filename, binary.data);
		}
		return data;
	}

	@Override
	public void close() throws IOException {
		zipStore.close();
	}

}
