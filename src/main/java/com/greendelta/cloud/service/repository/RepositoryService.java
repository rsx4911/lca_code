package com.greendelta.cloud.service.repository;

import java.io.File;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.error.UnauthorizedRepositoryAccessException;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Directories;
import com.greendelta.cloud.util.Strings;

public class RepositoryService {

	private String repositoryRoot;
	private UserService userService;
	private SharingService sharingService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath, UserService userService,
			SharingService sharingService) {
		this.repositoryRoot = repositoryPath;
		this.userService = userService;
		this.sharingService = sharingService;
	}

	public Repository getForId(String id) {
		Repository.checkIdForValidity(id);
		if (!sharingService.hasAccess(userService.getCurrentUser(), id))
			throw new UnauthorizedRepositoryAccessException(id);
		return internalGetForId(id);
	}

	private Repository internalGetForId(String id) {
		String path = Strings.concat(repositoryRoot, "/", id);
		return new Repository(path);
	}

	public boolean exists(String name) {
		Repository.checkNameForValidity(name);
		return getPath(name).exists();
	}

	public void create(String name) {
		Repository.checkNameForValidity(name);
		Repository.create(getPath(name));
	}

	public void delete(String name) {
		Repository.checkNameForValidity(name);
		User user = userService.getCurrentUser();
		String id = Strings.concat(user.getName(), "/" + name);
		for (String username : sharingService.getAccessListForRepository(id))
			sharingService.unshareById(id, username);
		Directories.delete(getPath(name));
	}

	public void deleteAllFor(User user) {
		File userDirectory = new File(repositoryRoot, user.getName());
		if (!userDirectory.exists())
			return;
		Set<String> accessibleRepositories = sharingService.getAccessListForUser(user.getName());
		for (String repository : accessibleRepositories)
			sharingService.unshareById(repository, user.getName());
		Directories.delete(userDirectory);
	}

	private File getPath(String name) {
		User user = userService.getCurrentUser();
		String path = Strings.concat(repositoryRoot, "/", user.getName(), "/", name);
		return new File(path);
	}

}
