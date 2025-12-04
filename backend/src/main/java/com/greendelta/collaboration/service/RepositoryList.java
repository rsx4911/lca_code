package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.openlca.commons.Strings;

public class RepositoryList extends ArrayList<Repository> implements AutoCloseable {

	private static final long serialVersionUID = -4522801518497978389L;
	
	public RepositoryList() {
	}

	public RepositoryList(Stream<Repository> repos) {
		if (repos == null)
			return;
		repos.forEach(this::add);
	}
	
	public void sort() {
		sort((r1, r2) -> Strings.compareIgnoreCase(r1.getLabel(), r2.getLabel()));
	}

	@Override
	public void close() {
		forEach(Repository::close);
	}

}
