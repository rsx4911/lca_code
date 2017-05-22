package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.util.Comments;

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
	@Path("{group}/{name}/{type}/{refId}")
	public Response getForDataset(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("type") ModelType type, @PathParam("refId") String refId) {
		return getForDataset(group, name, type, refId, null);
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
		result.put("canApprove", accessService.canManageCommentsIn(repository.toId()));
		return Respond.ok(result);
	}

	private List<Map<String, Object>> map(Repository repository, List<Comment> comments) {
		List<Map<String, Object>> mapped = new ArrayList<>();
		DatasetIndex index = indices.get(repository);
		Map<String, String> modelTypeAndIdToPath = new HashMap<>();
		for (DatasetIndexEntry entry : index.getAll()) {
			modelTypeAndIdToPath.put(entry.type.name() + "_" + entry.refId, entry.fullPath);
		}
		for (Comment comment : comments) {
			ObjectMap map = Comments.map(comment);
			String key = comment.field.modelType.name() + "_" + comment.field.refId;
			map.put("dsPath", modelTypeAndIdToPath.get(key));
			mapped.add(map);
		}
		return mapped;
	}

	@POST
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(@PathParam("group") String group, @PathParam("name") String name,
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
		return Respond.ok(map(comment, indices.get(repository)));
	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response edit(@PathParam("id") long id, Map<String, Object> data) {
		Comment comment = service.update(id, data.get("text").toString());
		if (comment == null)
			return Respond.notFound();
		Repository repository = getRepository(comment);
		return Respond.ok(map(comment, indices.get(repository)));
	}

	@PUT
	@Path("{id}/visibility/{role}")
	public Response changeVisibility(@PathParam("id") long id, @PathParam("role") String roleString) {
		Role role = "null".equals(roleString) ? null : Role.valueOf(roleString);
		Comment comment = service.changeVisibility(id, role);
		if (comment == null)
			return Respond.notFound();
		Repository repository = getRepository(comment);
		return Respond.ok(map(comment, indices.get(repository)));
	}

	@PUT
	@Path("{id}/release")
	public Response release(@PathParam("id") long id) {
		Comment comment = service.release(id);
		if (comment == null)
			return Respond.notFound();
		Repository repository = getRepository(comment);
		return Respond.ok(map(comment, indices.get(repository)));
	}

	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") long id) {
		service.delete(id);
		return Respond.ok(Collections.emptyMap());
	}

	private ObjectMap map(Comment comment, DatasetIndex index) {
		ObjectMap map = Comments.map(comment);
		DatasetIndexEntry ds = index.getForId(comment.field.refId, comment.field.commitId);
		map.put("dsPath", ds.fullPath);
		return map;
	}

	private Repository getRepository(Comment comment) {
		String group = comment.repositoryPath.split("/")[0];
		String name = comment.repositoryPath.split("/")[1];
		return repoService.get(group, name);
	}

	private Role parseRole(ObjectMap data) {
		if (!data.containsKey("restrictedToRole"))
			return null;
		return Role.valueOf(data.getString("restrictedToRole"));
	}

}
