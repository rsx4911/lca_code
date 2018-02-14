package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.SearchSorting;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.results.AggregationResultBuilder;

public class SearchService {

	private final SearchClient client;
	private final RepositoryService repoService;
	private final UserService userService;
	private final IndexEntryParser parser = new IndexEntryParser();

	@Inject
	public SearchService(SearchClient searchClient, RepositoryService repoService, UserService userService) {
		this.client = searchClient;
		this.repoService = repoService;
		this.userService = userService;
	}

	public SearchResult<IndexEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		List<Repository> repos = repoService.getAllAccessible();
		if (repos.isEmpty())
			return buildEmptyResult(page, pageSize);
		SearchQueryBuilder builder = new SearchQueryBuilder();
		ModelType type = getFilteredModelType(filters.get(Aggregations.MODEL_TYPE.name));
		for (SearchAggregation aggregation : Aggregations.getFilters(type)) {
			Set<String> filterValues = filters.get(aggregation.name);
			if (aggregation.name.equals(Aggregations.REPOSITORY.name)) {
				putRepositoryFilter(builder, filterValues, repos);
			} else if (aggregation.name.equals(Aggregations.MODEL_TYPE.name)) {
				if (type == null) {
					builder.aggregation(Aggregations.MODEL_TYPE, getModelTypes());
				} else {
					builder.aggregation(Aggregations.MODEL_TYPE, type.name());
				}
			} else if (filterValues != null && !filterValues.isEmpty()) {
				for (String filterValue : filterValues) {
					builder.aggregation(aggregation, filterValue);
				}
			} else {
				builder.aggregation(aggregation);
			}
		}
		boolean loggedIn = userService.getCurrentUser().getId() != 0;
		if (!Strings.isNullOrEmpty(query)) {
			builder.query(query, SearchFields.get(type, loggedIn));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		builder.page(page);
		builder.pageSize(pageSize);
		SearchResult<Map<String, Object>> result = client.search(builder.build());
		if (loggedIn)
			return SearchResults.convert(result, parser::parse);
		// only return newest and undeleted versions to anonymous users
		List<Map<String, Object>> entries = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();
		entries = Collections.filter(entries, (e) -> !alreadyAdded.add(e.get("refId").toString()));
		entries = Collections.filter(entries, (e) -> e.get("action") == IndexAction.DELETE);
		return SearchResults.convert(result, parser::parse);
	}

	private String[] getModelTypes() {
		Set<String> types = new HashSet<>();
		for (ModelType type : ModelType.categorized()) {
			types.add(type.name());
		}
		return types.toArray(new String[types.size()]);
	}

	private SearchResult<IndexEntry> buildEmptyResult(int page, int pageSize) {
		SearchResult<IndexEntry> result = new SearchResult<>();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = pageSize;
		for (SearchAggregation aggr : Aggregations.PROCESS_FILTERS) {
			result.aggregations.add(new AggregationResultBuilder().type(aggr.type).name(aggr.name).build());
		}
		return result;
	}

	private ModelType getFilteredModelType(Set<String> values) {
		if (values == null)
			return null;
		if (values.size() > 1)
			return null;
		return ModelType.valueOf(values.iterator().next());
	}

	private void putRepositoryFilter(SearchQueryBuilder builder, Set<String> values, List<Repository> repos) {
		for (Repository repo : repos) {
			if (values != null && !values.contains(repo.toId()))
				continue;
			builder.aggregation(Aggregations.REPOSITORY, repo.toId());
		}
	}

	public SearchResult<IndexEntry> search(SearchQuery query) {
		return SearchResults.convert(client.search(query), parser::parse);
	}

	SearchResult<ObjectMap> searchRaw(SearchQuery query) {
		return SearchResults.convert(client.search(query), parser::convert);
	}

