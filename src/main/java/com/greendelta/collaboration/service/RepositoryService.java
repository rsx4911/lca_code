package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.openlca.git.model.Commit;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Dirs;
import org.openlca.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.error.ForbiddenAccessException;
import com.greendelta.collaboration.error.RepositoryNotFoundException;
import com.greendelta.collaboration.io.RepositoryJsonWriter;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.GroupSetting;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService.Settings;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.SearchResults;

@Service
public class RepositoryService {

	private static final Logger log = LogManager.getLogger(RepositoryService.class);

	private final GroupService groupService;
	private final AccessService accessService;
	private final MembershipService membershipService;
	private final UserService userService;
	private final CommentService commentService;
	private final SettingsService settings;
	private final TaskService taskService;

	public RepositoryService(GroupService groupService, AccessService accessService,
			MembershipService membershipService, UserService userService, CommentService commentService,
			SettingsService settings, TaskService taskService) {
		this.groupService = groupService;
		this.accessService = accessService;
		this.membershipService = membershipService;
		this.userService = userService;
		this.commentService = commentService;
		this.settings = settings;
		this.taskService = taskService;
	}

	public Repository get(RepositoryPath path) {
		return get(path.group, path.repo);
	}

	public Repository get(String id) {
		if (Strings.nullOrEmpty(id))
			throw new RepositoryNotFoundException("");
		if (!id.contains("/"))
			throw new RepositoryNotFoundException(id);
		var path = id.split("/");
		if (path.length != 2)
			throw new RepositoryNotFoundException(id);
		return get(path[0], path[1]);
	}

	private String getRootPath() {
		return settings.get(ServerSetting.REPOSITORY_PATH);
	}

	public Repository get(String group, String name) {
		var path = getRootPath();
		var id = RepositoryPath.of(group, name).toString();
		if (path == null || path.isEmpty())
			throw new RepositoryNotFoundException(id);
		if (!Repository.getDir(path, group, name).exists())
			throw new RepositoryNotFoundException(group, name);
		if (!accessService.canRead(id))
			throw new ForbiddenAccessException(id, "READ");
		Settings<RepositorySetting> repoSettings = settings.get(SettingType.REPOSITORY_SETTING, id,
				accessService::canSetSettings);
		Settings<GroupSetting> groupSettings = groupService.getSettings(group);
		try {
			return new Repository(path, group, name, repoSettings, groupSettings);
		} catch (IOException e) {
			log.error("Error opening repository", e);
			return null;
		}
	}

	public boolean exists(String group, String name) {
		var root = new File(getRootPath());
		if (root.listFiles() == null)
			return false;
		for (var g : root.listFiles()) {
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
		var currentUser = userService.getCurrentUser();
		if (!accessService.canCreateRepositoryIn(group))
			throw new ForbiddenAccessException(group, "WRITE");
		var path = getPath(group, name);
		if (path == null)
			return null;
		if (!init(path))
			return null;
		membershipService.addMembership(currentUser, RepositoryPath.of(group, name).toString(), Role.OWNER, true);
		return get(group, name);
	}

	private boolean init(String path) {
		File dir = new File(path);
		try (var git = Git.init().setBare(true).setDirectory(dir).call()) {
			return true;
		} catch (Exception e) {
			log.error("Error initializing git repository", e);
			Dirs.delete(dir);
			return false;
		}
	}

	public boolean move(Repository repo, String group, String name) {
		if (!accessService.canMove(repo.path()))
			throw new ForbiddenAccessException(repo.path(), "MOVE");
		if (!accessService.canCreateRepositoryIn(group))
			throw new ForbiddenAccessException(group, "WRITE");
		if (!accessService.canDelete(repo.path()))
			throw new ForbiddenAccessException(repo.path(), "DELETE");
		if (exists(group, name))
			return false;
		try (var newRepo = create(group, name)) {
			if (newRepo == null)
				throw Response.error("Could not create repository, does the configured 'Repositories root directory' exist and can be write-accessed?");
			Dirs.move(repo.dir.toPath(), newRepo.dir.toPath());
			moveMemberships(repo, newRepo);
			commentService.move(repo, newRepo);
			taskService.move(repo, newRepo);
			repo.settings.move(newRepo);
			delete(repo);
			return true;
		}
	}

	private void moveMemberships(Repository fromRepo, Repository toRepo) {
		var memberships = membershipService.getMemberships(fromRepo.path());
		for (Membership membership : memberships) {
			if (membership.team != null) {
				membershipService.addMemberships(membership.team, toRepo.path(), membership.role);
			} else {
				membershipService.addMembership(membership.user, toRepo.path(), membership.role);
			}
		}
	}

	public boolean clone(Repository from, Repository to, Commit resetTo) {
		if (!accessService.canWrite(to.group))
			throw new ForbiddenAccessException(to.group, "WRITE");
		try {
			Dirs.copy(from.dir.toPath(), to.dir.toPath());
			if (resetTo != null) {
				try (var gitRepo = new FileRepository(to.dir)) {
					new ResetCommand(gitRepo)
							.setMode(ResetType.SOFT)
							.setRef(resetTo.id)
							.call();
					try (var git = new Git(gitRepo)) {
						git.gc().setPrunePreserved(true).setAggressive(true).call();
					}
				}
			}
		} catch (Exception e) {
			log.error("Error cloning git repository", e);
			Dirs.delete(to.dir);
			throw new Error(e);
		}
		commentService.copy(from, to);
		return true;
	}

	public void setRestriction(Repository repo, String restriction, Role restrictedTo) {
		Map<String, Role> restrictions = repo.settings.get(RepositorySetting.RESTRICTIONS, new HashMap<>());
		if (restrictedTo == null) {
			restrictions.remove(restriction);
		} else {
			restrictions.put(restriction, restrictedTo);
		}
		repo.settings.set(RepositorySetting.RESTRICTIONS, restrictions);
	}

	public boolean delete(Repository repo) {
		if (!accessService.canDelete(repo.path()))
			throw new ForbiddenAccessException(repo.path(), "DELETE");
		var path = getPath(repo.group, repo.name);
		if (path == null)
			return false;
		repo.close();
		Dirs.delete(new File(path));
		return true;
	}

	public StreamingResponseBody pack(Repository repo) {
		return output -> {
			var out = new ZipOutputStream(output);
			write(repo.dir.toPath(), repo.dir, out);
			out.close();
		};
	}

	private void write(Path repoPath, File file, ZipOutputStream out) throws IOException {
		if (file.isDirectory()) {
			for (var child : file.listFiles()) {
				write(repoPath, child, out);
			}
			return;
		}
		if (!file.isFile())
			return;
		var path = file.toPath();
		var entry = repoPath.relativize(path).toString().replace('\\', '/');
		out.putNextEntry(new ZipEntry(entry));
		java.nio.file.Files.copy(path, out);
		out.closeEntry();
	}

	public void unpack(Repository repo, InputStream input) {
		try {
			if (!repo.dir.exists())
				return;
			Dirs.delete(repo.dir);
			create(repo.group, repo.name).close();
			var repoPath = repo.dir.toPath();
			var in = new ZipInputStream(input);
			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				var filename = entry.getName();
				var path = repoPath.resolve(filename);
				var file = path.toFile();
				if (entry.isDirectory()) {
					file.mkdirs();
					continue;
				}
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				java.nio.file.Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
			}
			in.close();
		} catch (IOException e) {
			log.error("Error unpacking repository", e);
		}
	}

