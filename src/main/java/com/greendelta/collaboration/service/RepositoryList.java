package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.List;

public class RepositoryList extends ArrayList<Repository> implements AutoCloseable {

	private static final long serialVersionUID = -4522801518497978389L;
	
	public RepositoryList() {
	}

	public RepositoryList(List<Repository> repos) {
		addAll(repos);
	}
	
	@Override
	public void close() {
		forEach(Repository::close);
	}

}
