package com.greendelta.cloud.service;

import java.io.File;

import org.openlca.cloud.error.UnauthorizedRepositoryAccessException;
import org.openlca.cloud.util.Directories;
import org.openlca.cloud.util.Strings;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.User;

public class RepositoryService {

	private String repositoryRoot;
	private UserService userService;
	private AccessService accessService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath,
			UserService userService, AccessService accessService) {
		this.repositoryRoot = repositoryPath;
		this.userService = userService;
		this.accessService = accessService;
	}

	public Repository getForId(String id) {
		Repository.checkIdForValidity(id);
		if (!accessService
				.hasAccess(userService.getCurrentUser().getName(), id))
			throw new UnauthorizedRepositoryAccessException(id);
		return internalGetForId(id);
	}

	private Repository internalGetForId(String id) {
		String path = Strings.concat(repositoryRoot, "/", id);
		return new Repository(path);
	}

	public boolean exists(String name) {
		Repository.checkNameForValidity(name);
		return new File(getPath(name)).exists();
	}

	public void create(String name) {
		Repository.checkNameForValidity(name);
		Repository.create(getPath(name));
	}

	public void delete(String name) {
		Repository.checkNameForValidity(name);
		accessService.unshareById(toId(name));
		Directories.delete(new File(getPath(name)));
	}

	public void deleteAllFor(User user) {
		File userDirectory = new File(repositoryRoot, user.getName());
		if (!userDirectory.exists())
			return;
		accessService.unshareByUser(user.getName());
		Directories.delete(userDirectory);
	}

	private String getPath(String name) {
		return Strings.concat(repositoryRoot, "/", toId(name));
	}

	private String toId(String name) {
		User user = userService.getCurrentUser();
		return Strings.concat(user.getName(), "/", name);
	}
}
