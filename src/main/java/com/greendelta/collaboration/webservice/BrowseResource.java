package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.Reference;
import org.openlca.cloud.api.git.References.Entry;
import org.openlca.cloud.api.git.References.EntryType;
import org.openlca.core.model.ModelType;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;

@Path("public/browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private static final Logger log = LogManager.getLogger(BrowseResource.class);
	private final RepositoryService repoService;
	private final UserService userService;

	@Inject
	public BrowseResource(RepositoryService repoService, UserService userService) {
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
			@QueryParam("commitId") String commitId) {
		String path = categoryPath != null ? categoryPath : "";
		log.debug("Getting content for path /{} of repository {}/{}", path, group, name);
		Repository repo = repoService.get(group, name);
		List<Entry> entries = repo.references.walk(commitId, categoryPath);
		List<Map<String, Object>> mapped = new ArrayList<>();
		Map<String, Commit> commits = new HashMap<>();
		User user = userService.getCurrentUser();
		boolean loggedIn = user.hasId();
		for (Entry entry : entries) {
			ObjectMap map = ObjectMap.fromObject(entry);
			String entryPath = entry.name;
			if (!Strings.isNullOrEmpty(categoryPath)) {
				entryPath = categoryPath + "/" + entryPath;
			}
			if (loggedIn) {
				String entryCommitId = repo.commits.find().path(entryPath).latestId();
				Commit commit = commits.computeIfAbsent(entryCommitId, repo.commits::get);
				map.put("commitId", commit.id);
				map.put("commitMessage", commit.message);
				map.put("commitTimestamp", commit.timestamp);
			}
			if (entry.typeOfEntry != EntryType.DATASET) {
				map.put("count", repo.references.find().commit(commitId).path(entryPath).count());
			}
			mapped.add(map);
		}
		return Respond.ok(java.util.Collections.singletonMap("entries", mapped));
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
		Commit commit = repo.commits.find().model(type, refId).until(commitId).latest();
		if (commit == null)
			return Respond.notFound(notFoundMessage(type, refId, commitId));
		if (commitId == null) {
			commitId = repo.commits.find().model(type, refId).latestId();
		}
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		if (!loggedIn && !commit.id.equals(commitId))
			return Respond.unauthorized();
		Reference ref = repo.references.get(type, refId, commit.id);
		String dataset = repo.datasets.get(ref);
		if (Strings.isNullOrEmpty(dataset))
			return Respond.notFound(notFoundMessage(type, refId, commitId));
		log.info("Loading {} {} of repository {}/{} (commit id: {})", type, refId, group, name, commitId);
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		BrowseReferenceFiller references = new BrowseReferenceFiller(repo, commitId);
		references.fillReferencedElements(json);
		json.addProperty("category", ref.category);
		if (loggedIn) {
			json.add("commitId", new JsonPrimitive(commitId));
		}
		return Respond.ok(new Gson().toJson(json));
	}

	private String notFoundMessage(ModelType type, String refId, String commitId) {
		String base = type.name() + " " + refId + " not found";
		if (commitId == null)
			return base;
		return base + " for commit id " + commitId;
	}

}
