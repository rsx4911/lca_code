package com.greendelta.cloud.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.util.ObjectMap;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndexEntry;
import com.greendelta.cloud.service.BrowseService;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("browse")
@Produces(MediaType.APPLICATION_JSON)
public class BrowseResource {

	private BrowseService service;
	private RepositoryService repoService;
	private FetchService fetchService;

	@Inject
	public BrowseResource(BrowseService service, RepositoryService repoService, FetchService fetchService) {
		this.service = service;
		this.repoService = repoService;
		this.fetchService = fetchService;
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
	public Response getCategoryContent(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("categoryRefId") String categoryRefId) {
		Repository repo = repoService.get(group, name);
		for (ModelType type : ModelType.values()) {
			if (type.name().equals(categoryRefId)) {
				List<DatasetIndexEntry> content = service.getCategoryContent(repo, type);
				return Respond.ok(appendParentRefId(repo, content));
			}
		}
		List<DatasetIndexEntry> content = service.getCategoryContent(repo, categoryRefId);
		if (content.isEmpty())
			if (service.categoryExists(repo, categoryRefId))
				Respond.ok(appendParentRefId(repo, content));
			else
				return Respond.notFound();
		return Respond.ok(appendParentRefId(repo, content));
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
		String parent = fetchService.getDataset(repo, entry.categoryType, entry.categoryRefId, entry.commitId);
		return ObjectMap.fromJson(parent).get("categoryRefId");
	}

}
