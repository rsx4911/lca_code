package com.greendelta.cloud.service;

import static org.openlca.cloud.util.Strings.concat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.error.UnauthorizedRepositoryAccessException;
import org.openlca.cloud.util.Directories;
import org.openlca.jsonld.output.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.User;

public class RepositoryService {

	private static final Logger log = LoggerFactory.getLogger(Repository.class);

	private String root;
	private AccessService accessService;
	private UserService userService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath,
			AccessService accessService, UserService userService) {
		this.root = repositoryPath;
		this.accessService = accessService;
		this.userService = userService;
	}

	public Repository get(String group, String name) {
		Repository repo = new Repository(root, group, name);
		if (!accessService.hasAccess(repo))
			throw new UnauthorizedRepositoryAccessException(repo.toId());
		return repo;
	}

	public boolean exists(String group, String name) {
		return new File(getPath(group, name)).exists();
	}

	public void create(String group, String name) {
		new File(getPath(group, name)).mkdirs();
		putJsonContext(group, name);
	}

	private void putJsonContext(String group, String name) {
		JsonObject context = Context.write();
		try {
			File file = new File(getPath(group, name), "context.json");
			file.createNewFile();
			String json = new Gson().toJson(context);
			byte[] data = json.getBytes("utf-8");
			Files.write(data, file);
		} catch (Exception e) {
			log.error("Could not create context.json", e);
		}
	}

	public void delete(Repository repo) {
		accessService.unshare(repo);
		Directories.delete(new File(getPath(repo.group, repo.name)));
	}

	public void deleteAllFor(User user) {
		File userDirectory = new File(root, user.username);
		if (!userDirectory.exists())
			return;
		accessService.unshareByUser(user.username);
		Directories.delete(userDirectory);
	}

	public long getCount(boolean adminArea) {
		return getAll(adminArea).size();
	}

	public List<Repository> getAll(int page, String filter, boolean adminArea) {
		List<Repository> accessible = getAll(adminArea);
		List<Repository> filtered = new ArrayList<>();
		if (filter == null || filter.isEmpty())
			filtered = accessible;
		else
			for (Repository repo : accessible)
				if (repo.toId().contains(filter))
					filtered.add(repo);
		List<Repository> paged = new ArrayList<>();
		for (int i = 0; i < filtered.size(); i++)
			if (i < ((page - 1) * 10))
				continue;
			else if (i > (page * 10))
				break;
			else
				paged.add(filtered.get(i));
		return paged;
	}

	private List<Repository> getAll(boolean adminArea) {
		File root = new File(this.root);
		List<Repository> repos = new ArrayList<>();
		User currentUser = userService.getCurrentUser();
		boolean isAdmin = adminArea && currentUser.admin;
		for (File group : root.listFiles())
			for (File name : group.listFiles()) {
				Repository repo = new Repository(this.root, group.getName(),
						name.getName());
				if (!isAdmin)
					if (!accessService.hasAccess(repo))
						continue;
				repos.add(repo);
			}
		return repos;
	}

	private String getPath(String group, String name) {
		return concat(root, "/", group, "/", name);
	}

}
