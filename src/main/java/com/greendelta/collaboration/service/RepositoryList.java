package com.greendelta.collaboration.service;

import java.util.ArrayList;

public class RepositoryList extends ArrayList<Repository> implements AutoCloseable {

	private static final long serialVersionUID = -4522801518497978389L;

	@Override
	public void close() {
		forEach(Repository::close);
	}

}
