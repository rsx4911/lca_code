package com.greendelta.collaboration.service.user;

import java.util.HashMap;
import java.util.List;

import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.DatasetField;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class CommentService {

	private final Dao<Comment> dao;
	private final PermissionsService permissions;
	private final UserService userService;
	private final SettingsService settings;

	public CommentService(Dao<Comment> dao, PermissionsService permissions, UserService userService,
			SettingsService settings) {
		this.dao = dao;
		this.permissions = permissions;
		this.userService = userService;
		this.settings = settings;
	}

	public List<Comment> getAllTopSorted(Repository repo, String filter) {
		var jpql = "SELECT c FROM Comment c WHERE c.repositoryPath = :repositoryPath AND c.replyTo IS NULL";
		var attributes = new HashMap<String, Object>();
		attributes.put("repositoryPath", repo.path());
		if (!Strings.nullOrEmpty(filter)) {
			jpql += " AND (LOWER(c.text) LIKE :filter OR (SELECT count(c1) FROM Comment c1 WHERE c1.replyTo = c AND LOWER(c1.text) LIKE :filter) > 0)";
			attributes.put("filter", "%" + filter.toLowerCase() + "%");
		}
		jpql += " ORDER BY c.date DESC";
		return permissions.filterCanRead(dao.getAll(jpql, attributes));
	}

	public List<Comment> getAllFor(Repository repository) {
		return getAllFor(repository, null, null, null);
	}

	public List<Comment> getAllFor(Repository repo, ModelType type, String refId, String commitId) {
		var jpql = "SELECT c FROM Comment c WHERE c.repositoryPath = :repositoryPath";
		var attributes = new HashMap<String, Object>();
		attributes.put("repositoryPath", repo.path());
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
		return permissions.filterCanRead(dao.getAll(jpql, attributes));
	}

	public void clearUser(User user) {
		var jpql = "SELECT c FROM Comment c WHERE c.user.id = :userId";
		var attributes = new HashMap<String, Object>();
		attributes.put("userId", user.id);
		var comments = dao.getAll(jpql, attributes);
		comments.forEach(c -> c.user = null);
		dao.update(comments);
	}

	public Comment get(long id) {
		return dao.get(id);
	}

	public List<Comment> getRepliesTo(long id) {
		var jpql = "SELECT c FROM Comment c WHERE c.replyTo.id = :id ORDER BY c.date ASC";
		var attributes = new HashMap<String, Object>();
		attributes.put("id", id);
		return permissions.filterCanRead(dao.getAll(jpql, attributes));
	}

	public Comment insert(Comment comment) {
		if (!permissions.canCommentIn(comment.repositoryPath))
			throw Response.forbidden(comment.repositoryPath, Permission.COMMENT);
		return dao.insert(comment);
	}

	public Comment update(long commentId, String text) {
		var comment = dao.get(commentId);
		if (comment == null)
			return null;
		comment.text = text;
		return dao.update(comment);
	}

	public void move(Repository from, Repository to) {
		var comments = getAllFor(from);
		comments.forEach(c -> c.repositoryPath = to.path());
		dao.update(comments);
	}

	public void copy(Repository from, Repository to) {
		var comments = getAllFor(from);
		var oldToNew = new HashMap<Long, Comment>();
		comments.stream().filter(c -> c.replyTo == null).forEach(comment -> {
			Comment clone = clone(comment, null, to);
			clone = dao.insert(clone);
			oldToNew.put(comment.id, clone);
		});
		comments.stream().filter(c -> c.replyTo != null).forEach(comment -> {
			Comment replyTo = oldToNew.get(comment.replyTo.id);
			Comment clone = clone(comment, replyTo, to);
			dao.insert(clone);
		});
	}

	private Comment clone(Comment comment, Comment replyTo, Repository repo) {
		var clone = new Comment();
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
		clone.repositoryPath = repo.path();
		return clone;
	}

	public void delete(Repository repo) {
		var comments = getAllFor(repo);
		comments.stream().filter(c -> c.replyTo == null).forEach(c -> delete(c.id));
		comments.stream().filter(c -> c.replyTo != null).forEach(c -> delete(c.id));
	}

	public Comment changeVisibility(long commentId, Role role) {
		var comment = dao.get(commentId);
		if (comment == null)
			return null;
		if (!permissions.canManage(comment))
			throw Response.forbidden(comment.repositoryPath, Permission.MANAGE_COMMENTS);
		comment.restrictedToRole = role;
		dao.update(comment);
		return comment;
	}

	public Comment release(long commentId) {
		var comment = dao.get(commentId);
		if (comment == null)
			return null;
		var currentUser = userService.getCurrentUser();
		var isCurrentUser = comment.user.equals(currentUser);
		if (!isCurrentUser && !comment.released)
			return comment;
		if (isCurrentUser) {
			comment.released = true;
		}
		if (permissions.canManageCommentsIn(comment.repositoryPath)) {
			comment.approved = true;
		} else {
			String repoPath = settings.get(ServerSetting.REPOSITORY_PATH);
			if (repoPath != null) {
				var path = RepositoryPath.of(comment.repositoryPath).toString();
				if (!settings.is(RepositorySetting.COMMENT_APPROVAL, path)) {
					comment.approved = true;
				}
			}
		}
		return dao.update(comment);
	}

	public void delete(long commentId) {
		var comment = dao.get(commentId);
		if (comment == null)
			return;
		if (!permissions.canManage(comment))
			return;
		dao.delete(getRepliesTo(commentId));
		dao.delete(comment);
	}

}
