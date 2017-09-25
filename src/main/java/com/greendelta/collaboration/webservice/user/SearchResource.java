package com.greendelta.collaboration.webservice.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.search.SearchQuery;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.webservice.Respond;
import com.sun.jersey.api.uri.UriComponent;

@Path("search")
public class SearchResource {

	private final SearchService service;

	@Inject
	public SearchResource(SearchService service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context UriInfo uriInfo) {
		Map<String, Set<String>> parameters = getQueryParameters(uriInfo);
		String query = removeStringFilter("query", parameters);
		int page = removeIntFilter("page", parameters, 1);
		int pageSize = removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		return Respond.ok(service.search(query, page, pageSize, parameters));
	}

	private String removeStringFilter(String name, Map<String, Set<String>> filters) {
		return removeFilter(name, filters, "");
	}

	private int removeIntFilter(String name, Map<String, Set<String>> filters, int defaultValue) {
		String value = removeFilter(name, filters, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}

	private String removeFilter(String name, Map<String, Set<String>> filters, String defaultValue) {
		Set<String> value = filters.remove(name);
		if (value == null)
			return defaultValue;
		if (value.size() == 0)
			return defaultValue;
		String first = value.iterator().next();
		if (Strings.nullOrEmpty(first))
			return defaultValue;
		return first;
	}

	private Map<String, Set<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, Set<String>> filters = new HashMap<>();
		for (String key : uriInfo.getQueryParameters().keySet()) {
			Set<String> filterBy = filters.get(key);
			if (filterBy == null)
				filters.put(decode(key), filterBy = new HashSet<>());
			List<String> values = uriInfo.getQueryParameters().get(key);
			if (values == null)
				continue;
			for (String value : values)
				filterBy.add(decode(value));
		}
		return filters;
	}

	private String decode(String value) {
		return UriComponent.decode(value, UriComponent.Type.PATH_SEGMENT);
	}

}
