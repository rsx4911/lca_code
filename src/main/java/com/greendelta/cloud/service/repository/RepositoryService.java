package com.greendelta.cloud.service.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.error.InvalidRepositoryNameException;
import com.greendelta.cloud.error.UnauthorizedRepositoryAccessException;
import com.greendelta.cloud.error.UserNotFoundException;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Directories;
import com.greendelta.cloud.util.Strings;

public class RepositoryService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private String repositoryRoot;
	private UserService userService;

	@Inject
	public RepositoryService(@Named("repository.path") String repositoryPath, UserService userService) {
		this.repositoryRoot = repositoryPath;
		this.userService = userService;
	}

	public RepositoryPaths getForId(String id) {
		checkIdForValidity(id);
		if (!hasAccess(userService.getCurrentUser(), id))
			throw new UnauthorizedRepositoryAccessException(id);
		return internalGetForId(id);
	}

	private void checkIdForValidity(String id) {
		if (!id.contains("/") || id.indexOf('/') != id.lastIndexOf('/'))
			throw new InvalidRepositoryNameException(id);
	}

	private void checkNameForValidity(String name) {
		User user = userService.getCurrentUser();
		String id = Strings.concat(user.getName(), "/", name);
		if (!id.contains("/") || id.indexOf('/') != id.lastIndexOf('/'))
			throw new InvalidRepositoryNameException(id);
	}

	private RepositoryPaths internalGetForId(String id) {
		String path = Strings.concat(repositoryRoot, "/", id);
		return new RepositoryPaths(path);
	}

	private boolean hasAccess(User user, String id) {
		String owner = id.split("/")[0];
		if (user.getName().equals(owner))
			return true;
		Set<String> sharedWith = internalGetSharedWith(id);
		return sharedWith.contains(user.getName());
	}

	public boolean exists(String name) {
		checkNameForValidity(name);
		return getPath(name).exists();
	}

	public void create(String name) {
		checkNameForValidity(name);
		RepositoryPaths.create(getPath(name));
	}

	public void delete(String name) {
		checkNameForValidity(name);
		Directories.delete(getPath(name));
	}

	public void share(String name, String with) {
		checkNameForValidity(name);
		User user = userService.getCurrentUser();
		if (userService.getForName(with) == null)
			throw new UserNotFoundException(with);
		String id = Strings.concat(user.getName(), "/", name);
		File accessFile = internalGetForId(id).getSharedAccessFile();
		Set<String> sharedWith = new HashSet<>(internalGetSharedWith(id));
		boolean added = sharedWith.add(with);
		if (!added)
			return;
		try {
			Files.write(accessFile.toPath(), sharedWith, StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (IOException e) {
			log.error("Error writing access file for repository " + id, e);
		}
	}

	public void unshare(String name, String with) {
		checkNameForValidity(name);
		User user = userService.getCurrentUser();
		String id = Strings.concat(user.getName(), "/", name);
		File accessFile = internalGetForId(id).getSharedAccessFile();
		Set<String> sharedWith = new HashSet<>(internalGetSharedWith(id));
		boolean removed = sharedWith.remove(with);
		if (!removed)
			return;
		try {
			Files.write(accessFile.toPath(), sharedWith, StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (IOException e) {
			log.error("Error writing access file for repository " + id, e);
		}
	}

	private Set<String> internalGetSharedWith(String id) {
		File accessFile = internalGetForId(id).getSharedAccessFile();
		if (!accessFile.exists())
			return Collections.emptySet();
		try {
			return Collections.unmodifiableSet(new HashSet<>(Files.readAllLines(accessFile.toPath())));
		} catch (IOException e) {
			log.error("Error reading access file for repository " + id, e);
			return Collections.emptySet();
		}
	}

	public Set<String> getSharedWithFor(String id) {
		checkIdForValidity(id);
		if (!hasAccess(userService.getCurrentUser(), id))
			throw new UnauthorizedRepositoryAccessException(id);
		return internalGetSharedWith(id);
	}

	private File getPath(String name) {
		User user = userService.getCurrentUser();
		String path = Strings.concat(repositoryRoot, "/", user.getName(), "/", name);
		return new File(path);
	}

}
