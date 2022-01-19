package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.CommentService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;

@RestController
@RequestMapping("ws/comment")
public class CommentController {

	private final CommentService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final AccessService accessService;
	private final NotificationService notificationService;
	private final SettingsService settingsService;

	@Autowired
	public CommentController(CommentService service, RepositoryService repoService, UserService userService,
			AccessService accessService, NotificationService notificationService, SettingsService settingsService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.accessService = accessService;
		this.notificationService = notificationService;
		this.settingsService = settingsService;
	}

	@GetMapping("{group}/{name}")
	public Map<String, Object> getForRepository(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(name = "includeReplies", defaultValue = "false") boolean includeReplies) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		try (var repo = repoService.get(group, name)) {
			var comments = includeReplies ? service.getAllFor(repo) : service.getAllTopSorted(repo, filter);
			var result = SearchResults.paged(page, pageSize, comments);
			var mapped = SearchResults.listConvert(result, list -> map(repo, list, true));
			var map = ObjectMap.fromObject(mapped);
			var canApprove = accessService.canManageCommentsIn(repo.path());
			map.put("resultInfo.canApprove", canApprove);
			return map;
		}
	}

	@GetMapping("{group}/{name}/{type}/{refId}")
	public Map<String, Object> getForDataset(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@RequestParam(name = "commitId", required = false) String commitId) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		try (var repo = repoService.get(group, name)) {
			var comments = service.getAllFor(repo, type, refId, commitId);
			var result = new HashMap<String, Object>();
			result.put("comments", map(repo, comments, false));
			result.put("canComment", accessService.canCommentIn(repo.path()));
			result.put("canApprove", accessService.canManageCommentsIn(repo.path()));
			return result;
		}
	}

	private List<ObjectMap> map(Repository repo, List<Comment> comments, boolean putReplyCount) {
		var mapped = new ArrayList<ObjectMap>();
		if (comments.isEmpty())
			return mapped;
		comments.forEach(comment -> {
			var map = Comments.map(comment);
			var field = comment.field;
			var ref = repo.references().get(field.modelType, field.refId, field.commitId);
			map.put("dsPath", ref.category);
			if (putReplyCount) {
				map.put("replyCount", service.getRepliesTo(comment.id).size());
			}
			mapped.add(map);
		});
		return mapped;
	}

	@GetMapping("{id}/replies")
	public List<ObjectMap> getReplies(@PathVariable("id") long id) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var comment = service.get(id);
		try (var repo = repoService.get(comment.repositoryPath)) {
			var comments = service.getRepliesTo(id);
			return map(repo, comments, false);
		}
	}

	@PostMapping("{group}/{name}/{type}/{refId}/{commitId}")
	public Map<String, Object> add(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@PathVariable("type") ModelType type,
			@PathVariable("refId") String refId,
			@PathVariable("commitId") String commitId,
			@RequestBody Map<String, Object> data) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var map = ObjectMap.fromMap(data);
		try (var repo = repoService.get(group, name)) {
			var comment = new Comment();
			comment.repositoryPath = repo.path();
			comment.user = userService.getCurrentUser();
			comment.text = map.getString("text");
			comment.field = new DatasetField();
			comment.field.modelType = type;
			comment.field.refId = refId;
			comment.field.commitId = commitId;
			comment.field.path = map.getString("path");
			if (comment.field.path == null) {
				comment.field.path = "";
			}
			comment.restrictedToRole = parseRole(map);
			comment.date = Calendar.getInstance().getTime();
			comment.replyTo = service.get(map.getLong("replyTo"));
			comment = service.insert(comment);
			if (map.getBoolean("released")) {
				comment = service.release(comment.id);
			}
			notificationService.fieldCommented(comment).send();
			return map(comment, repo);
		}
	}

	@PutMapping("{id}")
	public Map<String, Object> edit(
			@PathVariable("id") long id,
			@RequestBody Map<String, Object> data) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var map = ObjectMap.fromMap(data);
		var comment = service.update(id, map.getString("text"));
		if (comment == null)
			throw Response.notFound();
		try (var repo = repoService.get(comment.repositoryPath)) {
			if (map.getBoolean("released")) {
				comment = service.release(id);
			}
			return map(comment, repo);
		}
	}

	@PutMapping("{id}/release")
	public Map<String, Object> release(@PathVariable("id") long id) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
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
			@PathVariable("id") long id,
			@PathVariable("role") String roleString) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		var role = "null".equals(roleString) ? null : Role.valueOf(roleString);
		var comment = service.changeVisibility(id, role);
		if (comment == null)
			throw Response.notFound();
		try (var repo = repoService.get(comment.repositoryPath)) {
			return map(comment, repo);
		}
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") long id) {
		if (!settingsService.is(ServerSetting.COMMENTS_ENABLED))
			throw Response.unavailable("Comment feature not enabled");
		service.delete(id);
	}

	private ObjectMap map(Comment comment, Repository repo) {
		var map = Comments.map(comment);
		var field = comment.field;
		var ref = repo.references().get(field.modelType, field.refId, field.commitId);
		map.put("dsPath", ref.category);
		return map;
	}

	private Role parseRole(ObjectMap data) {
		if (!data.containsKey("restrictedToRole"))
			return null;
		return Role.valueOf(data.getString("restrictedToRole"));
	}

}
