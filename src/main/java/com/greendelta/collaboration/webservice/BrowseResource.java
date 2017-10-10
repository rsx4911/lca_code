package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Collections;
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
import org.openlca.core.model.ModelType;
import org.openlca.util.KeyGen;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.BrowseService;
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
	public Response getCategoryContent(
			@PathParam("group") String group, 
			@PathParam("name") String name,
			@QueryParam("categoryPath") String categoryPath, 
			@QueryParam("filter") String filter) {
		Repository repo = repoService.get(group, name);
		List<?> content = null;
		if (Strings.isNullOrEmpty(categoryPath))
			content = getRootContent(repo);
		else
			content = getCategoryContent(repo, toId(categoryPath), filter);
		if (content == null)
			return Respond.notFound();
		return Respond.ok(Collections.singletonMap("entries", content));
	}

	private String toId(String categoryPath) {
		if (categoryPath.contains("/"))
			return KeyGen.get(categoryPath.split("/"));
		return categoryPath;
	}

	private List<ModelType> getRootContent(Repository repo) {
		return service.getRootContent(repo);
	}

	private List<IndexEntry> getCategoryContent(Repository repo, String categoryRefId, String filter) {
		for (ModelType type : ModelTypes.SORTED) {
			if (!type.name().equals(categoryRefId))
				continue;
			List<IndexEntry> content = service.getUncategorized(repo, type, filter);
			return filterDeleted(repo, content);
		}
		List<IndexEntry> content = service.getForCategory(repo, categoryRefId, filter);
		if (content.isEmpty())
			if (service.hasDataset(repo, categoryRefId))
				return filterDeleted(repo, content);
			else
				return null;
		return filterDeleted(repo, content);
	}

	private List<IndexEntry> filterDeleted(Repository repo, List<IndexEntry> entries) {
		List<IndexEntry> notDeleted = new ArrayList<>();
		for (IndexEntry entry : entries) {
			if (entry.action == IndexAction.DELETE)
				continue;
			notDeleted.add(entry);
		}
		return notDeleted;
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(
			@PathParam("group") String group,
			@PathParam("name") String name, 
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		commitId = getLastCommitId(repo, type, refId, commitId);
		if (commitId == null) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		String dataset = fetchService.getDataset(repo, type, refId, commitId);
		if (Strings.isNullOrEmpty(dataset)) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		ObjectMap map = ObjectMap.fromJson(dataset);
		if (map.containsKey("category"))
			map.put("category.name", getFullPath(repo, ModelType.CATEGORY, map.get("category.@id"), commitId));
		if (type == ModelType.PROCESS) {
			putFlowCategories(repo, map, commitId);
			putSocialIndicators(repo, map, commitId);
		} else if (type == ModelType.FLOW) {
			putReferenceUnits(repo, map, commitId);
		}
		map.put("commitId", commitId);
		return Respond.ok(map);
	}

	@SuppressWarnings("unchecked")
	private void putFlowCategories(Repository repo, ObjectMap map, String commitId) {
		List<Map<String, Object>> exchanges = (List<Map<String, Object>>) map.get("exchanges");
		if (exchanges == null)
			return;
		for (Map<String, Object> exchange : exchanges) {
			if (!exchange.containsKey("flow"))
				continue;
			Map<String, Object> flow = (Map<String, Object>) exchange.get("flow");
			String refId = (String) flow.get("@id");
			String name = (String) flow.get("name");
			// last element in path is the flow name itself
			String flowCommitId = getLastCommitId(repo, ModelType.FLOW, refId, commitId);
			String fullPath = getFullPath(repo, ModelType.FLOW, refId, flowCommitId);
			if (!fullPath.contains("/"))
				continue;
			fullPath = fullPath.substring(0, fullPath.length() - name.length() - 1);
			flow.put("category", fullPath);
		}
	}

	@SuppressWarnings("unchecked")
	private void putSocialIndicators(Repository repo, ObjectMap map, String commitId) {
		List<Map<String, Object>> aspects = (List<Map<String, Object>>) map.get("socialAspects");
		if (aspects == null)
			return;
		for (Map<String, Object> aspect : aspects) {
			if (!aspect.containsKey("socialIndicator"))
				continue;
			Map<String, Object> indicator = (Map<String, Object>) aspect.get("socialIndicator");
			String refId = (String) indicator.get("@id");
			String cId = getLastCommitId(repo, ModelType.SOCIAL_INDICATOR, refId, commitId);
			aspect.put("socialIndicator",
					ObjectMap.fromJson(fetchService.getDataset(repo, ModelType.SOCIAL_INDICATOR, refId, cId)));
		}
	}

	@SuppressWarnings("unchecked")
	private void putReferenceUnits(Repository repo, ObjectMap map, String commitId) {
		List<Map<String, Object>> factors = (List<Map<String, Object>>) map.get("flowProperties");
		if (factors == null)
			return;
		for (Map<String, Object> factor : factors) {
			if (!factor.containsKey("flowProperty"))
				continue;
			Map<String, Object> property = (Map<String, Object>) factor.get("flowProperty");
			String refId = (String) property.get("@id");
			String cId = getLastCommitId(repo, ModelType.FLOW_PROPERTY, refId, commitId);
			String data = fetchService.getDataset(repo, ModelType.FLOW_PROPERTY, refId, cId);
			if (data == null)
				continue;
			Map<String, Object> flowProperty = ObjectMap.fromJson(data);
			Map<String, Object> unitGroup = (Map<String, Object>) flowProperty.get("unitGroup");
			if (unitGroup == null)
				continue;
			refId = (String) unitGroup.get("@id");
			cId = getLastCommitId(repo, ModelType.UNIT_GROUP, refId, commitId);
			data = fetchService.getDataset(repo, ModelType.UNIT_GROUP, refId, cId);
			if (data == null)
				continue;
			unitGroup = ObjectMap.fromJson(data);
			List<Map<String, Object>> units = (List<Map<String, Object>>) unitGroup.get("units");
			if (units == null)
				continue;
			for (Map<String, Object> unit : units) {
				if (!unit.containsKey("referenceUnit"))
					continue;
				if (Boolean.parseBoolean(unit.get("referenceUnit").toString()) == false)
					continue;
				factor.put("referenceUnit", unit.get("name"));
				break;
			}
		}
	}

	private String getFullPath(Repository repo, ModelType type, String refId, String commitId) {
		if (refId == null)
			return "";
		if (commitId == null)
			return "";
		commitId = getLastCommitId(repo, type, refId, commitId);
		IndexEntry entry = service.getDataset(repo, type, refId, commitId);
		if (entry == null)
			return "";
		return entry.fullPath;
	}

	private String getLastCommitId(Repository repo, ModelType type, String refId, String commitId) {
		if (commitId.equals("null"))
			commitId = null;
		if (commitId != null && service.hasDataset(repo, type, refId, commitId))
			return commitId;
		Commit commit = historyService.getLastCommit(repo, type, refId, commitId);
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

}
