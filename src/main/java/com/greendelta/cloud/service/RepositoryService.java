package com.greendelta.cloud.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.util.Directories;
import org.openlca.jsonld.output.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.model.Role;
import com.greendelta.cloud.model.User;

@Singleton
public class RepositoryService {

	private static final Logger log = LoggerFactory.getLogger(Repository.class);

	private final Map<String, DatasetIndex> indices = new HashMap<>();
	private final String root;
	private final AccessService accessService;
	private final MembershipService membershipService;
	private final UserService userService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath,
			AccessService accessService, MembershipService membershipService, UserService userService) {
		this.root = repositoryPath;
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.userService = userService;
	}

	public Repository get(String group, String name) {
		Repository repo = new Repository(root, group, name);
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canRead(currentUser, repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "READ");
		return repo;
	}

	public DatasetIndex getIndex(Repository repo) {
		DatasetIndex index = indices.get(repo.toId());
		if (index == null) {
			index = new DatasetIndex(repo.getIndexDir());
			indices.put(repo.toId(), index);
		}
		return index;
	}

	public boolean exists(String group, String name) {
		return new File(getPath(group, name)).exists();
	}

	public Repository create(String group, String name) {
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canWrite(currentUser, group))
			throw new UnauthorizedAccessException(group, "WRITE");
		new File(getPath(group, name)).mkdirs();
		putJsonContext(group, name);
		Repository repo = get(group, name);
		membershipService.addMembership(currentUser, repo.toId(), Role.OWNER);
		return repo;
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
		User currentUser = userService.getCurrentUser();
		if (!currentUser.admin && !accessService.canDelete(currentUser, repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "DELETE");
		membershipService.removeMemberships(repo.toId());
		Directories.delete(new File(getPath(repo.group, repo.name)));
	}

	public void deleteAllFor(User user) {
		File userDirectory = new File(root, user.username);
		if (!userDirectory.exists())
			return;
		for (File repoDir : userDirectory.listFiles())
			membershipService.removeMemberships(userDirectory.getName() + File.separator + repoDir.getName());
		Directories.delete(userDirectory);
	}

	public long getCount(boolean adminArea) {
		return getAll(adminArea).size();
	}

	public PagedResult<Repository> getAll(int page, String filter,
			boolean adminArea) {
		List<Repository> accessible = getAll(adminArea);
		return PagedResult.pagedAndFiltered(page, filter, accessible, (repo) -> {
			return repo.toId();
		});
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
				if (!isAdmin && !accessService.canRead(currentUser, repo.toId()))
					continue;
				repos.add(repo);
			}
		return repos;
	}

	private String getPath(String group, String name) {
		return root + File.separator + group + File.separator + name;
	}

	public byte[] getAvatar(String group, String name) {
		File avatarFile = get(group, name).getAvatarFile();
		if (!avatarFile.exists())
			return null;
		try {
			return Files.toByteArray(avatarFile);
		} catch (IOException e) {
			log.error("Error reading repository avatar file", e);
			return null;
		}
	}

	public void setAvatar(String group, String name, InputStream file) {
		User currentUser = userService.getCurrentUser();
		String repoId = Repository.toId(group, name);
		if (!currentUser.admin && !accessService.canWrite(currentUser, repoId))
			throw new UnauthorizedAccessException(Repository.toId(group, name), "WRITE");
		File avatarFile = get(group, name).getAvatarFile();
		if (file != null)
			try (FileOutputStream output = new FileOutputStream(avatarFile)) {
				ByteStreams.copy(file, output);
			} catch (IOException e) {
				log.error("Error writing repository avatar file", e);
			}
		else if (avatarFile.exists())
			avatarFile.delete();
	}

}
