package com.greendelta.collaboration.service.repository;

import java.util.List;

import org.openlca.core.model.ModelType;

public class Datasets {

	private final Repository repo;

	Datasets(Repository repo) {
		this.repo = repo;
	}

	public String get(ModelType type, String refId, String commitId) {
		// TODO read data set from git
		return null;
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
