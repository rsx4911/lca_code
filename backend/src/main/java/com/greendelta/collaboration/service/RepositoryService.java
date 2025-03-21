package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import org.openlca.git.actions.GitInit;
import org.openlca.git.model.Commit;
import org.openlca.util.Dirs;
import org.openlca.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.io.Files;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.RepositoryJsonWriter;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.LibraryService.LibraryLoader;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService.Settings;
import com.greendelta.collaboration.service.task.TaskService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.UserService;

@Service
public class RepositoryService {

	private static final Logger log = LogManager.getLogger(RepositoryService.class);

	private final GroupService groupService;
	private final PermissionsService permissions;
	private final MembershipService membershipService;
	private final UserService userService;
	private final CommentService commentService;
	private final SettingsService settings;
	private final FileService fileService;
	private final TaskService taskService;
	private final ReleaseService releaseService;
	private final HistoryService historyService;

	public RepositoryService(GroupService groupService, PermissionsService permissions,
			MembershipService membershipService, UserService userService, CommentService commentService,
			SettingsService settings, FileService fileService, TaskService taskService, ReleaseService releaseService,
			HistoryService historyService) {
		this.groupService = groupService;
		this.permissions = permissions;
		this.membershipService = membershipService;
		this.userService = userService;
		this.commentService = commentService;
		this.settings = settings;
		this.fileService = fileService;
		this.taskService = taskService;
		this.releaseService = releaseService;
		this.historyService = historyService;
	}

	public Repository get(RepositoryPath path) {
		return get(path.group, path.repo);
	}

	public Repository get(String id) {
		if (Strings.nullOrEmpty(id) || !id.contains("/"))
			throw Response.notFound("No repository '" + id + "' found");
		var path = id.split("/");
		if (path.length != 2)
			throw Response.notFound("No repository '" + id + "' found");
		return get(path[0], path[1]);
	}

	private String getRootPath() {
		return settings.get(ServerSetting.REPOSITORY_PATH);
	}

	public Repository get(String group, String name) {
		var path = getRootPath();
		var id = RepositoryPath.of(group, name).toString();
		if (path == null || path.isEmpty())
			throw Response.notFound("No repository '" + id + "' found");
		if (!Repository.getDir(path, group, name).exists())
			throw Response.notFound("No repository '" + group + "/" + name + "' found");
		Settings<RepositorySetting> repoSettings = settings.get(SettingType.REPOSITORY_SETTING, id,
				permissions::canSetSettingsOf);
		var groupSettings = groupService.getSettings(group);
		try {
			var repo = new Repository(path, group, name, repoSettings, groupSettings);
			if (!permissions.canRead(id) && historyService.getReleasedCommits(repo).isEmpty())
				throw Response.forbidden(id, Permission.READ);
			return repo;
		} catch (IOException e) {
			log.error("Error opening repository", e);
			return null;
		}
	}
	
	File getDir(RepositoryPath path) {
		return Repository.getDir(getRootPath(), path.group, path.repo);
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
		if (!permissions.canCreateRepositoryIn(group))
			throw Response.forbidden(group, Permission.WRITE);
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
		try {
			GitInit.in(dir).run();
			return true;
		} catch (Exception e) {
			log.error("Error initializing git repository", e);
			Dirs.delete(dir);
			return false;
		}
	}

	public boolean move(Repository repo, String group, String name) {
		if (!permissions.canMove(repo.path()))
			throw Response.forbidden(repo.path(), Permission.MOVE);
		if (!permissions.canCreateRepositoryIn(group))
			throw Response.forbidden(group, Permission.WRITE);
		if (!permissions.canDelete(repo.path()))
			throw Response.forbidden(repo.path(), Permission.DELETE);
		if (exists(group, name))
			return false;
		try (var newRepo = create(group, name)) {
			if (newRepo == null)
				throw Response.error(
						"Could not create repository, does the configured 'Repositories root directory' exist and can be write-accessed?");
			Dirs.move(repo.dir.toPath(), newRepo.dir.toPath());
			moveMemberships(repo, newRepo);
			commentService.move(repo, newRepo);
			taskService.move(repo, newRepo);
			releaseService.move(repo.path(), newRepo.path());
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
		if (!permissions.canWriteTo(to.group))
			throw Response.forbidden(to.group, Permission.WRITE);
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

	public boolean delete(Repository repo) {
		if (!permissions.canDelete(repo.path()))
			throw Response.forbidden(repo.path(), Permission.DELETE);
		var path = getPath(repo.group, repo.name);
		if (path == null)
			return false;
		repo.close();
		Dirs.delete(new File(path));
		return true;
	}

	public File pack(Repository repo) throws IOException {
		var file = fileService.createTempFile();
		var out = new ZipOutputStream(new FileOutputStream(file));
		write(repo.dir.toPath(), repo.dir, out);
		out.close();
		return file;
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

	public void generateCachedJson(Repository repo, Commit commit, LibraryLoader libraryLoader) {
		generateJson(repo.dir, repo.getCachedJsonFile(commit.id), commit, libraryLoader);
	}

	public void deleteCachedJson(Repository repo, String commitId) {
		var jsonFile = repo.getCachedJsonFile(commitId);
		Dirs.delete(jsonFile);
	}

	private void generateJson(File repoDir, File jsonFile, Commit commit, LibraryLoader libraryLoader) {
		// Don't use git repo in thread, since it might be closed by calling
		// code
		var lockFile = new File(repoDir, ".lock_" + commit.id);
		if (lockFile.exists())
			return;
		new Thread(() -> {
			try {
				Files.write(new byte[0], lockFile);
				var tmpFile = fileService.createTempFile();
				RepositoryJsonWriter.write(repoDir, commit, tmpFile, libraryLoader);
				Files.copy(tmpFile, jsonFile);
				tmpFile.delete();
			} catch (IOException e) {
				log.error("Error generating json file", e);
			} finally {
				if (lockFile.exists()) {
					Dirs.delete(lockFile);
				}
			}
		}).start();
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

	public long getCount() {
		try (var repos = getAllAccessible()) {
			return repos.size();
		}
	}

	public RepositoryList getAllAccessible() {
		if (userService.getCurrentUser().isAnonymous())
			return getReleased();
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
				if (!permissions.canRead(repoPath.toString()))
					continue;
				var repo = get(group.getName(), name.getName());
				repos.add(repo);
			}
		}
		return repos;
	}

	public RepositoryList getReleased() {
		return new RepositoryList(
				releaseService.getAll().stream()
						.map(info -> info.repositoryPath)
						.distinct()
						.map(path -> {
							try {
								return get(path);
							} catch (ResponseStatusException e) {
								return null;
							}
						})
						.filter(Objects::nonNull));
	}

	public List<String> getRepositoryOrder() {
		return getRepositoryList(ServerSetting.REPOSITORIES_ORDER, true);
	}

	public List<String> getHiddenRepositories() {
		return getRepositoryList(ServerSetting.REPOSITORIES_HIDDEN, false);
	}

	private List<String> getRepositoryList(ServerSetting key, boolean addMissing) {
		List<String> repositoryArray = settings.get(key, new ArrayList<>());
		try (var repos = getReleased()) {
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
