package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.List;

import org.openlca.util.Strings;

public class RepositoryList extends ArrayList<Repository> implements AutoCloseable {

	private static final long serialVersionUID = -4522801518497978389L;
	
	public RepositoryList() {
	}

	public RepositoryList(List<Repository> repos) {
		if (repos == null || repos.isEmpty())
			return;
		addAll(repos);
	}
	
	public void sort() {
		sort((r1, r2) -> Strings.compare(r1.getLabel().toLowerCase(), r2.getLabel().toLowerCase()));
	}

	@Override
	public void close() {
		forEach(Repository::close);
	}

}
