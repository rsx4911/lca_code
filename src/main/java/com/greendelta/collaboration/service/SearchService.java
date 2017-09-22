package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.DatasetIndexEntry;
import com.greendelta.collaboration.search.SearchClient;
import com.greendelta.collaboration.search.SearchFilterValue.Type;
import com.greendelta.collaboration.search.SearchQuery;
import com.greendelta.collaboration.search.SearchQueryBuilder;
import com.greendelta.collaboration.search.SearchResult;
import com.greendelta.collaboration.search.aggregations.SearchAggregation;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.ObjectMap;

public class SearchService {

	private final SearchClient client;
	private final RepositoryService repoService;

	@Inject
	public SearchService(SearchClient searchClient, RepositoryService repoService) {
		this.client = searchClient;
		this.repoService = repoService;
	}

	public void createIndex(Map<String, Object> settings) {
		client.create(settings);
	}

	public SearchResult search(String query, int page) {
		SearchQueryBuilder builder = new SearchQueryBuilder();
		for (SearchAggregation aggregation : Aggregations.ALL) {
			builder.aggregation(aggregation);
		}
		for (Repository repo : repoService.getAllAccessible()) {
			builder.aggregation(Aggregations.REPOSITORY, repo.toId());
		}
		builder.query(query);
		builder.page(page);
		builder.pageSize(SearchQuery.DEFAULT_PAGE_SIZE);
		return client.search(builder.build());
	}

	public List<DatasetIndexEntry> getAll(Repository repo) {
		return getAll(repo, null, null);
	}

	public List<DatasetIndexEntry> getAll(Repository repo, ModelType type) {
		return getAll(repo, type, null);
	}

	public List<DatasetIndexEntry> getAll(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		return parse(client.search(builder.build()));
	}

	public List<DatasetIndexEntry> getUncategorized(Repository repo, ModelType type, String nameFilter) {
		List<DatasetIndexEntry> results = new ArrayList<>();
		results.addAll(getRootCategories(repo, type, nameFilter));
		results.addAll(getRootModels(repo, type, nameFilter));
		return results;
	}

	private List<DatasetIndexEntry> getRootModels(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
			builder.aggregation(Aggregations.CATEGORY, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		return parse(client.search(builder.build()));
	}

	private List<DatasetIndexEntry> getRootCategories(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, ModelType.CATEGORY.name());
			builder.aggregation(Aggregations.CATEGORY_TYPE, type.name());
			builder.aggregation(Aggregations.CATEGORY, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		return parse(client.search(builder.build()));
	}

	public List<DatasetIndexEntry> getForCategory(Repository repo, String id) {
		return getForCategory(repo, id, null);
	}

	public List<DatasetIndexEntry> getForCategory(Repository repo, String id, String nameFilter) {
		SearchQueryBuilder builder = builder(repo)
				.aggregation(Aggregations.CATEGORY, id);
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		return parse(client.search(builder.build()));
	}

	private SearchQueryBuilder builder(Repository repo) {
		return new SearchQueryBuilder()
				.page(-1)
				.aggregation(Aggregations.REPOSITORY, repo.toId());
	}

	public boolean contains(Repository repo, String refId) {
		SearchQueryBuilder builder = builder(repo)
				.aggregation(Aggregations.REF_ID, refId);
		return !client.search(builder.build()).data.isEmpty();
	}

	public DatasetIndexEntry get(Repository repo, ModelType type, String refId, String commitId) {
		String id = repo.toId() + "/" + refId + "/" + commitId;
		return parse(client.get(type.name().toLowerCase(), id));
	}

	public List<DatasetIndexEntry> parse(SearchResult result) {
		List<DatasetIndexEntry> parsed = new ArrayList<>();
		for (Map<String, Object> entry : result.data) {
			parsed.add(parse(entry));
		}
		return parsed;
	}

	private DatasetIndexEntry parse(Map<String, Object> entry) {
		DatasetIndexEntry e = new DatasetIndexEntry();
		e.categoryRefId = toString(entry.get("categoryRefId"));
		e.categoryType = toModelType(entry.get("categoryType"));
		e.commitId = toString(entry.get("commitId"));
		e.commitMessage = toString(entry.get("commitMessage"));
		e.fullPath = toString(entry.get("fullPath"));
		e.lastUpdate = toLong(entry.get("lastUpdate"));
		e.name = toString(entry.get("name"));
		e.refId = toString(entry.get("refId"));
		e.repositoryId = toString(entry.get("repositoryId"));
		e.type = toModelType(entry.get("type"));
		return e;
	}

	private String toString(Object o) {
		if (o == null)
			return null;
		if ("".equals(o.toString()))
			return null;
		return o.toString();
	}

	private long toLong(Object o) {
		if (o == null)
			return 0;
		return Long.parseLong(o.toString());
	}

	private ModelType toModelType(Object o) {
		if (o == null)
			return null;
		return ModelType.valueOf(o.toString().toUpperCase());
	}

	public void index(Collection<DatasetIndexEntry> entries) {
		for (DatasetIndexEntry entry : entries) {
			index(entry);
		}
	}

	public void index(DatasetIndexEntry entry) {
		String id = entry.repositoryId + "/" + entry.refId + "/" + entry.commitId;
		if (Strings.isNullOrEmpty(entry.categoryRefId)) {
			if (entry.type == ModelType.CATEGORY) {
				entry.categoryRefId = entry.categoryType.name();
			} else {
				entry.categoryRefId = entry.type.name();
			}
		}
		Map<String, Object> map = ObjectMap.fromObject(entry);
		client.index(entry.type.name().toLowerCase(), id, map);
	}

	public void remove(Collection<DatasetIndexEntry> entries) {
		for (DatasetIndexEntry entry : entries) {
			remove(entry);
		}
	}

	public void remove(DatasetIndexEntry entry) {
		String id = entry.repositoryId + "/" + entry.refId + "/" + entry.commitId;
		client.remove(entry.type.name().toLowerCase(), id);
	}

}
