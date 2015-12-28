package com.greendelta.cloud.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openlca.cloud.error.UserNotFoundException;

import com.google.inject.Inject;
import com.greendelta.cloud.model.Access;
import com.greendelta.cloud.model.User;

import static org.openlca.cloud.util.Strings.concat;

public class AccessService {

	private Dao<Access> dao;
	private UserService userService;

	@Inject
	public AccessService(Dao<Access> dao, UserService userService) {
		this.dao = dao;
		this.userService = userService;
	}

	public void share(String repoName, String withUser) {
		Repository.checkNameForValidity(repoName);
		User currentUser = userService.getCurrentUser();
		if (currentUser.username.equals(withUser))
			return;
		String fullId = concat(currentUser.username, "/", repoName);
		User user = getUser(withUser);
		if (get(user.username, fullId) != null)
			return;
		Access access = new Access();
		access.user = user;
		access.repositoryId = fullId;
		dao.insert(access);
	}

	public void unshare(String repoName, String withUser) {
		Repository.checkNameForValidity(repoName);
		User currentUser = userService.getCurrentUser();
		if (currentUser.username.equals(withUser))
			return;
		String fullId = concat(currentUser.username, "/", repoName);
		User user = getUser(withUser);
		Access access = get(user.username, fullId);
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
		User user = userService.getForUsername(username);
		if (user == null)
			throw new UserNotFoundException(username);
		return user;
	}

	public Set<String> getAccessListForRepository(String id) {
		List<Access> accesses = dao.getForAttribute("repositoryId", id);
		Set<String> users = new HashSet<>();
		for (Access access : accesses)
			users.add(access.user.username);
		return users;
	}

	public Set<String> getAccessListForUser(String username) {
		List<Access> accesses = dao.getForAttribute("user.name", username);
		Set<String> repositories = new HashSet<>();
		for (Access access : accesses)
			repositories.add(access.repositoryId);
		return repositories;
	}

	boolean hasAccess(String username, String id) {
		String owner = id.split("/")[0];
		User currentUser = userService.getCurrentUser();
		if (currentUser.username.equals(owner))
			return true;
		return get(username, id) != null;
	}

	private Access get(String username, String id) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("repositoryId", id);
		attributes.put("user.name", username);
		return dao.getFirstForAttributes(attributes);
	}

}
