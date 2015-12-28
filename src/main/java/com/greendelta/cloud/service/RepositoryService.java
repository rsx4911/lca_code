package com.greendelta.cloud.service;

import static org.openlca.cloud.util.Strings.concat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.openlca.cloud.error.UnauthorizedRepositoryAccessException;
import org.openlca.cloud.util.Directories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.User;

public class RepositoryService {

	private String root;
	private UserService userService;
	private AccessService accessService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath,
			UserService userService, AccessService accessService) {
		this.root = repositoryPath;
		this.userService = userService;
		this.accessService = accessService;
	}

	public Repository getForId(String id) {
		Repository.checkIdForValidity(id);
		String currentUser = userService.getCurrentUser().username;
		if (!accessService.hasAccess(currentUser, id))
			throw new UnauthorizedRepositoryAccessException(id);
		return internalGetForId(id);
	}

	private Repository internalGetForId(String id) {
		String path = concat(root, "/", id);
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
		File userDirectory = new File(root, user.username);
		if (!userDirectory.exists())
			return;
		accessService.unshareByUser(user.username);
		Directories.delete(userDirectory);
	}

	@RequiresRoles("admin")
	public List<String> getAll() {
		File root = new File(this.root);
		List<String> repos= new ArrayList<>();
		for (File group : root.listFiles())
			for (File repo : group.listFiles())
				repos.add(concat(group.getName(), "/", repo.getName()));
		return repos;
	}

	private String getPath(String name) {
		return concat(root, "/", toId(name));
	}

	private String toId(String name) {
		User user = userService.getCurrentUser();
		return concat(user.username, "/", name);
	}
	
}
