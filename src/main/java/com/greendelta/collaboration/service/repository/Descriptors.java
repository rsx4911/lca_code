package com.greendelta.collaboration.service.repository;

import java.util.Iterator;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.descriptors.CategorizedDescriptor;

public class Descriptors {

	private final Repository repo;

	Descriptors(Repository repo) {
		this.repo = repo;
	}

	public boolean has(ModelType type) {
		// TODO determine if model type is in repository
		return true;
	}

	public Iterator<Descriptor> get() {
		return null;
	}

	public Iterator<Descriptor> get(Commit commit) {
		return null;
	}

	public Iterator<Descriptor> get(ModelType type, Commit commit) {
		return null;
	}

	public Descriptor get(ModelType type, String refId, Commit commit) {
		return null;
	}

	public Iterator<Descriptor> getForPath(ModelType type, Commit commit, String path) {
		return null;
	}

	public static class Descriptor extends CategorizedDescriptor {
		public String commitId;
	}

}
