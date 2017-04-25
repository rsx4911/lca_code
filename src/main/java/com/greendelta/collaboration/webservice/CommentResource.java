package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.util.ObjectMap;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.index.DatasetIndex;
import com.greendelta.collaboration.index.DatasetIndexEntry;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.DatasetField;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.service.AccessService;
import com.greendelta.collaboration.service.CommentService;
import com.greendelta.collaboration.service.NotificationService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryIndices;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.UserService;

@Path("comment")
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

	private final CommentService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final AccessService accessService;
	private final NotificationService notificationService;
	private final RepositoryIndices indices;

	@Inject
	public CommentResource(CommentService service, RepositoryService repoService, UserService userService,
			AccessService accessService, NotificationService notificationService, RepositoryIndices indices) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.accessService = accessService;
		this.notificationService = notificationService;
		this.indices = indices;
	}

	@GET
	@Path("{group}/{name}")
	public Response getForRepository(@PathParam("group") String group, @PathParam("name") String name) {
		return getForDataset(group, name, null, null, null);
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
	public Response getForDataset(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("type") ModelType type, @PathParam("refId") String refId, @PathParam("commitId") String commitId) {
		Repository repository = repoService.get(group, name);
		List<Comment> comments = service.getAllFor(repository, type, refId, commitId);
		Map<String, Object> result = new HashMap<>();
		result.put("comments", map(repository, comments));
		result.put("canComment", accessService.canCommentIn(repository.toId()));
		return Respond.ok(result);
	}

	private List<Map<String, Object>> map(Repository repository, List<Comment> comments) {
		List<Map<String, Object>> mapped = new ArrayList<>();
		DatasetIndex index = indices.get(repository);
		for (Comment comment : comments) {
			ObjectMap map = ObjectMap.fromObject(comment);
			DatasetIndexEntry ds = index.getForId(comment.field.refId, comment.field.commitId);
			map.put("dsPath", ds.fullPath);
			mapped.add(map);
		}
		return mapped;
	}

	@POST
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComment(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("type") ModelType type, @PathParam("refId") String refId,
			@PathParam("commitId") String commitId, Map<String, Object> data) {
		ObjectMap map = ObjectMap.fromMap(data);
		Repository repository = repoService.get(group, name);
		Comment comment = new Comment();
		comment.repositoryPath = repository.toId();
		comment.user = userService.getCurrentUser();
		comment.text = map.getString("text");
		comment.field = new DatasetField();
		comment.field.modelType = type;
		comment.field.refId = refId;
		comment.field.commitId = commitId;
		comment.field.path = map.getString("path");
		if (comment.field.path == null)
			comment.field.path = "";
		comment.restrictedToRole = parseRole(map);
		comment.date = Calendar.getInstance().getTime();
		comment.replyTo = service.get(map.getLong("replyTo"));
		comment = service.insert(comment);
		notificationService.fieldCommented(comment).send();
		return Respond.ok(comment);
	}

	@PUT
	@Path("{id}/{role}")
	public Response changeVisibility(@PathParam("id") long id, @PathParam("role") String roleString) {
		Role role = "null".equals(roleString) ? null : Role.valueOf(roleString);
		boolean changed = service.changeVisibility(id, role);
		return Respond.ok(Collections.singletonMap("changed", changed));
	}

	private Role parseRole(ObjectMap data) {
		if (!data.containsKey("restrictedToRole"))
			return null;
		return Role.valueOf(data.getString("restrictedToRole"));
	}

}
