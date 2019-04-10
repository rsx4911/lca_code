package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.error.RepositoryNotFoundException;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Schema;
import org.openlca.jsonld.Schema.UnsupportedSchemaException;
import org.openlca.jsonld.output.Context;
import org.zeroturnaround.zip.ZipUtil;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Dirs;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.util.io.Json2Repository;
import com.greendelta.search.wrapper.SearchResult;

public class RepositoryService {

	private static final Logger log = LogManager.getLogger(Repository.class);
	private static final String VERSION = "1";

	private final AccessService accessService;
	private final MembershipService membershipService;
	private final UserService userService;
	private final CommentService commentService;
	private final SettingsService settingsService;
	private final LibraryService libraryService;

	@Inject
	public RepositoryService(AccessService accessService, MembershipService membershipService, UserService userService,
			CommentService commentService, SettingsService settingsService, LibraryService libraryService) {
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.userService = userService;
		this.commentService = commentService;
		this.settingsService = settingsService;
		this.libraryService = libraryService;
	}

	public Repository get(String id) {
		if (Strings.isNullOrEmpty(id))
			throw new RepositoryNotFoundException("");
		if (!id.contains("/"))
			throw new RepositoryNotFoundException(id);
		String[] path = id.split("/");
		if (path.length != 2)
			throw new RepositoryNotFoundException(id);
		return get(path[0], path[1]);
	}

	private String getRootPath() {
		return settingsService.get(Key.REPOSITORY_PATH);
	}

	public Repository get(String group, String name) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			throw new UnauthorizedAccessException(Repository.toId(group, name), "READ");
		Repository repo = Repository.get(path, group, name);
		if (!accessService.canRead(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "READ");
		return repo;
	}

	public boolean exists(String group, String name) {
		File root = new File(getRootPath());
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
		String path = getPath(group, name);
		if (path == null)
			throw new UnauthorizedAccessException(group, "WRITE");
		new File(path).mkdirs();
		putJsonContext(group, name);
		putVersion(group, name);
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
			commentService.move(repo, newRepo);
			delete(repo);
		} catch (IOException e) {
			log.error("Error moving repository contents", e);
			return false;
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
		if (!Repositories.clone(from, to, commits))
			return false;
		commentService.copy(from, to);
		return true;
	}

	public void setSetting(Repository repo, String setting, String value) {
		if (!accessService.canSetSettings(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "SET_SETTING");
		repo.setSetting(setting, value);
	}
	
	public void setLibraryRestriction(Repository repo, String library, Role restriction) {
		if (!accessService.canSetSettings(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "SET_SETTING");
		repo.setRestriction(library, restriction);
	}

	public Map<String, Role> getLibraryRestrictions(Repository repo) {
		Map<String, Role> restrictions = new HashMap<>();
		for (String library : libraryService.getLibraryNames()) {
			restrictions.put(library, repo.libraryRestrictions.get(library));
		}
		return restrictions;
	}
	
	private void putJsonContext(String group, String name) {
		String path = getPath(group, name);
		if (path == null)
			return;
		JsonObject context = Context.write(Schema.URI);
		try {
			File file = new File(path, "context.json");
			file.createNewFile();
			String json = new Gson().toJson(context);
			byte[] data = json.getBytes("utf-8");
			Files.write(data, file);
		} catch (Exception e) {
			log.error("Could not create context.json", e);
		}
	}

	private void putVersion(String group, String name) {
		String path = getPath(group, name);
		if (path == null)
			return;
		File versionFile = new File(path, ".version");
		if (versionFile.exists())
			return;
		try {
			Files.write(VERSION.getBytes(Charset.forName("utf-8")), versionFile);
		} catch (Exception e) {
			log.error("Could not create .version file", e);
		}
	}

	boolean delete(Repository repo) {
		String path = getPath(repo.group, repo.name);
		if (path == null)
			return false;
		return Directories.delete(new File(path));
	}

	public StreamingOutput pack(Repository repo) {
		return new RepositoryOutput(repo.repoDir.toPath());
	}

	public void unpack(Repository repo, InputStream input) {
		try {
			org.openlca.util.Dirs.delete(repo.repoDir.toPath());
			create(repo.group, repo.name);
			new RepositoryInput(repo.repoDir.toPath()).read(input);
		} catch (IOException e) {
			log.error("Error unpacking repository", e);
		}
	}

	public void importJsonLd(Repository repo, InputStream input, String commitMessage) {
		org.openlca.util.Dirs.delete(repo.repoDir.toPath());
		create(repo.group, repo.name);
		User user = userService.getCurrentUser();
		ZipUtil.unpack(input, repo.repoDir);
		try {
			Json2Repository.convert(repo.repoDir, user, commitMessage);
		} catch (IOException e) {
			log.error("Error converting json to repository", e);
		}
	}

	public long getCount(boolean adminArea) {
		return getAll(adminArea).size();
	}

	public SearchResult<Repository> getAll(int page, int pageSize, String filter, boolean adminArea) {
		List<Repository> accessible = getAll(adminArea);
		return SearchResults.pagedAndFiltered(page, pageSize, filter, accessible, (repo) -> repo.toId());
	}

	public List<Repository> getAllAccessible() {
		return getAll(true);
	}

	private List<Repository> getAll(boolean adminArea) {
		String path = getRootPath();
		if (path == null || path.isEmpty())
			return new ArrayList<>();
		File root = new File(path);
		if (!root.exists() || !root.isDirectory())
			return new ArrayList<>();
		List<Repository> repos = new ArrayList<>();
		for (File group : root.listFiles()) {
			if (group.listFiles() == null)
				continue;
			for (File name : group.listFiles()) {
				if (!name.isDirectory())
					continue;
				try {
					Repository repo = Repository.get(path, group.getName(), name.getName());
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
		String path = getRootPath();
		if (path == null)
			return null;
		return path + File.separator + group + File.separator + name;
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

	public File getBinFile(Repository repo, ModelType type, String refId, String commitId, String filename) {
		return repo.getBinFile(type, refId, commitId, filename);
	}
}
