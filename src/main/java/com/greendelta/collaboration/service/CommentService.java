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
		if (!accessService.canCommentIn(comment.repositoryPath))
			throw new UnauthorizedAccessException(comment.repositoryPath, "COMMENT");
		comment.restrictedToRole = role;
		dao.update(comment);
		return true;
	}

}
