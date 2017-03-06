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

	@Inject
	public CommentService(Dao<Comment> dao, AccessService accessService) {
		this.dao = dao;
		this.accessService = accessService;
	}

	public List<Comment> getAllFor(Repository repository, ModelType type, String refId, String commitId) {
		String jpql = "SELECT c FROM Comment c "
				+ "WHERE c.repositoryPath = :repositoryPath "
				+ "AND c.field.modelType = :modelType "
				+ "AND c.field.refId = :refId "
				+ "AND c.field.commitId = :commitId "
				+ "AND c.field.field = :field";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("repositoryPath", repository.toId());
		attributes.put("modelType", type);
		attributes.put("refId", refId);
		attributes.put("commitId", commitId);
		List<Comment> accessible = new ArrayList<>();
		for (Comment comment : dao.getAll(jpql, attributes)) {
			if (!accessService.canRead(comment))
				continue;
			accessible.add(comment);
		}
		return accessible;
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
		if (!accessService.canCommentIn(comment.repositoryPath))
			throw new UnauthorizedAccessException(comment.repositoryPath, "COMMENT");
		comment.restrictedToRole = role;
		comment.restrictedTo.clear();
		dao.update(comment);
		return true;
	}
	
	public boolean changeVisibility(long commentId, List<User> users) {
		Comment comment = dao.get(commentId);
		if (comment == null)
			return false;
		if (!accessService.canCommentIn(comment.repositoryPath))
			throw new UnauthorizedAccessException(comment.repositoryPath, "COMMENT");
		comment.restrictedToRole = null;
		comment.restrictedTo.clear();
		comment.restrictedTo.addAll(users);
		dao.update(comment);
		return true;		
	}

}
