package com.greendelta.cloud.webservice;

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

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.util.ObjectMap;
import org.openlca.core.model.ModelType;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndexEntry;
import com.greendelta.cloud.service.BrowseService;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private final BrowseService service;
	private final RepositoryService repoService;
	private final FetchService fetchService;
	private final HistoryService historyService;

	@Inject
	public BrowseResource(BrowseService service, RepositoryService repoService, FetchService fetchService,
			HistoryService historyService) {
		this.service = service;
		this.repoService = repoService;
		this.fetchService = fetchService;
		this.historyService = historyService;
	}

	@GET
	@Path("{group}/{name}")
	public Response getRootContent(@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = repoService.get(group, name);
		return Respond.ok(appendParentRefId(repo, service.getRootContent(repo)));
	}

	@GET
	@Path("{group}/{name}/{categoryRefId}")
	public Response getCategoryContent(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("categoryRefId") String categoryRefId, @QueryParam("filter") String filter) {
		Repository repo = repoService.get(group, name);
		for (ModelType type : ModelType.values()) {
			if (type.name().equals(categoryRefId)) {
				List<DatasetIndexEntry> content = service.getCategoryContent(repo, type, filter);
				return Respond.ok(appendParentRefId(repo, content));
			}
		}
		List<DatasetIndexEntry> content = service.getCategoryContent(repo, categoryRefId, filter);
		if (content.isEmpty())
			if (service.categoryExists(repo, categoryRefId))
				Respond.ok(appendParentRefId(repo, content));
			else
				return Respond.notFound();
		return Respond.ok(appendParentRefId(repo, content));
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@PathParam("group") String group,
			@PathParam("name") String name, @PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		if (commitId.equals("null"))
			commitId = getLastCommitId(repo, type, refId);
		if (commitId == null) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		String dataset = fetchService.getDataset(repo, type, refId, commitId);
		if (dataset == null) {
			String message = notFoundMessage(type, refId, commitId);
			return Respond.notFound(message);
		}
		ObjectMap map = ObjectMap.fromJson(dataset);
		if (map.containsKey("category"))
			map.put("category.name", getFullPath(repo, map.get("category.@id")));
		if (type == ModelType.PROCESS) {
			putFlowCategories(repo, map);
			putSocialIndicators(repo, map);
		}
		return Respond.ok(map);
	}

	@SuppressWarnings("unchecked")
	private void putFlowCategories(Repository repo, ObjectMap map) {
		List<Map<String, Object>> exchanges = (List<Map<String, Object>>) map.get("exchanges");
		if (exchanges == null)
			return;
		for (Map<String, Object> exchange : exchanges) {
			if (!exchange.containsKey("flow"))
				continue;
			Map<String, Object> flow = (Map<String, Object>) exchange.get("flow");
			String refId = (String) flow.get("@id");
			// last element in path is the flow name itself
			String fullPath = getFullPath(repo, refId);
			if (!fullPath.contains("/"))
				continue;
			fullPath = fullPath.substring(0, fullPath.lastIndexOf("/"));
			flow.put("category", fullPath);
		}
	}

	@SuppressWarnings("unchecked")
	private void putSocialIndicators(Repository repo, ObjectMap map) {
		List<Map<String, Object>> aspects = (List<Map<String, Object>>) map.get("socialAspects");
		if (aspects == null)
			return;
		for (Map<String, Object> aspect : aspects) {
			if (!aspect.containsKey("socialIndicator"))
				continue;
			Map<String, Object> indicator = (Map<String, Object>) aspect.get("socialIndicator");
			String refId = (String) indicator.get("@id");
			String commitId = getLastCommitId(repo, ModelType.SOCIAL_INDICATOR, refId);
			aspect.put("socialIndicator",
					ObjectMap.fromJson(fetchService.getDataset(repo, ModelType.SOCIAL_INDICATOR, refId, commitId)));
		}
	}

	private String getFullPath(Repository repo, String refId) {
		if (refId == null)
			return "";
		DatasetIndexEntry entry = service.getCategory(repo, refId);
		if (entry == null)
			return "";
		return entry.fullPath;
	}

	private String getLastCommitId(Repository repo, ModelType type, String refId) {
		Commit commit = historyService.getLastCommit(repo, type, refId);
		if (commit == null)
			return null;
		return commit.id;
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

	private Map<String, Object> appendParentRefId(Repository repo, List<?> entries) {
		String parentRefId = entries.size() == 0 ? null : getParentRefId(repo, entries.get(0));
		Map<String, Object> clientData = new HashMap<>();
		clientData.put("entries", entries);
		clientData.put("parentRefId", parentRefId);
		return clientData;
	}

	private String getParentRefId(Repository repo, Object obj) {
		if (!(obj instanceof DatasetIndexEntry))
			return null;
		DatasetIndexEntry entry = (DatasetIndexEntry) obj;
		if (Strings.isNullOrEmpty(entry.categoryRefId))
			return null;
		String parent = fetchService.getDataset(repo, ModelType.CATEGORY, entry.categoryRefId, entry.commitId);
		String parentRefId = ObjectMap.fromJson(parent).get("category.@id");
		if (parentRefId == null)
			return entry.categoryType.name();
		return parentRefId;
	}

}
