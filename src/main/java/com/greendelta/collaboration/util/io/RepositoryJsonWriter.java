package com.greendelta.collaboration.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.greendelta.collaboration.service.repository.Commits.Commit;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;

import com.greendelta.collaboration.service.repository.Datasets.Binary;
import com.greendelta.collaboration.service.repository.References.CommitReference;
import com.greendelta.collaboration.service.repository.Repository;

public class RepositoryJsonWriter implements Closeable {

	private final ZipStore zipStore;
	private final Repository repo;

	public static void writeCurrent(Repository repo) throws IOException {
		Commit commit = repo.commits.find().latest();
		if (commit == null)
			return;
		RepositoryJsonWriter writer = new RepositoryJsonWriter(repo, repo.getCachedJsonFile());
		List<CommitReference> refs = repo.references.getFor(commit);
		for (CommitReference ref : refs) {
			writer.put(ref);
		}
		writer.close();
	}

	public RepositoryJsonWriter(Repository repo, File file) throws IOException {
		this.zipStore = ZipStore.open(file);
		this.repo = repo;
	}

	public String put(CommitReference ref) throws IOException {
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
