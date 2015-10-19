package com.greendelta.cloud.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.cloud.error.UserNotFoundException;
import org.openlca.cloud.util.Strings;

import com.google.inject.Inject;
import com.greendelta.cloud.model.Access;
import com.greendelta.cloud.model.User;

public class AccessService {

	private Dao<Access> dao;
	private UserService userService;

	@Inject
	public AccessService(Dao<Access> dao, UserService userService) {
		this.dao = dao;
		this.userService = userService;
	}

	public void share(String repositoryName, String withUser) {
		Repository.checkNameForValidity(repositoryName);
		User currentUser = userService.getCurrentUser();
		if (currentUser.getName().equals(withUser))
			return;
		String fullId = Strings.concat(currentUser.getName(), "/", repositoryName);
		User user = getUser(withUser);
		if (get(user.getName(), fullId) != null)
			return;
		Access access = new Access();
		access.setUser(user);
		access.setRepositoryId(fullId);
		dao.insert(access);
	}

	public void unshare(String repositoryName, String withUser) {
		Repository.checkNameForValidity(repositoryName);
		User currentUser = userService.getCurrentUser();
		if (currentUser.getName().equals(withUser))
			return;
		String fullId = Strings.concat(currentUser.getName(), "/", repositoryName);
		User user = getUser(withUser);
		Access access = get(user.getName(), fullId);
		dao.delete(access);
	}

	void unshareById(String id) {
		List<Access> accesses = dao.getForAttribute("repositoryId", id);
		dao.delete(accesses);
	}

	void unshareByUser(String username) {
		List<Access> accesses = dao.getForAttribute("user.name", username);
		dao.delete(accesses);
	}

	private User getUser(String username) {
		User user = userService.getForName(username);
		if (user == null)
			throw new UserNotFoundException(username);
		return user;
	}

	public Set<String> getAccessListForRepository(String id) {
		List<Access> accesses = dao.getForAttribute("repositoryId", id);
		Set<String> users = new HashSet<>();
		for (Access access : accesses)
			users.add(access.getUser().getName());
		return users;
	}

	public Set<String> getAccessListForUser(String username) {
		List<Access> accesses = dao.getForAttribute("user.name", username);
		Set<String> repositories = new HashSet<>();
		for (Access access : accesses)
			repositories.add(access.getRepositoryId());
		return repositories;
	}

	boolean hasAccess(String username, String id) {
		return get(username, id) != null;
	}

	private Access get(String username, String id) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("repositoryId", id);
		attributes.put("user.name", username);
		return dao.getFirstForAttributes(attributes);
	}

}
