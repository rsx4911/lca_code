package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.index.DatasetIndexEntry;
import com.greendelta.collaboration.service.PagedResult;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.ObjectMap;

@Path("search")
public class SearchResource {

	private final SearchService service;

	@Inject
	public SearchResource(SearchService service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("query") @DefaultValue("") String filter,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("type") ModelType type) {
		PagedResult<DatasetIndexEntry> result = service.search(page, filter, type);
		PagedResult<ObjectMap> mapped = result.toClient2((entries) -> {
			List<ObjectMap> list = new ArrayList<>();
			for (DatasetIndexEntry entry : entries)
				list.add(ObjectMap.fromObject(entry));
			return list;
		});
		ObjectMap response = ObjectMap.fromObject(mapped);
		response.put("modelTypes", getModelTypes());
		if (type != null)
			response.put("filteredType", type);
		return Respond.ok(response);
	}

	private List<ModelType> getModelTypes() {
		List<ModelType> types = new ArrayList<>();
		for (ModelType type : ModelType.values())
			if (type.isCategorized())
				types.add(type);
		return types;
	}

}
