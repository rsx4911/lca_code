package com.greendelta.collaboration.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.data.ModelStreamReader;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Bytes;

public class CommitService {

	private final static Logger log = LogManager.getLogger(CommitService.class);

	private final UserService userService;
	private final AccessService accessService;
	private final SearchService searchService;
	private final LibraryService libraryService;
	private final MembershipService membershipService;

	@Inject
	public CommitService(UserService userService, AccessService accessService, SearchService searchService,
			LibraryService libraryService, MembershipService membershipService) {
		this.userService = userService;
		this.searchService = searchService;
		this.accessService = accessService;
		this.libraryService = libraryService;
		this.membershipService = membershipService;
	}

	public Commit put(Repository repo, InputStream data) {
		if (!accessService.canWrite(repo.toId()))
			throw new UnauthorizedAccessException(repo.toId(), "WRITE");
		try (ModelStreamReader reader = new ModelStreamReader(data)) {
			return write(repo, reader);
		} catch (IOException e) {
			log.error("Error handling commit data", e);
			return null;
		}
	}

	private Commit write(Repository repo, ModelStreamReader reader) throws IOException {
		log.debug("Committing to repository {}", repo.toId());
		Commit commit = createCommit(repo, reader.readNextPartAsString());
		log.debug("Writing data sets", commit.id);
		DatasetWriter writer = initDatasetWriter(repo, commit);
		writer.writeDatasets(reader);
		log.debug("Appending commit {} to commit history", commit.id);
		File historyFile = repo.getHistoryFile(true);
		Bytes.appendTo(historyFile, commit.toString());
		return commit;
	}

	private Commit createCommit(Repository repo, String commitMessage) {
		String username = userService.getCurrentUser().username;
		long timestamp = Calendar.getInstance().getTimeInMillis();
		Commit commit = new Commit();
		commit.id = UUID.randomUUID().toString();
		commit.message = commitMessage.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
		commit.user = username;
		commit.timestamp = timestamp;
		return commit;
	}

	private DatasetWriter initDatasetWriter(Repository repo, Commit commit) {
		User currentUser = userService.getCurrentUser();
		long userGroupSize = 0;
		long maxUserGroupSize = 0;
		Role role = null;
		if (currentUser != null) {
			userGroupSize = userService.getUserGroupSize(currentUser);
			maxUserGroupSize = currentUser.settings.maxSize;
			role = membershipService.getRole(currentUser, repo.toId());
		}
		return new DatasetWriter(searchService, libraryService, userGroupSize, maxUserGroupSize, repo, commit, role);
	}

	public static class InsufficientStorageException extends RuntimeException {

		private static final long serialVersionUID = 543921197834005033L;

		InsufficientStorageException(String message) {
			super(message);
		}

	}

}
