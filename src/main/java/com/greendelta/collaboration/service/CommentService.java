package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;

public class CommentService {

	private final Dao<Comment> dao;
	private final AccessService accessService;
	private final UserService userService;

	@Inject
	public CommentService(Dao<Comment> dao, AccessService accessService, UserService userService) {
		this.dao = dao;
		this.accessService = accessService;
		this.userService = userService;
	}

	public List<Comment> getAllFor(Repository repository, ModelType type, String refId, String commitId) {
		String jpql = "SELECT c FROM Comment c "
				+ "WHERE c.repositoryPath = :repositoryPath ";
		if (type != null) {
			jpql += "AND c.field.modelType = :modelType ";
		}
		if (refId != null) {
			jpql += "AND c.field.refId = :refId ";
		}
		if (commitId != null) {
			jpql += "AND c.field.commitId = :commitId";
		}
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("repositoryPath", repository.toId());
		if (type != null) {
			attributes.put("modelType", type);
		}
		if (refId != null) {
			attributes.put("refId", refId);
		}
		if (commitId != null) {
			attributes.put("commitId", commitId);
		}
		List<Comment> accessible = new ArrayList<>();
		for (Comment comment : dao.getAll(jpql, attributes)) {
			if (!accessService.canRead(comment))
				continue;
			accessible.add(comment);
		}
		return accessible;
	}

	public Comment get(long id) {
		return dao.get(id);
	}

	public Comment insert(Comment comment) {
		if (!accessService.canCommentIn(comment.repositoryPath))
			throw new UnauthorizedAccessException(comment.repositoryPath, "COMMENT");
		return dao.insert(comment);
	}

	public boolean changeVisibility(long commentId, Role role) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return false;
		if (!accessService.canManage(comment))
			throw new UnauthorizedAccessException(comment.repositoryPath, "MANAGE_COMMENT");
		comment.restrictedToRole = role;
		dao.update(comment);
		return true;
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
			comment.approvedBy = currentUser;
		}
		return dao.update(comment);
	}

	public void delete(long commentId) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return;
		if (!accessService.canManage(comment))
			return;
		dao.delete(comment);
	}

}
