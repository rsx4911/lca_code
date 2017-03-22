package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.openlca.cloud.util.ObjectMap;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.DatasetField;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.CommentService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.UserService;

@Path("comment")
public class CommentResource {

	private final CommentService service;
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public CommentResource(CommentService service, RepositoryService repoService, UserService userService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
	public Response getForDataset(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("type") ModelType type, @PathParam("refId") String refId, @PathParam("commitId") String commitId) {
		Repository repository = repoService.get(group, name);
		List<Comment> comments = service.getAllFor(repository, type, refId, commitId);
		return Respond.ok(comments);
	}

	@POST
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
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
		comment.field.field = map.getString("field");
		comment.restrictedToRole = parseRole(map);
		comment.restrictedTo = parseUsers(map);
		comment.date = Calendar.getInstance().getTime();
		return Respond.ok(comment);
	}

	@PUT
	@Path("{id}")
	public Response changeVisibility(@PathParam("id") long id, Map<String, Object> data) {
		ObjectMap map = ObjectMap.fromMap(data);
		Role role = parseRole(map);
		List<User> users = parseUsers(map);
		boolean changed = false;
		if (users == null || users.isEmpty()) {
			changed = service.changeVisibility(id, role);
		} else {
			changed = service.changeVisibility(id, users);			
		}
		return Respond.ok(Collections.singletonMap("changed", changed));
	}

	private Role parseRole(ObjectMap data) {
		if (!data.containsKey("restrictedToRole"))
			return null;
		return Role.valueOf(data.getString("restrictedToRole"));
	}

	private List<User> parseUsers(ObjectMap data) {
		if (!data.containsKey("restrictedTo"))
			return new ArrayList<>();
		List<User> parsed = new ArrayList<>();
		List<Map<String, Object>> users = data.get("restrictedTo");
		for (Map<String, Object> user : users) {
			User u = new User();
			// id is enough information, JPA will link to whole user
			u.setId(Long.parseLong(user.get("id").toString()));
			parsed.add(u);
		}
		return parsed;
	}

}
