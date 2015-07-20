package com.greendelta.cloud.service.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.error.UnauthorizedRepositoryAccessException;
import com.greendelta.cloud.error.UserNotFoundException;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.util.Strings;

public class SharingService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private String repositoryRoot;
	private UserService userService;

	@Inject
	public SharingService(@Named("repository.path") String repositoryPath, UserService userService) {
		this.repositoryRoot = repositoryPath;
		this.userService = userService;
	}

	public void share(String name, String with) {
		String id = checkSharingInput(name, with);
		File repositoryAccessFile = internalGetForId(id).getSharedAccessFile();
		File userAccessFile = getUserAccessFile(with);
		addToList(with, repositoryAccessFile);
		addToList(id, userAccessFile);
	}

	public void unshare(String name, String with) {
		String id = checkSharingInput(name, with);
		unshareById(id, with);
	}

	void unshareById(String id, String with) {
		Repository.checkIdForValidity(id);
		if (userService.getForName(with) == null)
			throw new UserNotFoundException(with);
		File repositoryAccessFile = internalGetForId(id).getSharedAccessFile();
		File userAccessFile = getUserAccessFile(with);
		removeFromList(with, repositoryAccessFile);
		removeFromList(id, userAccessFile);
	}

	private File getUserDir(String name) {
		File userDir = new File(repositoryRoot, name);
		if (!userDir.exists())
			userDir.mkdir();
		return userDir;
	}

	private File getUserAccessFile(String name) {
		File userDir = new File(repositoryRoot, name);
		if (!userDir.exists())
			userDir.mkdir();
		return new File(userDir, "access.txt");
	}

	private String checkSharingInput(String name, String with) {
		Repository.checkNameForValidity(name);
		User user = userService.getCurrentUser();
		if (userService.getForName(with) == null)
			throw new UserNotFoundException(with);
		return Strings.concat(user.getName(), "/", name);
	}

	public Set<String> getAccessListForRepository(String id) {
		Repository.checkIdForValidity(id);
		if (!hasAccess(userService.getCurrentUser(), id))
			throw new UnauthorizedRepositoryAccessException(id);
		File repositoryAccessFile = internalGetForId(id).getSharedAccessFile();
		return Collections.unmodifiableSet(readList(repositoryAccessFile));
	}

	public Set<String> getAccessListForUser(String username) {
		if (userService.getForName(username) == null)
			throw new UserNotFoundException(username);
		File userAccessFile = getUserAccessFile(username);
		Set<String> list = readList(userAccessFile);
		File userDir = getUserDir(username);
		for (File repository : userDir.listFiles())
			if (repository.isDirectory())
				list.add(Strings.concat(username, "/", repository.getName()));
		return Collections.unmodifiableSet(list);
	}

	private Repository internalGetForId(String id) {
		String path = Strings.concat(repositoryRoot, "/", id);
		return new Repository(path);
	}

	boolean hasAccess(User user, String id) {
		String owner = id.split("/")[0];
		if (user.getName().equals(owner))
			return true;
		File userAccessFile = getUserAccessFile(user.getName());
		Set<String> sharedRepositories = readList(userAccessFile);
		return sharedRepositories.contains(id);
	}

	private boolean addToList(String value, File file) {
		Set<String> list = readList(file);
		boolean added = list.add(value);
		if (!added)
			return false;
		writeList(file, list);
		return true;
	}

	private boolean removeFromList(String value, File file) {
		Set<String> list = readList(file);
		boolean removed = list.remove(value);
		if (!removed)
			return false;
		if (list.size() == 0)
			file.delete();
		else
			writeList(file, list);
		return true;
	}

	private Set<String> readList(File file) {
		if (!file.exists())
			return new HashSet<>();
		try {
			return new HashSet<>(Files.readAllLines(file.toPath()));
		} catch (IOException e) {
			log.error("Error reading access file " + file.getAbsolutePath(), e);
			return new HashSet<>();
		}
	}

	private void writeList(File file, Collection<String> lines) {
		try {
			Files.write(file.toPath(), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.CREATE);
		} catch (IOException e) {
			log.error("Error writing access file " + file.getAbsolutePath(), e);
		}
	}

}
