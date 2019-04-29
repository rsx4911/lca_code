package com.greendelta.collaboration.service.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
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

public class SearchService {

	private final static Logger log = LogManager.getLogger(SearchService.class);
	private final SettingsService settingsService;
	private final QueryService queryService;
	private final IndexEntryParser parser = new IndexEntryParser();

	@Inject
	public SearchService(SettingsService settingsService, QueryService queryService) {
		this.settingsService = settingsService;
		this.queryService = queryService;
	}

	public SearchResult<IndexEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	public SearchResult<IndexEntry> search(SearchQuery query) {
		return SearchResults.convert(getClient().search(query), parser::parse);
	}

	SearchResult<ObjectMap> searchRaw(SearchQuery query) {
		return SearchResults.convert(getClient().search(query), parser::convert);
	}

	public List<IndexEntry> getAll(Repository repo) {
		SearchQueryBuilder builder = builder(repo);
		return parser.parse(getClient().search(builder.build()));
	}

	public List<IndexEntry> getAll(Repository repo, String commitId) {
		SearchQueryBuilder builder = builder(repo);
		if (commitId != null) {
			builder.filter("commitId", SearchFilterValue.term(commitId));
		}
		return parser.parse(getClient().search(builder.build()));
	}
	
	public ObjectMap getMostRecentUntil(Repository repo, String refId, String commitId) {
		if (refId == null)
			return null;
		SearchQueryBuilder builder = builder(repo);
		builder.filter("refId", SearchFilterValue.term(refId));
		if (commitId != null) {
			builder.filter("commits", SearchFilterValue.term(commitId));
		}
		SearchResult<Map<String, Object>> results = getClient().search(builder.build());
		if (results.data.isEmpty())
			return null;
		return parser.convert(results.data.get(0));
	}

	public List<IndexEntry> getMostRecentUntil(Repository repo, String commitId) {
		return getMostRecentUntil(repo, null, null, commitId);
	}

	public List<IndexEntry> getMostRecentUntil(Repository repo, ModelType type, String path, String commitId) {
		SearchQueryBuilder builder = builder(repo);
		if (commitId != null) {
			builder.filter("commits", SearchFilterValue.term(commitId));
		}
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		if (path != null) {
			builder.filter("fullPath", SearchFilterValue.wildcard(path + "/*"));
		}
		return parser.parse(getClient().search(builder.build()));
	}

	public List<IndexEntry> getMostRecentAfter(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo);
		if (commit != null) {
			builder.filter("commitTimestamp", SearchFilterValue.from(commit.timestamp + 1));
		}
		return parser.parse(getClient().search(builder.build()));
	}

	public IndexEntry getFirst(Repository repo, String refId) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("refId", SearchFilterValue.term(refId));
		builder.filter("action", SearchFilterValue.term(IndexAction.ADD.name()));
		builder.sortBy("commitTimestamp", SearchSorting.ASC);
		SearchResult<Map<String, Object>> result = getClient().search(builder.build());
		if (result.data.isEmpty())
			return null;
		return parser.parse(result.data.get(0));
	}

	public IndexEntry get(Repository repo, String refId, String commitId) {
		Map<String, Object> value = getClient().get(IndexEntry.toIndexId(repo.toId(), refId, commitId));
		if (value == null)
			return null;
		return parser.parse(value);
	}

	public IndexAction getMostRecentAction(Repository repo, String refId) {
		IndexEntry mostRecent = getMostRecent(repo, refId);
		if (mostRecent == null)
			return null;
		return mostRecent.action;
	}

	ObjectMap getRaw(Repository repo, String refId, String commitId) {
		log.trace("Getting index entry for repository {}, refId {} and commitId {}", repo.toId(), refId, commitId);
		Map<String, Object> value = getClient().get(IndexEntry.toIndexId(repo.toId(), refId, commitId));
		if (value == null)
			return null;
		return parser.convert(value);
	}

	SearchQueryBuilder builder(Repository repo) {
		return new SearchQueryBuilder().page(0)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repo.toId()))
				.sortBy("commitTimestamp", SearchSorting.DESC);
	}

	private List<IndexEntry> getMostRecent(Repository repo) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		return search(builder.build()).data;
	}

	private IndexEntry getMostRecent(Repository repo, String refId) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		builder.filter("refId", SearchFilterValue.term(refId));
		List<IndexEntry> data = search(builder.build()).data;
		if (data.isEmpty())
			return null;
		return data.get(0);
	}

	public IndexEntry get(String id) {
		return parser.parse(getClient().get(id));
	}

	public boolean has(String id) {
		return getClient().has(id);
	}

	public List<IndexEntry> get(Set<String> ids) {
		return parser.parse(getClient().get(ids));
	}

	public void index(Repository repo, String commitId, Collection<IndexEntry> entries) {
		if (entries.isEmpty())
			return;
		Set<String> refIds = Collections.convertToSet(entries, (entry) -> entry.refId);
		log.debug("Indexing {} entries in repository {}", entries.size(), repo.toId());
		List<IndexEntry> mostRecent = getMostRecent(repo);
		if (!mostRecent.isEmpty()) {
			for (IndexEntry entry : mostRecent) {
				if (refIds.contains(entry.refId)) {
					entry.mostRecent = false;
				} else if (!Strings.isNullOrEmpty(commitId)) {
					entry.commits.add(commitId);
				}
			}
			index(mostRecent);
		}
		for (IndexEntry entry : entries) {
			entry.mostRecent = true;
		}
		index(entries);
	}

	public void index(Collection<IndexEntry> entries) {
		Map<String, Map<String, Object>> contentsById = buildIndexMap(entries);
		getClient().index(contentsById);
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
		log.debug("Removing {} index entries", entries.size());
		Set<String> ids = new HashSet<>();
		for (IndexEntry entry : entries) {
			ids.add(entry.toIndexId());
		}
		getClient().remove(ids);
	}

	public void remove(IndexEntry entry) {
		getClient().remove(entry.toIndexId());
	}

	public void clearIndex() {
		getClient().clear();
	}

	private SearchClient getClient() {
		return settingsService.getSearchConfig().getSearchClient();
	}

}
