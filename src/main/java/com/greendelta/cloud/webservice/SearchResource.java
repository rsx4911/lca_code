package com.greendelta.cloud.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.util.ObjectMap;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndexEntry;
import com.greendelta.cloud.service.PagedResult;
import com.greendelta.cloud.service.SearchService;

@Path("search")
public class SearchResource {

	private final SearchService service;

	@Inject
	public SearchResource(SearchService service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("query") @DefaultValue("") String filter, @QueryParam("page") @DefaultValue("1") int page) {
		PagedResult<DatasetIndexEntry> result = service.search(page, filter);
		return Respond.ok(result.toClient((entries) -> {
			List<Map<String, Object>> mapped = new ArrayList<>();
			for (DatasetIndexEntry entry : entries)
				mapped.add(ObjectMap.fromObject(entry));
			return mapped;
		}));
	}

}
