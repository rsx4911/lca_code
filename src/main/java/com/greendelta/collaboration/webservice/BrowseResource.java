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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.Datasets.Descriptor;
import org.openlca.cloud.api.git.Reference;
import org.openlca.cloud.api.git.References.Entry;
import org.openlca.cloud.api.git.References.EntryType;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;

@Path("public/browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private static final Logger log = LogManager.getLogger(BrowseResource.class);
	private final RepositoryService repoService;
	private final UserService userService;
	private final SettingsService settingsService;

	@Inject
	public BrowseResource(RepositoryService repoService, UserService userService, SettingsService settingsService) {
		this.repoService = repoService;
		this.userService = userService;
		this.settingsService = settingsService;
	}

	@GET
	@Path("{group}/{name}")
	public Response getCategoryContent(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("categoryPath") String categoryPath,
			@QueryParam("filter") String filter,
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("commitId") String commitId) {
		String path = categoryPath != null ? categoryPath : "";
		log.debug("Getting content for path /{} of repository {}/{}", path, group, name);
		Repository repo = repoService.get(group, name);
		List<Entry> entries = repo.references.walk(commitId, categoryPath);
		List<String> typesOrder = settingsService.get(ServerSetting.MODEL_TYPES_ORDER);
		if (path.isEmpty()) {
			List<String> typesHidden = settingsService.get(ServerSetting.MODEL_TYPES_HIDDEN);
			entries = com.greendelta.collaboration.util.Collections.filter(entries,
					e -> typesHidden.contains(e.type.name()));
		}
		List<ObjectMap> mapped = putInfo(entries, repo, commitId, categoryPath);
		Collections.sort(mapped, (m1, m2) -> {
			if (!path.isEmpty())
				return m1.getString("name").toLowerCase().compareTo(m2.getString("name").toLowerCase());
			String t1 = m1.getString("type");
			String t2 = m2.getString("type");
			return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
		});
		return Respond.ok(SearchResults.pagedAndFiltered(page, pageSize, filter, mapped, m -> m.getString("name")));
	}

	private List<ObjectMap> putInfo(List<Entry> entries, Repository repo, String commitId,
			String categoryPath) {
		User user = userService.getCurrentUser();
		boolean loggedIn = user.id != 0;
		Map<String, Commit> commits = new HashMap<>();
		List<ObjectMap> mapped = new ArrayList<>();
		entries.forEach(entry -> {
			ObjectMap map = ObjectMap.fromObject(entry);
			String entryPath = Strings.nullOrEmpty(categoryPath)
					? entry.name
					: categoryPath + "/" + entry.name;
			if (loggedIn) {
				putCommitInfo(map, entry, repo, commits);
			}
			if (entry.typeOfEntry != EntryType.DATASET) {
				map.put("count", repo.references.find().commit(commitId).path(entryPath).count());
			} else {
				putDatasetInfo(map, entry, repo, commitId);
			}
			mapped.add(map);
		});
		return mapped;
	}

	private void putCommitInfo(Map<String, Object> map, Entry entry, Repository repo, Map<String, Commit> commits) {
		Commit commit = commits.computeIfAbsent(entry.commitId, repo.commits::get);
		map.put("commitId", commit.id);
		map.put("commitMessage", commit.message);
		map.put("commitTimestamp", commit.timestamp);
	}

	private void putDatasetInfo(Map<String, Object> map, Entry entry, Repository repo, String commitId) {
		Descriptor descriptor = repo.datasets.getDescriptor(entry);
		if (Strings.nullOrEmpty(descriptor.location)) {
			map.put("name", descriptor.name);
		} else {
			map.put("name", descriptor.name + " - " + descriptor.location);
		}
		if (descriptor.flowType != null) {
			map.put("flowType", descriptor.flowType);
		}
		if (descriptor.processType != null) {
			map.put("processType", descriptor.processType);
		}
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
			return Respond.notFound(type + " " + refId + " not found for commit " + commitId);
		if (commitId == null) {
			commitId = repo.commits.find().model(type, refId).latestId();
		}
		boolean loggedIn = userService.getCurrentUser().id != 0;
		if (!loggedIn && !commit.id.equals(commitId))
			return Respond.unauthorized();
		Reference ref = repo.references.get(type, refId, commit.id);
		String dataset = repo.datasets.get(ref);
		if (Strings.nullOrEmpty(dataset))
			return Respond.notFound(type + " " + refId + " not found for commit " + commitId);
		log.info("Loading {} {} of repository {}/{} (commit id: {})", type, refId, group, name, commitId);
		JsonObject json = new Gson().fromJson(dataset, JsonObject.class);
		json.addProperty("category", ref.category);
		if (loggedIn) {
			json.add("commitId", new JsonPrimitive(commitId));
		}
		return Respond.ok(new Gson().toJson(json));
	}

}