	public List<IndexEntry> getAll(Repository repo) {
		return getAll(repo, (ModelType) null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return parser.parse(client.search(builder.build()));
	}

	public List<IndexEntry> getAll(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (commit != null) {
			builder.filter("commitId", SearchFilterValue.term(commit.id));
		}
		return parser.parse(client.search(builder.build()));
	}

	public List<IndexEntry> getDescriptors(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (commit != null) {
			builder.filter("commitId", SearchFilterValue.term(commit.id));
		}
		builder.fullResult(false);
		List<IndexEntry> descriptors = new ArrayList<>();
		for (Map<String, Object> descriptor : client.search(builder.build()).data) {
			IndexEntry entry = IndexEntry.descriptor(descriptor.get("documentId").toString());
			descriptors.add(entry);
		}
		return descriptors;
	}

	SearchQueryBuilder builder(String repoId) {
		return new SearchQueryBuilder()
				.page(0)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repoId));
	}

	public IndexEntry get(Repository repo, String refId, String commitId) {
		return get(IndexEntry.toIndexId(repo.toId(), refId, commitId));
	}

	ObjectMap getRaw(Repository repo, String refId, String commitId) {
		return parser.convert(client.get(IndexEntry.toIndexId(repo.toId(), refId, commitId)));
	}

	IndexAction getMostRecentAction(String repoId, String refId) {
		ObjectMap latest = getMostRecent(repoId, refId, null);
		if (latest == null)
			return null;
		return IndexAction.from(latest);
	}

	ObjectMap getMostRecent(String repoId, String refId, Commit until) {
		List<ObjectMap> latest = getMostRecent(repoId, java.util.Collections.singleton(refId), until);
		if (latest == null || latest.isEmpty())
			return null;
		return latest.get(0);
	}

	List<ObjectMap> getMostRecent(String repoId, Set<String> refIds, Commit until) {
		List<ObjectMap> results = new ArrayList<>();
		Set<String> remaining = new HashSet<>(refIds);
		Set<String> added = new HashSet<>();
		while (!remaining.isEmpty()) {
			Set<String> next = Collections.pop(remaining, 1000);
			SearchQueryBuilder builder = builder(repoId);
			if (next.size() == 1) {
				builder.filter("refId", SearchFilterValue.term(next.iterator().next()));
			} else {
				builder.filter("refId", SearchFilterValue.term(next));
			}
			if (until != null) {
				builder.filter("commitTimestamp", SearchFilterValue.to(until.timestamp));
			}
			builder.sortBy("commitTimestamp", SearchSorting.DESC);
			SearchResult<ObjectMap> result = searchRaw(builder.build());
			if (result.data.isEmpty())
				continue;
			for (ObjectMap data : result.data) {
				String refId = data.get("refId").toString();
				if (added.contains(refId))
					continue;
				results.add(data);
				added.add(refId);
			}
		}
		return results;
	}

	public List<IndexEntry> getMostRecent(Repository repo, ModelType type, String path, Commit until) {
		List<IndexEntry> results = new ArrayList<>();
		Set<String> added = new HashSet<>();
		SearchQueryBuilder builder = builder(repo.toId());
		if (until != null) {
			builder.filter("commitTimestamp", SearchFilterValue.to(until.timestamp));
		}
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		if (path != null) {
			builder.filter("fullPath", SearchFilterValue.wildcard(path + "/?*"));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		SearchResult<ObjectMap> result = searchRaw(builder.build());
		if (result.data.isEmpty())
			return results;
		for (ObjectMap data : result.data) {
			String refId = data.get("refId").toString();
			if (added.contains(refId))
				continue;
			results.add(parser.parse(data));
			added.add(refId);
		}
		return results;
	}

	public IndexEntry getFirst(String repoId, String refId) {
		SearchQueryBuilder builder = builder(repoId);
		builder.filter("refId", SearchFilterValue.term(refId));
		builder.filter("action", SearchFilterValue.term(IndexAction.ADD.name()));
		builder.sortBy("commitTimestamp", SearchSorting.ASC);
		SearchResult<Map<String, Object>> result = client.search(builder.build());
		if (result.data.isEmpty())
			return null;
		return parser.parse(result.data.get(0));
	}

	public void index(String repoId, Collection<IndexEntry> entries) {
		if (entries.isEmpty())
			return;
		Set<String> refIds = Collections.convert(new HashSet<>(entries), e -> e.refId);
		List<IndexEntry> mostRecent = Collections.convert(getMostRecent(repoId, refIds, null), parser::parse);
		if (!mostRecent.isEmpty()) {
			for (IndexEntry entry : mostRecent) {
				entry.mostRecent = false;
			}
			Map<String, Map<String, Object>> contentsById = buildIndexMap(mostRecent);
			client.index(contentsById);
		}
		for (IndexEntry entry : entries) {
			entry.mostRecent = true;
		}
		Map<String, Map<String, Object>> contentsById = buildIndexMap(entries);
		client.index(contentsById);
	}

	public IndexEntry get(String id) {
		return parser.parse(client.get(id));
	}

	public boolean has(String id) {
		return client.has(id);
	}

	public List<IndexEntry> get(Set<String> ids) {
		return parser.parse(client.get(ids));
	}

	private Map<String, Map<String, Object>> buildIndexMap(Collection<IndexEntry> entries) {
		Map<String, Map<String, Object>> contentsById = new HashMap<>();
		for (IndexEntry entry : entries) {
			Map<String, Object> content = toMap(entry);
			contentsById.put(entry.toIndexId(), content);
		}
		return contentsById;
	}

	private ObjectMap toMap(IndexEntry entry) {
		setDummyCategoryId(entry);
		ObjectMap map = ObjectMap.fromObject(entry);
		map.put("typeOrdinal", ModelTypes.getOrdinal(entry.type, entry.categoryType));
		return map;
	}

	private void setDummyCategoryId(IndexEntry entry) {
		if (!Strings.isNullOrEmpty(entry.categoryRefId))
			return;
		if (entry.type == ModelType.CATEGORY) {
			entry.categoryRefId = entry.categoryType.name();
		} else {
			entry.categoryRefId = entry.type.name();
		}
	}

	public void remove(Collection<IndexEntry> entries) {
		if (entries.isEmpty())
			return;
		Set<String> ids = new HashSet<>();
		for (IndexEntry entry : entries) {
			ids.add(entry.toIndexId());
		}
		client.remove(ids);
	}

	public void remove(IndexEntry entry) {
		client.remove(entry.toIndexId());
	}

	public void clearIndex() {
		remove(search(new SearchQueryBuilder().page(0).build()).data);
	}

}
