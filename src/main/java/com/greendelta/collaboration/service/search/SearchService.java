package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
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
		return getAll(repo, (ModelType) null);
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return parser.parse(getClient().search(builder.build()));
	}

	public List<IndexEntry> getAll(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (commit != null) {
			builder.filter("commitId", SearchFilterValue.term(commit.id));
		}
		return parser.parse(getClient().search(builder.build()));
	}

	public List<IndexEntry> getAll(Repository repo, ModelType type, String path, Commit commit) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (commit != null) {
			builder.filter("commits", SearchFilterValue.term(commit.id));
		}
		if (type != null) {
			builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		}
		if (path != null) {
			builder.filter("fullPath", SearchFilterValue.wildcard(path + "/?*"));
		}
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return search(builder.build()).data;
	}

	public List<IndexEntry> getDescriptors(Repository repo, Commit commit) {
		SearchQueryBuilder builder = builder(repo.toId());
		if (commit != null) {
			builder.filter("commitId", SearchFilterValue.term(commit.id));
		}
		builder.fullResult(false);
		List<IndexEntry> descriptors = new ArrayList<>();
		for (Map<String, Object> descriptor : getClient().search(builder.build()).data) {
			IndexEntry entry = IndexEntry.descriptor(descriptor.get("documentId").toString());
			descriptors.add(entry);
		}
		return descriptors;
	}

	SearchQueryBuilder builder(String repoId) {
		return new SearchQueryBuilder().page(0)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repoId));
	}

	private List<IndexEntry> getMostRecent(String repoId) {
		SearchQueryBuilder builder = builder(repoId);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		return search(builder.build()).data;
	}

	private IndexEntry getMostRecent(String repoId, String refId) {
		SearchQueryBuilder builder = builder(repoId);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		builder.sortBy("commitTimestamp", SearchSorting.DESC);
		builder.filter("refId", SearchFilterValue.term(refId));
		List<IndexEntry> data = search(builder.build()).data;
		if (data.isEmpty())
			return null;
		return data.get(0);
	}

	public IndexEntry getFirst(String repoId, String refId) {
		SearchQueryBuilder builder = builder(repoId);
		builder.filter("refId", SearchFilterValue.term(refId));
		builder.filter("action", SearchFilterValue.term(IndexAction.ADD.name()));
		builder.sortBy("commitTimestamp", SearchSorting.ASC);
		SearchResult<Map<String, Object>> result = getClient().search(builder.build());
		if (result.data.isEmpty())
			return null;
		return parser.parse(result.data.get(0));
	}

	public void index(String repoId, Collection<IndexEntry> entries) {
		index(repoId, entries);
	}

	public void index(String repoId, String commitId, Collection<IndexEntry> entries) {
		if (entries.isEmpty())
			return;
		Set<String> refIds = Collections.convertToSet(entries, (entry) -> entry.refId);
		log.debug("Indexing {} entries in repository {}", entries.size(), repoId);
		List<IndexEntry> mostRecent = getMostRecent(repoId);
		if (!mostRecent.isEmpty()) {
			for (IndexEntry entry : mostRecent) {
				if (refIds.contains(entry.refId)) {
					entry.mostRecent = false;
				} else if (!Strings.isNullOrEmpty(commitId)) {
					entry.commits.add(commitId);
				}
			}
			Map<String, Map<String, Object>> contentsById = buildIndexMap(mostRecent);
			getClient().index(contentsById);
		}
		for (IndexEntry entry : entries) {
			entry.mostRecent = true;
		}
		Map<String, Map<String, Object>> contentsById = buildIndexMap(entries);
		getClient().index(contentsById);
	}

	public IndexEntry get(Repository repo, String refId, String commitId) {
		ObjectMap value = getRaw(repo, refId, commitId);
		if (value == null)
			return null;
		return parser.parse(value);
	}

	public IndexAction getLastAction(Repository repo, String refId) {
		IndexEntry mostRecent = getMostRecent(repo.toId(), refId);
		if (mostRecent == null)
			return null;
		return mostRecent.action;
	}

	ObjectMap getRaw(Repository repo, String refId, String commitId) {
		log.trace("Getting index entry for repository {}, refId {} and commitId {}", repo.toId(), refId, commitId);
		Map<String, Object> value = getClient().get(IndexEntry.toIndexId(repo.toId(), refId, commitId));
		if (value != null)
			return parser.convert(value);
		SearchQueryBuilder builder = builder(repo.toId());
		if (commitId != null) {
			builder.filter("commits", SearchFilterValue.term(commitId));
		}
		List<ObjectMap> data = searchRaw(builder.build()).data;
		if (data.isEmpty())
			return null;
		return parser.convert(data.get(0));
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
