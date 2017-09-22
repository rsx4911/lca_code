package com.greendelta.collaboration.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.Strings;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.search.SearchClient;
import com.greendelta.collaboration.search.SearchFilterValue.Type;
import com.greendelta.collaboration.search.SearchQuery;
import com.greendelta.collaboration.search.SearchQueryBuilder;
import com.greendelta.collaboration.search.SearchResult;
import com.greendelta.collaboration.search.aggregations.SearchAggregation;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.IndexEntryParser;
import com.greendelta.collaboration.util.ObjectMap;

public class SearchService {

	private final SearchClient client;
	private final RepositoryService repoService;
	private final IndexEntryParser parser = new IndexEntryParser();

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
		builder.query(query, "name");
		builder.page(page);
		builder.pageSize(SearchQuery.DEFAULT_PAGE_SIZE);
		return client.search(builder.build());
	}
	
	List<IndexEntry> search(SearchQuery query) {
		return parser.parse(client.search(query));
	}

	public List<IndexEntry> getAll(Repository repo) {
		return getAll(repo, null, null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		return getAll(repo, type, null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type, String nameFilter) {
		SearchQueryBuilder builder = builder(repo);
		if (type != null) {
			builder.aggregation(Aggregations.MODEL_TYPE, type.name());
		}
		if (!Strings.isNullOrEmpty(nameFilter)) {
			builder.filter("name", "*" + nameFilter + "*", Type.WILDCART);
		}
		return parser.parse(client.search(builder.build()));
	}

	SearchQueryBuilder builder(Repository repo) {
		return new SearchQueryBuilder()
				.page(-1)
				.aggregation(Aggregations.REPOSITORY, repo.toId());
	}

	public boolean contains(Repository repo, String refId) {
		SearchQueryBuilder builder = builder(repo)
				.aggregation(Aggregations.REF_ID, refId);
		return !client.search(builder.build()).data.isEmpty();
	}

	public IndexEntry get(Repository repo, ModelType type, String refId, String commitId) {
		String id = repo.toId() + "/" + refId + "/" + commitId;
		return parser.parse(client.get(type.name().toLowerCase(), id));
	}

	public void index(Collection<IndexEntry> entries) {
		for (IndexEntry entry : entries) {
			index(entry);
		}
	}

	public void index(IndexEntry entry) {
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

	public void remove(Collection<IndexEntry> entries) {
		for (IndexEntry entry : entries) {
			remove(entry);
		}
	}

	public void remove(IndexEntry entry) {
		String id = entry.repositoryId + "/" + entry.refId + "/" + entry.commitId;
		client.remove(entry.type.name().toLowerCase(), id);
	}

}
