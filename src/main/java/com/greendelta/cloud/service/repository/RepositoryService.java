package com.greendelta.cloud.service.repository;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Directories;
import com.greendelta.cloud.util.Strings;

public class RepositoryService {

	private String repositoryRoot;
	private UserService userService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath, UserService userService) {
		this.repositoryRoot = repositoryPath;
		this.userService = userService;
	}

	public RepositoryPaths getForId(String id) {
		String path = Strings.concat(repositoryRoot, "/", id);
		return new RepositoryPaths(path);
	}

	public boolean exists(String name) {
		return getPath(name).exists();
	}

	public void create(String name) {
		RepositoryPaths.create(getPath(name));
	}

	public void delete(String name) {
		Directories.delete(getPath(name));
	}

	private File getPath(String name) {
		User user = userService.getCurrentUser();
		String path = Strings.concat(repositoryRoot, "/", user.getName(), "/", name);
		return new File(path);
	}

}
