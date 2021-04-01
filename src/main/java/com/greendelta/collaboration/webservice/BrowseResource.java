package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.util.KeyGen;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.BrowseService.BrowseParameter;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;

@Path("public/browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private static final Logger log = LogManager.getLogger(BrowseResource.class);
	private final BrowseService service;
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public BrowseResource(BrowseService service, RepositoryService repoService, UserService userService) {
		this.service = service;
		this.repoService = repoService;
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
		String path = categoryPath != null ? categoryPath : "";
		log.debug("Getting content for path /{} of repository {}/{}", path, group, name);
		Repository repo = repoService.get(group, name);
		List<ObjectMap> content = null;
		if (Strings.isNullOrEmpty(categoryPath)) {
			content = service.getRootContent(new BrowseParameter(repo, commitId).includeDeleted(showDeleted));
		} else {
			ModelType type = getModelType(categoryPath);
			BrowseParameter params = new BrowseParameter(repo, filter, commitId).includeDeleted(showDeleted);
			if (type.name().equals(categoryPath)) {
				content = getRootCategoryContent(type, params);
			} else {
				content = getCategoryContent(type, toId(categoryPath), params);
			}
		}
		if (content == null)
			content = new ArrayList<>();
		if (userService.getCurrentUser().getId() == 0) {
			content = com.greendelta.collaboration.util.Collections.convertToList(content, (entry) -> {
				entry.remove("commitTimestamp", "commitMessage", "commitId");
				return entry;
			});
		}
		for (ObjectMap entry : content) {
			entry.put("count", getCount(entry, new BrowseParameter(repo, commitId).includeDeleted(showDeleted)));
		}
		return Respond.ok(Collections.singletonMap("entries", content));
	}

	private long getCount(ObjectMap entry, BrowseParameter params) {
		ModelType type = ModelType.valueOf(entry.getString("type"));
		String refId = entry.getString("refId");
		if (type != ModelType.CATEGORY && !Strings.isNullOrEmpty(refId))
			return -1;
		if (type == ModelType.CATEGORY) {
			type = ModelType.valueOf(entry.getString("categoryType"));
		}
		String fullPath = entry.getString("fullPath");
		return service.getCount(type, fullPath, params);
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

	private List<ObjectMap> getRootCategoryContent(ModelType type, BrowseParameter params) {
		if (type == ModelType.CATEGORY || !Arrays.asList(ModelType.categorized()).contains(type))
			return null;
		return service.getUncategorized(type, params);
	}

	private List<ObjectMap> getCategoryContent(ModelType type, String categoryRefId, BrowseParameter params) {
		if (service.getMostRecent(params.repo, ModelType.CATEGORY, categoryRefId, params.commitId) == null)
			return null;
		List<ObjectMap> content = service.getForCategory(type, categoryRefId, params);
		if (content.isEmpty())
			return null;
		return content;
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
		Commit commit = repo.commits.getLast(type, refId, commitId);
		if (commit == null)
			return Respond.notFound(notFoundMessage(type, refId, commitId));
		if (commitId == null) {
			commitId = repo.commits.getLastId(type, refId);
		}
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		if (!loggedIn && !commit.id.equals(commitId))
			return Respond.unauthorized();
		String dataset = repo.datasets.get(type, refId, commit.id);
		if (Strings.isNullOrEmpty(dataset)) {
			Map<String, Object> descriptor = new HashMap<>();
			IndexEntry entry = service.getMostRecent(repo, type, refId, commit.id);
			descriptor.put("@id", refId);
			descriptor.put("@type", type.getModelClass().getSimpleName());
			descriptor.put("name", entry.name);
			if (loggedIn) {
				descriptor.put("commitId", commitId);
			}
			descriptor.put("deleted", true);
			return Respond.ok(descriptor);
		}
		log.info("Loading {} {} of repository {}/{} (commit id: {})", type, refId, group, name, commitId);
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		BrowseReferenceFiller references = new BrowseReferenceFiller(service, repo, commitId);
		references.fillReferencedElements(json);
		if (loggedIn) {
			json.add("commitId", new JsonPrimitive(commitId));
		}
		return Respond.ok(new Gson().toJson(json));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("categoryInfo/{group}/{name}")
	public Response categoryInfo(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("categoryPath") String categoryPath,
			@QueryParam("commitId") String commitId) {
		log.debug("Getting category info for {} of repository {}/{}", categoryPath, group, name);
		if (categoryPath == null || categoryPath.isEmpty())
			return Respond.ok(new HashMap<>());
		if (!categoryPath.contains("/"))
			return Respond.ok(Collections.emptyMap());
		Repository repo = repoService.get(group, name);
		String refId = toId(categoryPath);
		String category = categoryPath.substring(categoryPath.indexOf('/') + 1);
		IndexEntry entry = service.getMostRecent(repo, ModelType.CATEGORY, refId, commitId);
		if (entry == null)
			return Respond.notFound("No category '" + category + "' found");
		List<String> categories = new ArrayList<>();
		if (entry.categories != null) {
			categories.addAll(entry.categories);
		}
		categories.add(entry.name);
		Map<String, Object> result = new HashMap<>();
		result.put("id", refId);
		result.put("category", categories);
		result.put("deleted", entry.action == IndexAction.DELETE ? "true" : "false");
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

}