	@Async("taskExecutor")
	public void generateJson(Repository repo, Function<LibraryLink, String> urlResolver) {
		RepositoryJsonWriter.writeCurrent(repo.dir, repo.getCachedJsonFile(), repo.linkedLibraries(urlResolver));
	}

	public int getNoOfRepositories(User user) {
		if (user.username == null || user.username.isEmpty())
			return 0;
		String path = settings.get(ServerSetting.REPOSITORY_PATH);
		if (path == null || path.isEmpty())
			return 0;
		var userGroup = new File(path, user.username);
		if (!userGroup.exists())
			return 0;
		return userGroup.listFiles().length;
	}

	public long getCount(boolean adminArea) {
		try (var repos = getAll(false, adminArea)) {
			return repos.size();
		}
	}

	public RepositorySearchResult getAll(int page, int pageSize, String filter, boolean onlyPublic,
			boolean adminArea) {
		var accessible = getAll(onlyPublic, adminArea);
		try {
			accessible.sort();
			var result = SearchResults.pagedAndFiltered(page, pageSize, filter, accessible, Repository::path);
			return new RepositorySearchResult(result);
		} catch (Throwable e) {
			accessible.close();
			return new RepositorySearchResult();
		}
	}

	public RepositoryList getAllAccessible() {
		return getAll(false, true);
	}

	public RepositoryList getPublic() {
		return getAll(true, false);
	}

	private RepositoryList getAll(boolean onlyPublic, boolean adminArea) {
		var path = getRootPath();
		if (path == null || path.isEmpty())
			return new RepositoryList();
		var root = new File(path);
		if (!root.exists() || !root.isDirectory())
			return new RepositoryList();
		var repos = new RepositoryList();
		for (var group : root.listFiles()) {
			if (group.listFiles() == null)
				continue;
			for (var name : group.listFiles()) {
				if (!name.isDirectory())
					continue;
				var repoPath = RepositoryPath.of(group.getName(), name.getName());
				if (!accessService.canRead(repoPath.toString(), !adminArea))
					continue;
				var repo = get(group.getName(), name.getName());
				if (onlyPublic && !repo.settings.is(RepositorySetting.PUBLIC_ACCESS)) {
					repo.close();
					continue;
				}
				repos.add(repo);
			}
		}
		return repos;
	}

	public List<String> getPublicRepositoryOrder() {
		return getRepositoryList(ServerSetting.REPOSITORIES_ORDER, true);
	}

	public List<String> getPublicHiddenRepositories() {
		return getRepositoryList(ServerSetting.REPOSITORIES_HIDDEN, false);
	}

	private List<String> getRepositoryList(ServerSetting key, boolean addMissing) {
		List<String> repositoryArray = settings.get(key, new ArrayList<>());
		try (var repos = getPublic()) {
			var repoMap = repos.stream()
					.collect(Collectors.toMap(repo -> repo.path(), repo -> repo));
			var repoIds = new ArrayList<String>();
			for (var repoId : repositoryArray) {
				var repo = repoMap.remove(repoId);
				if (repo == null)
					continue;
				repoIds.add(repoId);
			}
			if (addMissing) {
				repoMap.values().forEach(repo -> repoIds.add(repo.path()));
			}
			if (userService.getCurrentUser().isAdmin()) {
				settings.set(key, repoIds);
			}
			return repoIds;
		}
	}

	private String getPath(String group, String name) {
		var path = getRootPath();
		if (path == null)
			return null;
		return path + File.separator + group + File.separator + name;
	}

}
