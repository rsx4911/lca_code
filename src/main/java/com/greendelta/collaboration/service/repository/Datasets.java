package com.greendelta.collaboration.service.repository;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.openlca.core.model.ModelType;

public class Datasets {

	private final FileRepository repo;

	Datasets(Repository repo) throws IOException {
		this.repo = new FileRepository(repo.dir);
	}

	public String get(ModelType type, String refId, String commitId) {
		try {
			ObjectId id = null; // TODO
			return new String(repo.getObjectDatabase().newReader().open(id).getBytes(), "utf-8");
		} catch (IOException e) {
			return null;
		}
	}

	public Binary getBinary(ModelType type, String refId, String commitId, String name) {
		// TODO read binary data from git
		return null;
	}

	public List<Binary> getBinaries(ModelType type, String refId, String commitId) {
		// TODO read binary data from git
		return null;
	}

	public class Binary {

		public final String filename;
		public final byte[] data;

		private Binary(String filename, byte[] data) {
			this.filename = filename;
			this.data = data;
		}

	}

}
