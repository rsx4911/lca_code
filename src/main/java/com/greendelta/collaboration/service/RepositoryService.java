package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.openlca.jsonld.output.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.Dirs;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.lca.search.SearchResult;

public class RepositoryService {

	private static final Logger log = LoggerFactory.getLogger(Repository.class);

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
		Repository repo = Repository.get(root, group, name);
		if (!accessService.canRead(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "READ");
		return repo;
	}

	public boolean exists(String group, String name) {
		File root = new File(this.root);
		if (root.listFiles() == null)
			return false;
		for (File g : root.listFiles()) {
			if (!g.getName().equalsIgnoreCase(group))
				continue;
			if (g.listFiles() == null)
				return false;
			for (File repo : g.listFiles()) {
				if (!repo.getName().equalsIgnoreCase(name))
					continue;
				return true;
			}
		}
		return false;
	}

	public Repository create(String group, String name) {
		User currentUser = userService.getCurrentUser();
		if (!accessService.canCreateRepositoryIn(group))
			throw new UnauthorizedAccessException(group, "WRITE");
		new File(getPath(group, name)).mkdirs();
		putJsonContext(group, name);
		Repository repo = get(group, name);
		membershipService.addMembership(currentUser, repo.toId(), Role.OWNER, true);
		return repo;
	}

	public boolean move(Repository repo, String group, String name) {
		if (!accessService.canMove(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "MOVE");
		if (!accessService.canCreateRepositoryIn(group))
			throw new UnauthorizedAccessException(group, "WRITE");
		if (!accessService.canDelete(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "DELETE");
		if (exists(group, name))
			return false;
		Repository newRepo = create(group, name);
		try {
			boolean moved = Dirs.moveContents(repo.repoDir, newRepo.repoDir, true);
			if (!moved) {
				delete(newRepo);
				return false;
			}
			moveMemberships(repo, newRepo);
			delete(repo);
		} catch (IOException e) {
			log.error("Error moving repository contents", e);
		}
		return true;
	}

	private void moveMemberships(Repository fromRepo, Repository toRepo) {
		List<Membership> memberships = membershipService.getMemberships(fromRepo.toId());
		for (Membership membership : memberships)
			if (membership.team != null)
				membershipService.addMemberships(membership.team, toRepo.toId(), membership.role);
			else
				membershipService.addMembership(membership.user, toRepo.toId(), membership.role);
	}

	public boolean clone(Repository from, Repository to, List<Commit> commits) {
		if (!accessService.canWrite(to.group))
			throw new UnauthorizedAccessException(to.group, "WRITE");
		return Repositories.clone(from, to, commits);
	}

	public boolean setPublic(Repository repo, boolean value) {
		if (!accessService.canWrite(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "SET_PUBLIC");
		File file = new File(repo.repoDir, ".public");
		if (value && !file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error("Error making repository public", e);
			}
		} else if (file.exists()) {
			file.delete();
		}
		return file.exists();
	}

	private void putJsonContext(String group, String name) {
		JsonObject context = Context.write(Schema.URI);
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

	boolean delete(Repository repo) {
		return Directories.delete(new File(getPath(repo.group, repo.name)));
	}

	public long getCount(boolean adminArea) {
		return getAll(adminArea).size();
	}

	public SearchResult<Repository> getAll(int page, String filter, boolean adminArea) {
		List<Repository> accessible = getAll(adminArea);
		return SearchResults.pagedAndFiltered(page, filter, accessible, (repo) -> repo.toId());
	}

	public List<Repository> getAllAccessible() {
		return getAll(true);
	}

	private List<Repository> getAll(boolean adminArea) {
		File root = new File(this.root);
		List<Repository> repos = new ArrayList<>();
		for (File group : root.listFiles()) {
			if (group.listFiles() == null)
				continue;
			for (File name : group.listFiles()) {
				if (!name.isDirectory())
					continue;
				try {
					Repository repo = Repository.get(this.root, group.getName(), name.getName());
					if (!accessService.canRead(repo.toId(), !adminArea))
						continue;
					repos.add(repo);
				} catch (UnsupportedSchemaException e) {
					// ignore, just don't add to list
				}
			}
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
		String repoId = Repository.toId(group, name);
		if (!accessService.canWrite(repoId))
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

	public File getBinDir(Repository repo, ModelType type, String refId, String commitId) {
		return repo.getBinDir(type, refId, commitId, false);
	}
}
