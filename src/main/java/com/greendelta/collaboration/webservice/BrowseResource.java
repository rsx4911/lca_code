package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.util.KeyGen;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.BrowseService.BrowseParameter;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;

@Path("public/browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private final BrowseService service;
	private final RepositoryService repoService;
	private final FetchService fetchService;
	private final HistoryService historyService;
	private final UserService userService;

	@Inject
	public BrowseResource(BrowseService service, RepositoryService repoService, FetchService fetchService,
			HistoryService historyService, UserService userService) {
		this.service = service;
		this.repoService = repoService;
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.userService = userService;
	}

	@GET
	@Path("{group}/{name}")
	public Response getCategoryContent(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("categoryPath") String categoryPath,
			@QueryParam("filter") String filter,
			@QueryParam("commitId") String commitId,
			@QueryParam("showDeleted") @DefaultValue("false") boolean showDeleted) {
		Repository repo = repoService.get(group, name);
		List<ObjectMap> content = null;
		if (Strings.isNullOrEmpty(categoryPath)) {
			content = service.getRootContent(new BrowseParameter(repo, commitId).includeDeleted(showDeleted));
		} else {
			ModelType type = getModelType(categoryPath);
			BrowseParameter params = new BrowseParameter(repo, filter, commitId).includeDeleted(showDeleted);
			content = getCategoryContent(type, toId(categoryPath), params);
		}
		if (content == null)
			content = new ArrayList<>();
		if (userService.getCurrentUser().getId() == 0) {
			content = com.greendelta.collaboration.util.Collections.convert(content, (entry) -> {
				entry.remove("commitTimestamp", "commitMessage", "commitId");
				return entry;
			});
		}
		return Respond.ok(Collections.singletonMap("entries", content));
	}

	private ModelType getModelType(String categoryPath) {
		if (categoryPath.contains("/"))
			return ModelType.valueOf(categoryPath.substring(0, categoryPath.indexOf('/')));
		return ModelType.valueOf(categoryPath);
	}

	private String toId(String categoryPath) {
		if (categoryPath.contains("/"))
			return KeyGen.get(categoryPath.split("/"));
		return categoryPath;
	}

	private List<ObjectMap> getCategoryContent(ModelType type, String categoryRefId, BrowseParameter params) {
		for (ModelType t : ModelTypes.SORTED) {
			if (!t.name().equals(categoryRefId))
				continue;
			return service.getUncategorized(type, params);
		}
		List<ObjectMap> content = service.getForCategory(categoryRefId, params);
		if (content.isEmpty() || service.getDataset(params.repo, categoryRefId, params.commitId) == null)
			return null;
		return content;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("categoryInfo/{group}/{name}")
	public Response categoryInfo(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("categoryPath") String categoryPath,
			@QueryParam("commitId") String commitId) {
		if (categoryPath == null || categoryPath.isEmpty())
			return Respond.ok(new HashMap<>());
		if (!categoryPath.contains("/"))
			return Respond.ok(Collections.emptyMap());
		Repository repo = repoService.get(group, name);
		String refId = toId(categoryPath);
		String category = categoryPath.substring(categoryPath.indexOf('/') + 1);
		ObjectMap entry = service.getDataset(repo, refId, commitId);
		if (entry == null)
			return Respond.notFound("No category '" + category + "' found");
		List<String> categories = entry.get("categories") != null ? new ArrayList<String>(entry.get("categories"))
				: new ArrayList<>();
		categories.add(entry.get("name"));
		Map<String, Object> result = new HashMap<>();
		result.put("id", refId);
		result.put("category", categories);
		result.put("deleted", entry.get("action") == IndexAction.DELETE ? "true" : "false");
		return Respond.ok(result);
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@QueryParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		String lastCommitId = getLastCommitId(repo, type, refId, commitId);
		if (lastCommitId == null) {
			String message = notFoundMessage(type, refId, commitId);
			return Respond.notFound(message);
		}
		if (commitId == null) {
			commitId = historyService.getLastCommit(repo).id;
		}
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		String dataset = fetchService.getDataset(repo, type, refId, lastCommitId);
		if (Strings.isNullOrEmpty(dataset)) {
			Map<String, Object> descriptor = new HashMap<>();
			Map<String, Object> entry = service.getDataset(repo, refId, lastCommitId);
			descriptor.put("@id", refId);
			descriptor.put("@type", type.getModelClass().getSimpleName());
			descriptor.put("name", entry.get("name"));
			if (loggedIn) {
				descriptor.put("commitId", commitId);
			}
			descriptor.put("deleted", true);
			return Respond.ok(descriptor);
		}
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		BrowseReferenceFiller references = new BrowseReferenceFiller(service, fetchService, repo, commitId);
		references.fillReferencedElements(json);
		ObjectMap map = ObjectMap.fromJson(new Gson().toJson(json));
		if (loggedIn) {
			map.put("commitId", commitId);
		}
		return Respond.ok(map);
	}

	@GET
	@Path("count/{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCount(@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("categoryPath") String path,
			@QueryParam("commitId") String commitId,
			@QueryParam("showDeleted") @DefaultValue("false") boolean showDeleted) {
		String typeAsString = path.contains("/") ? path.substring(0, path.indexOf('/')) : path;
		String category = path.contains("/") ? path.substring(path.indexOf('/') + 1) : null;
		ModelType type = ModelTypes.parse(typeAsString);
		Repository repo = repoService.get(group, name);
		if (commitId == null) {
			commitId = historyService.getLastCommit(repo).id;
		}
		long count = service.getCount(type, category, new BrowseParameter(repo, commitId).includeDeleted(showDeleted));
		Map<String, Object> result = new HashMap<>();
		result.put("count", count);
		result.put("path", path);
		return Respond.ok(result);
	}

	private String notFoundMessage(ModelType type, String refId, String commitId) {
		return notFoundMessage(type, refId, commitId, null);
	}

	private String notFoundMessage(ModelType type, String refId, String commitId, String filename) {
		String base = "";
		if (!Strings.isNullOrEmpty(filename))
			base = "Binary file " + filename + " of ";
		base += type.name() + " " + refId + " not found";
		if (commitId == null)
			return base;
		return base + " for commit id " + commitId;
	}

	private String getLastCommitId(Repository repo, ModelType type, String refId, String commitId) {
		Commit commit = historyService.getLastCommit(repo, type, refId, commitId);
		if (commit == null)
			return null;
		return commit.id;
	}
}
