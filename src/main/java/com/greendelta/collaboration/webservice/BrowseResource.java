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
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.index.ProcessIndexEntry.ProcessType;
import com.greendelta.collaboration.service.BrowseService;
import com.greendelta.collaboration.service.BrowseService.BrowseParameter;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;

@Path("public/browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private final BrowseService service;
	private final SearchService searchService;
	private final RepositoryService repoService;
	private final FetchService fetchService;
	private final HistoryService historyService;

	@Inject
	public BrowseResource(BrowseService service, SearchService searchService, RepositoryService repoService,
			FetchService fetchService, HistoryService historyService) {
		this.service = service;
		this.searchService = searchService;
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
			@QueryParam("filter") String filter,
			@QueryParam("commitId") String commitId,
			@QueryParam("showDeleted") @DefaultValue("false") boolean showDeleted) {
		Repository repo = repoService.get(group, name);
		List<?> content = null;
		if (Strings.isNullOrEmpty(categoryPath)) {
			content = service.getRootContent(new BrowseParameter(repo, commitId, showDeleted));
		} else {
			ModelType type = getModelType(categoryPath);
			BrowseParameter params = new BrowseParameter(repo, filter, commitId, showDeleted);
			content = getCategoryContent(type, toId(categoryPath), params);
		}
		if (content == null)
			content = new ArrayList<>();
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
	public Response categoryDeleted(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("categoryPath") String categoryPath,
			@QueryParam("commitId") String commitId) {
		if (categoryPath == null || categoryPath.isEmpty())
			return Respond.ok(new HashMap<>());
		Repository repo = repoService.get(group, name);
		if (!categoryPath.contains("/")) {
			if (service.getAll(repo, ModelType.valueOf(categoryPath)).isEmpty())
				return Respond.ok(Collections.singletonMap("deleted", true));
			return Respond.ok(Collections.singletonMap("deleted", false));
		}
		String refId = toId(categoryPath);
		ObjectMap entry = service.getDataset(repo, refId, commitId);
		Map<String, Object> result = new HashMap<>();
		result.put("id", refId);
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
		CommitCache commits = new CommitCache(repo);
		commitId = commits.getLastCommitId(type, refId, commitId);
		if (commitId == null) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		String dataset = fetchService.getDataset(repo, type, refId, commitId);
		if (Strings.isNullOrEmpty(dataset)) {
			Map<String, Object> descriptor = new HashMap<>();
			Map<String, Object> entry = service.getDataset(repo, type, refId, commitId);
			descriptor.put("@id", refId);
			descriptor.put("@type", type.getModelClass().getSimpleName());
			descriptor.put("name", entry.get("name"));
			descriptor.put("commitId", commitId);
			descriptor.put("deleted", true);
			return Respond.ok(descriptor);
		}
		ObjectMap map = ObjectMap.fromJson(dataset);
		if (map.containsKey("category"))
			map.put("category.name", getFullPath(commits, repo, ModelType.CATEGORY, map.get("category.@id"), commitId));
		if (type == ModelType.PROCESS) {
			List<Map<String, Object>> exchanges = map.get("exchanges");
			List<Map<String, Object>> aspects = map.get("socialAspects");
			putFlowCategories(commits, repo, commitId, exchanges);
			putProviderTypes(commits, repo, commitId, exchanges);
			putSocialIndicators(commits, repo, commitId, aspects);
		} else if (type == ModelType.IMPACT_CATEGORY) {
			List<Map<String, Object>> factors = map.get("impactFactors");
			putFlowCategories(commits, repo, commitId, factors);
		} else if (type == ModelType.FLOW) {
			putReferenceUnits(commits, repo, map, commitId);
		}
		map.put("commitId", commitId);
		return Respond.ok(map);
	}

	private void putFlowCategories(CommitCache commits, Repository repo, String commitId,
			List<Map<String, Object>> elements) {
		if (elements == null)
			return;
		for (Map<String, Object> element : elements) {
			if (!element.containsKey("flow"))
				continue;
			@SuppressWarnings("unchecked")
			Map<String, Object> flow = (Map<String, Object>) element.get("flow");
			String refId = (String) flow.get("@id");
			String name = (String) flow.get("name");
			// last element in path is the flow name itself
			String fullPath = getFullPath(commits, repo, ModelType.FLOW, refId, commitId);
			if (!fullPath.contains("/"))
				continue;
			fullPath = fullPath.substring(0, fullPath.length() - name.length() - 1);
			flow.put("category", fullPath);
		}
	}

	private void putProviderTypes(CommitCache commits, Repository repo, String commitId,
			List<Map<String, Object>> elements) {
		if (elements == null)
			return;
		for (Map<String, Object> element : elements) {
			if (!element.containsKey("defaultProvider"))
				continue;
			@SuppressWarnings("unchecked")
			Map<String, Object> provider = (Map<String, Object>) element.get("defaultProvider");
			String refId = (String) provider.get("@id");
			String providerCommitId = commits.getLastCommitId(ModelType.PROCESS, refId, commitId);
			Map<String, Object> entry = service.getDataset(repo, ModelType.PROCESS, refId, providerCommitId);
			provider.put("processType", ProcessType.from(entry));
			String name = (String) provider.get("name");
			// last element in path is the provider name itself
			String fullPath = getFullPath(commits, repo, ModelType.PROCESS, refId, commitId);
			if (!fullPath.contains("/"))
				continue;
			fullPath = fullPath.substring(0, fullPath.length() - name.length() - 1);
			provider.put("category", fullPath);
		}
	}

	@SuppressWarnings("unchecked")
	private void putSocialIndicators(CommitCache commits, Repository repo, String commitId,
			List<Map<String, Object>> aspects) {
		if (aspects == null)
			return;
		for (Map<String, Object> aspect : aspects) {
			if (!aspect.containsKey("socialIndicator"))
				continue;
			Map<String, Object> indicator = (Map<String, Object>) aspect.get("socialIndicator");
			String refId = (String) indicator.get("@id");
			String cId = commits.getLastCommitId(ModelType.SOCIAL_INDICATOR, refId, commitId);
			String dataset = fetchService.getDataset(repo, ModelType.SOCIAL_INDICATOR, refId, cId);
			if (dataset == null)
				continue;
			aspect.put("socialIndicator", ObjectMap.fromJson(dataset));
		}
	}

	@SuppressWarnings("unchecked")
	private void putReferenceUnits(CommitCache commits, Repository repo, ObjectMap map, String commitId) {
		List<Map<String, Object>> factors = (List<Map<String, Object>>) map.get("flowProperties");
		if (factors == null)
			return;
		for (Map<String, Object> factor : factors) {
			if (!factor.containsKey("flowProperty"))
				continue;
			Map<String, Object> property = (Map<String, Object>) factor.get("flowProperty");
			String refId = (String) property.get("@id");
			String cId = commits.getLastCommitId(ModelType.FLOW_PROPERTY, refId, commitId);
			String data = fetchService.getDataset(repo, ModelType.FLOW_PROPERTY, refId, cId);
			if (data == null)
				continue;
			Map<String, Object> flowProperty = ObjectMap.fromJson(data);
			Map<String, Object> unitGroup = (Map<String, Object>) flowProperty.get("unitGroup");
			if (unitGroup == null)
				continue;
			refId = (String) unitGroup.get("@id");
			cId = commits.getLastCommitId(ModelType.UNIT_GROUP, refId, commitId);
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

	private String getFullPath(CommitCache commits, Repository repo, ModelType type, String refId, String commitId) {
		if (refId == null)
			return "";
		if (commitId == null)
			return "";
		commitId = commits.getLastCommitId(type, refId, commitId);
		Map<String, Object> entry = service.getDataset(repo, type, refId, commitId);
		if (entry == null)
			return "";
		return entry.get("fullPath").toString();
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

	// using history service is not very efficient, so caching the references is
	// a necessary improvement
	// TODO maybe the history service can be refactored
	private class CommitCache {

		private final Repository repo;
		private final List<Commit> commits;
		private Map<String, Map<ModelType, List<String>>> commitToReferences = new HashMap<>();

		private CommitCache(Repository repo) {
			this.repo = repo;
			commits = historyService.getCommits(repo);
		}

		private String getLastCommitId(ModelType type, String refId, String commitId) {
			if (commitId != null) {
				Map<String, Object> dataset = service.getDataset(repo, type, refId, commitId);
				if (dataset != null && IndexAction.from(dataset) != IndexAction.DELETE) {
					return commitId;
				}
			}
			boolean foundCurrent = false;
			for (int i = commits.size() - 1; i >= 0; i--) {
				Commit commit = commits.get(i);
				if (commitId == null || commit.id.equals(commitId)) {
					foundCurrent = true;
				}
				if (!foundCurrent)
					continue;
				Map<ModelType, List<String>> refMap = commitToReferences.get(commit.id);
				if (refMap == null) {
					refMap = new HashMap<>();
					for (IndexEntry entry : searchService.getAll(repo, commit)) {
						List<String> refs = refMap.get(entry.type);
						if (refs == null) {
							refMap.put(entry.type, refs = new ArrayList<>());
						}
						refs.add(entry.refId);
					}
					commitToReferences.put(commit.id, refMap);
				}
				if (refMap.containsKey(type) && refMap.get(type).contains(refId)) {
					return commit.id;
				}
			}
			return null;
		}
	}

}
