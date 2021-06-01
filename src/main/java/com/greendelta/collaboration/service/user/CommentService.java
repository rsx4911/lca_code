package com.greendelta.collaboration.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.DatasetField;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;

public class CommentService {

	private final Dao<Comment> dao;
	private final AccessService accessService;
	private final UserService userService;
	private final SettingsService settingsService;

	@Inject
	public CommentService(Dao<Comment> dao, AccessService accessService, UserService userService,
			SettingsService settingsService) {
		this.dao = dao;
		this.accessService = accessService;
		this.userService = userService;
		this.settingsService = settingsService;
	}

	public List<Comment> getAllTopSorted(Repository repository, String filter) {
		String jpql = "SELECT c FROM Comment c WHERE c.repositoryPath = :repositoryPath AND c.replyTo IS NULL";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("repositoryPath", repository.toId());
		if (!Strings.nullOrEmpty(filter)) {
			jpql += " AND (LOWER(c.text) LIKE :filter OR (SELECT count(c1) FROM Comment c1 WHERE c1.replyTo = c AND LOWER(c1.text) LIKE :filter) > 0)";
			attributes.put("filter", "%" + filter.toLowerCase() + "%");
		}
		jpql += " ORDER BY c.date DESC";
		return accessService.filterCanRead(dao.getAll(jpql, attributes));
	}

	public List<Comment> getAllFor(Repository repository) {
		return getAllFor(repository, null, null, null);
	}

	public List<Comment> getAllFor(Repository repository, ModelType type, String refId, String commitId) {
		String jpql = "SELECT c FROM Comment c WHERE c.repositoryPath = :repositoryPath";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("repositoryPath", repository.toId());
		if (type != null) {
			jpql += " AND c.field.modelType = :modelType";
			attributes.put("modelType", type);
		}
		if (refId != null) {
			jpql += " AND c.field.refId = :refId";
			attributes.put("refId", refId);
		}
		if (commitId != null) {
			jpql += " AND c.field.commitId = :commitId";
			attributes.put("commitId", commitId);
		}
		return accessService.filterCanRead(dao.getAll(jpql, attributes));
	}

	public void clearUser(User user) {
		String jpql = "SELECT c FROM Comment c WHERE c.user.id = :userId";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("userId", user.id);
		List<Comment> comments = dao.getAll(jpql, attributes);
		for (Comment comment : comments) {
			comment.user = null;
		}
		dao.update(comments);
	}

	public Comment get(long id) {
		return dao.get(id);
	}

	public List<Comment> getRepliesTo(long id) {
		String jpql = "SELECT c FROM Comment c WHERE c.replyTo.id = :id ORDER BY c.date ASC";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", id);
		return accessService.filterCanRead(dao.getAll(jpql, attributes));
	}

	public Comment insert(Comment comment) {
		if (!accessService.canCommentIn(comment.repositoryPath))
			throw new UnauthorizedAccessException(comment.repositoryPath, "COMMENT");
		return dao.insert(comment);
	}

	public Comment update(long commentId, String text) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return null;
		comment.text = text;
		return dao.update(comment);
	}

	public void move(Repository from, Repository to) {
		List<Comment> comments = getAllFor(from, null, null, null);
		for (Comment comment : comments) {
			comment.repositoryPath = to.toId();
		}
		dao.update(comments);
	}

	public void copy(Repository from, Repository to) {
		List<Comment> comments = getAllFor(from, null, null, null);
		Map<Long, Comment> oldToNew = new HashMap<>();
		for (Comment comment : comments) {
			if (comment.replyTo != null)
				continue;
			Comment clone = clone(comment, null, to);
			clone = dao.insert(clone);
			oldToNew.put(comment.id, clone);
		}
		for (Comment comment : comments) {
			if (comment.replyTo == null)
				continue;
			Comment replyTo = oldToNew.get(comment.replyTo.id);
			Comment clone = clone(comment, replyTo, to);
			dao.insert(clone);
		}
	}

	private Comment clone(Comment comment, Comment replyTo, Repository repo) {
		Comment clone = new Comment();
		clone.approved = comment.approved;
		clone.date = comment.date;
		clone.field = new DatasetField();
		clone.field.modelType = comment.field.modelType;
		clone.field.refId = comment.field.refId;
		clone.field.path = comment.field.path;
		clone.field.commitId = comment.field.commitId;
		clone.released = comment.released;
		clone.restrictedToRole = comment.restrictedToRole;
		clone.text = comment.text;
		clone.user = comment.user;
		clone.replyTo = replyTo;
		clone.repositoryPath = repo.toId();
		return clone;
	}

	public void delete(Repository repo) {
		List<Comment> comments = getAllFor(repo);
		for (Comment comment : comments) {
			if (comment.replyTo != null) {
				delete(comment.id);
			}
		}
		for (Comment comment : comments) {
			if (comment.replyTo == null) {
				delete(comment.id);
			}
		}
	}

	public Comment changeVisibility(long commentId, Role role) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return null;
		if (!accessService.canManage(comment))
			throw new UnauthorizedAccessException(comment.repositoryPath, "MANAGE_COMMENT");
		comment.restrictedToRole = role;
		dao.update(comment);
		return comment;
	}

	public Comment release(long commentId) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return null;
		User currentUser = userService.getCurrentUser();
		boolean isCurrentUser = comment.user.equals(currentUser);
		if (!isCurrentUser && !comment.released)
			return comment;
		if (isCurrentUser) {
			comment.released = true;
		}
		if (accessService.canManageCommentsIn(comment.repositoryPath)) {
			comment.approved = true;
		} else {
			String[] split = comment.repositoryPath.split("/");
			String repoPath = settingsService.get(ServerSetting.REPOSITORY_PATH);
			if (repoPath != null) {
				String id = Repository.toId(split[0], split[1]);
				if (!settingsService.is(RepositorySetting.COMMENT_APPROVAL, id)) {
					comment.approved = true;
				}
			}
		}
		return dao.update(comment);
	}

	public void delete(long commentId) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return;
		if (!accessService.canManage(comment))
			return;
		dao.delete(getRepliesTo(commentId));
		dao.delete(comment);
	}

}
