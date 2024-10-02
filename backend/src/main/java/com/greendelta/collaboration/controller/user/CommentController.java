package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Comments;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.DatasetField;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.collaboration.util.MetaData;
import com.greendelta.collaboration.util.SearchResults;

@RestController
@RequestMapping("ws/comment")
public class CommentController {

	private final CommentService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final PermissionsService permissions;
	private final NotificationService notificationService;
	private final SettingsService settings;

	public CommentController(CommentService service, RepositoryService repoService, UserService userService,
			PermissionsService permissions, NotificationService notificationService, SettingsService settings) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.permissions = permissions;
		this.notificationService = notificationService;
		this.settings = settings;
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> getForRepository(
			@PathVariable String group,
			@PathVariable String name,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "false") boolean includeReplies) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		try (var repo = repoService.get(group, name)) {
			var comments = includeReplies ? service.getAllFor(repo) : service.getAllTopSorted(repo, filter);
			var result = SearchResults.paged(page, pageSize, comments);
			var mapped = SearchResults.listConvert(result, list -> map(repo, list, true));
			var map = Maps.of(mapped);
			var canApprove = permissions.canManageCommentsIn(repo.path());
			Maps.put(map, "resultInfo.canApprove", canApprove);
			return map;
		}
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public Map<String, Object> getForDataset(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable ModelType type,
			@PathVariable String refId,
			@RequestParam(required = false) String commitId) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		try (var repo = repoService.get(group, name)) {
			var comments = service.getAllFor(repo, type, refId, commitId);
			var result = new HashMap<String, Object>();
			result.put("comments", map(repo, comments, false));
			result.put("canComment", permissions.canCommentIn(repo.path()));
			result.put("canApprove", permissions.canManageCommentsIn(repo.path()));
			return result;
		}
	}

	private List<Map<String, Object>> map(Repository repo, List<Comment> comments, boolean putReplyCount) {
		var mapped = new ArrayList<Map<String, Object>>();
		if (comments.isEmpty())
			return mapped;
		comments.forEach(comment -> {
			var map = Comments.map(comment);
			var field = comment.field;
			var ref = repo.references.get(field.modelType, field.refId, field.commitId);
			map.put("dsPath", ref.category + "/" + MetaData.getName(repo, ref));
			if (putReplyCount) {
				map.put("replyCount", service.getRepliesTo(comment.id).size());
			}
			mapped.add(map);
		});
		return mapped;
	}

	@GetMapping("{id}/replies")
	public List<Map<String, Object>> getReplies(@PathVariable long id) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var comment = service.get(id);
		try (var repo = repoService.get(comment.repositoryPath)) {
			var comments = service.getRepliesTo(id);
			return map(repo, comments, false);
		}
	}

	@PostMapping("{group}/{name}/{type}/{refId}/{commitId}")
	public Map<String, Object> add(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable ModelType type,
			@PathVariable String refId,
			@PathVariable String commitId,
			@RequestBody Map<String, Object> map) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		try (var repo = repoService.get(group, name)) {
			var comment = new Comment();
			comment.repositoryPath = repo.path();
			comment.user = userService.getCurrentUser();
			comment.text = Maps.getString(map, "text");
			comment.field = new DatasetField();
			comment.field.modelType = type;
			comment.field.refId = refId;
			comment.field.commitId = commitId;
			comment.field.path = Maps.getString(map, "path");
			if (comment.field.path == null) {
				comment.field.path = "";
			}
			comment.restrictedToRole = parseRole(map);
			comment.date = Calendar.getInstance().getTime();
			comment.replyTo = service.get(Maps.getLong(map, "replyTo"));
			comment = service.insert(comment);
			if (Maps.getBoolean(map, "released")) {
				comment = service.release(comment.id);
			}
			notificationService.fieldCommented(comment).send();
			return map(comment, repo);
		}
	}

	@PutMapping("{id}")
	public Map<String, Object> edit(
			@PathVariable long id,
			@RequestBody Map<String, Object> map) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var comment = service.update(id, Maps.getString(map, "text"));
		if (comment == null)
			throw Response.notFound();
		try (var repo = repoService.get(comment.repositoryPath)) {
			if (Maps.getBoolean(map, "released")) {
				comment = service.release(id);
			}
			return map(comment, repo);
		}
	}

	@PutMapping("{id}/release")
	public Map<String, Object> release(@PathVariable long id) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var comment = service.release(id);
		if (comment == null)
			throw Response.notFound();
		try (var repo = repoService.get(comment.repositoryPath)) {
			return map(comment, repo);
		}
	}

	@PutMapping("{id}/visibility/{role}")
	public Map<String, Object> changeVisibility(
			@PathVariable long id,
			@PathVariable String role) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var roleValue = "null".equals(role) ? null : Role.valueOf(role);
		var comment = service.changeVisibility(id, roleValue);
		if (comment == null)
			throw Response.notFound();
		try (var repo = repoService.get(comment.repositoryPath)) {
			return map(comment, repo);
		}
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable long id) {
		if (!settings.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		service.delete(id);
	}

	private Map<String, Object> map(Comment comment, Repository repo) {
		var map = Comments.map(comment);
		var field = comment.field;
		var ref = repo.references.get(field.modelType, field.refId, field.commitId);
		map.put("dsPath", ref.category);
		return map;
	}

	private Role parseRole(Map<String, Object> data) {
		if (!data.containsKey("restrictedToRole"))
			return null;
		return Role.valueOf(Maps.getString(data, "restrictedToRole"));
	}

}
