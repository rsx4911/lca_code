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

public class AccessService {

	private Dao<Access> dao;
	private UserService userService;

	@Inject
	public AccessService(Dao<Access> dao, UserService userService) {
		this.dao = dao;
		this.userService = userService;
	}

	public void share(Repository repo, String withUser) {
		User currentUser = userService.getCurrentUser();
		if (currentUser.username.equals(withUser))
			return;
		User user = getUser(withUser);
		if (getFirst(repo, user.username) != null)
			return;
		Access access = new Access();
		access.user = user;
		access.group = repo.group;
		access.repository = repo.name;
		dao.insert(access);
	}

	public void unshare(Repository repo, String withUser) {
		User currentUser = userService.getCurrentUser();
		if (currentUser.username.equals(withUser))
			return;
		User user = getUser(withUser);
		Access access = getFirst(repo, user.username);
		dao.delete(access);
	}

	void unshare(Repository repo) {
		List<Access> accesses = getAll(repo, null);
		dao.delete(accesses);
	}

	void unshareByUser(String username) {
		List<Access> accesses = getAll(null, username);
		dao.delete(accesses);
	}

	private User getUser(String username) {
		User user = userService.getForUsername(username);
		if (user == null)
			throw new UserNotFoundException(username);
		return user;
	}

	public Set<String> getAccessListForRepository(Repository repo) {
		List<Access> accesses = getAll(repo, null);
		Set<String> users = new HashSet<>();
		for (Access access : accesses)
			users.add(access.user.username);
		return users;
	}

	public Set<String> getAccessListForUser(String username) {
		List<Access> accesses = getAll(null, username);
		Set<String> repositories = new HashSet<>();
		for (Access access : accesses)
			repositories.add(Repository.toId(access.group, access.repository));
		return repositories;
	}

	boolean hasAccess(Repository repo) {
		User currentUser = userService.getCurrentUser();
		if (currentUser.username.equals(repo.group))
			return true;
		return getFirst(repo, currentUser.username) != null;
	}

	private Access getFirst(Repository repo, String username) {
		List<Access> all = getAll(repo, username);
		if (all == null || all.isEmpty())
			return null;
		return all.get(0);
	}

	private List<Access> getAll(Repository repo, String username) {
		Map<String, Object> parameters = new HashMap<>();
		if (repo != null) {
			parameters.put("group", repo.group);
			parameters.put("repository", repo.name);
		}
		if (username != null)
			parameters.put("user.name", username);
		return dao.getForAttributes(parameters);
	}

}
