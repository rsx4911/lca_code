package com.greendelta.collaboration.service.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.Collections;
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

	public IndexIterator iterator(SearchQueryBuilder builder) {
		return new IndexIterator(builder);
	}

	SearchResult<ObjectMap> searchRaw(SearchQuery query) {
		return SearchResults.convert(getClient().search(query), parser::convert);
	}

	public IndexIterator getAll(Repository repo) {
		return new IndexIterator(builder(repo));
	}

	public IndexIterator getAll(Repository repo, String commitId) {
		SearchQueryBuilder builder = builder(repo);
		if (commitId != null) {
			builder.filter("commitId", SearchFilterValue.term(commitId));
		}
		return new IndexIterator(builder);
	}

	public Set<String> getDocumentIds(Repository repo) {
		SearchQueryBuilder builder = builder(repo);
		return getClient().searchIds(builder.build());
	}

	public long getDatasetCount(Repository repo, String commitId, IndexAction action) {
		SearchQueryBuilder builder = new SearchQueryBuilder().page(1).pageSize(1)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repo.toId()))
				.filter("commitId", SearchFilterValue.term(commitId));
		if (action != null) {
			builder.filter("action", SearchFilterValue.term(action.name()));
		}
		return getClient().search(builder.build()).resultInfo.totalCount;
	}

	public IndexEntry getMostRecentUntil(Repository repo, ModelType type, String refId, String commitId) {
		if (refId == null)
			return null;
		SearchQueryBuilder builder = builder(repo);
		builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		builder.filter("refId", SearchFilterValue.term(refId));
		if (commitId != null) {
			builder.filter("commits", SearchFilterValue.term(commitId));
		}
		SearchResult<Map<String, Object>> results = getClient().search(builder.build());
		if (results.data.isEmpty())
			return null;
		return parser.parse(results.data.get(0));
	}

	public IndexIterator getMostRecentUntil(Repository repo, String commitId, Filter... filters) {
		return getMostRecentUntilForPath(repo, null, null, commitId, filters);
	}

	public IndexIterator getMostRecentUntilForPath(Repository repo, ModelType type, String path, String commitId,
			Filter... filters) {
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
		return new IndexIterator(builder, append(filters, new DuplicateFilter()));
	}

	public IndexIterator getMostRecentAfter(Repository repo, Commit commit, Filter... filters) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		if (commit != null) {
			builder.filter("commitTimestamp", SearchFilterValue.from(commit.timestamp + 1));
		}
		return new IndexIterator(builder, filters);
	}

	private Filter[] append(Filter[] filters, Filter... toAppend) {
		if (filters == null)
			return toAppend;
		if (toAppend == null)
			return filters;
		Filter[] extended = new Filter[filters.length + toAppend.length];
		for (int i = 0; i < filters.length; i++) {
			extended[i] = filters[i];
		}
		for (int i = 0; i < toAppend.length; i++) {
			extended[filters.length + i] = toAppend[i];
		}
		return extended;
	}

	public IndexEntry getFirst(Repository repo, ModelType type, String refId) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("refId", SearchFilterValue.term(refId));
		builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		builder.filter("action", SearchFilterValue.term(IndexAction.ADD.name()));
		builder.sortBy("commitTimestamp", SearchSorting.ASC);
		SearchResult<Map<String, Object>> result = getClient().search(builder.build());
		if (result.data.isEmpty())
			return null;
		return parser.parse(result.data.get(0));
	}

	public IndexEntry get(Repository repo, ModelType type, String refId, String commitId) {
		Map<String, Object> value = getClient().get(IndexEntry.toIndexId(repo.toId(), type, refId, commitId));
		if (value == null)
			return null;
		return parser.parse(value);
	}

	public IndexAction getMostRecentAction(Repository repo, ModelType type, String refId) {
		IndexEntry mostRecent = getMostRecent(repo, type, refId);
		if (mostRecent == null)
			return null;
		return mostRecent.action;
	}

	ObjectMap getRaw(Repository repo, ModelType type, String refId, String commitId) {
		log.trace("Getting index entry for repository {}, type {} refId {} and commitId {}", repo.toId(), type.name(),
				refId, commitId);
		Map<String, Object> value = getClient().get(IndexEntry.toIndexId(repo.toId(), type, refId, commitId));
		if (value == null)
			return null;
		return parser.convert(value);
	}

	SearchQueryBuilder builder(Repository repo) {
		return new SearchQueryBuilder().page(0)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repo.toId()))
				.sortBy("commitTimestamp", SearchSorting.DESC);
	}

	private IndexEntry getMostRecent(Repository repo, ModelType type, String refId) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		builder.filter(Aggregations.MODEL_TYPE.field, SearchFilterValue.term(type.name()));
		builder.filter("refId", SearchFilterValue.term(refId));
		List<IndexEntry> data = search(builder.build()).data;
		if (data.isEmpty())
			return null;
		return data.get(0);
	}

	private List<IndexEntry> getMostRecent(Repository repo) {
		SearchQueryBuilder builder = builder(repo);
		builder.filter("mostRecent", SearchFilterValue.term(true));
		return search(builder.build()).data;
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
		Set<String> refIds = Collections.convertToSet(entries, (entry) -> entry.toId());
		log.debug("Indexing {} entries in repository {}", entries.size(), repo.toId());
		// TODO updating without loading all index entries
		List<IndexEntry> mostRecent = getMostRecent(repo);
		if (!mostRecent.isEmpty()) {
			for (IndexEntry entry : mostRecent) {
				if (refIds.contains(entry.toId())) {
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

	public void update(String id, Map<String, Object> content) {
		getClient().update(id, content);
	}

	public void update(String id, String script, Map<String, Object> parameters) {
		getClient().update(id, script, parameters);
	}

	public void update(Set<String> ids, Map<String, Object> content) {
		getClient().update(ids, content);
	}

	public void update(Set<String> ids, String script, Map<String, Object> parameters) {
		getClient().update(ids, script, parameters);
	}

	public void update(Map<String, Map<String, Object>> contentsById) {
		getClient().update(contentsById);
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
		return ObjectMap.fromObject(entry);
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

	public void remove(Set<String> ids) {
		if (ids.isEmpty())
			return;
		log.debug("Removing {} index entries", ids.size());
		getClient().remove(ids);
	}

	public void clearIndex() {
		getClient().clear();
	}

	private SearchClient getClient() {
		return settingsService.getSearchConfig().getSearchClient();
	}

	public interface Filter extends Function<IndexEntry, Boolean> {

	}

	public static class RequestedFilter implements Filter {

		private final List<FileReference> requested;

		public RequestedFilter(List<FileReference> requested) {
			this.requested = requested;
		}

		@Override
		public Boolean apply(IndexEntry t) {
			return !requested.contains(t.asFileReference());
		}

	}

	public static class DeletedFilter implements Filter {

		@Override
		public Boolean apply(IndexEntry entry) {
			return entry.action == IndexAction.DELETE;
		}

	}

	private class DuplicateFilter implements Filter {

		private final Set<String> processed = new HashSet<>();

		@Override
		public Boolean apply(IndexEntry entry) {
			String id = entry.toId();
			if (processed.contains(id))
				return true;
			processed.add(id);
			return false;
		}

	}

	public class IndexIterator implements Iterator<IndexEntry> {

		private static final int PAGE_SIZE = 1000;
		private final SearchQueryBuilder queryBuilder;
		private final Filter[] filters;
		private SearchResult<ObjectMap> current;
		private int position = 0;
		private IndexEntry next = null;

		private IndexIterator(SearchQueryBuilder queryBuilder, Filter... filters) {
			queryBuilder.pageSize(PAGE_SIZE);
			queryBuilder.page(1);
			current = searchRaw(queryBuilder.build());
			this.queryBuilder = queryBuilder;
			this.filters = filters != null ? filters : new Filter[0];
		}

		public boolean hasNext() {
			if (next != null)
				return true;
			if (current.resultInfo.currentPage >= current.resultInfo.pageCount && position >= current.resultInfo.count)
				return false;
			next = parser.parse(current.data.get(position));
			if (doFilter(next)) {
				updatePosition();
				return hasNext();
			}
			return true;
		}

		private boolean doFilter(IndexEntry entry) {
			for (Function<IndexEntry, Boolean> filter : filters)
				if (filter.apply(entry))
					return true;
			return false;
		}

		public IndexEntry next() {
			if (!hasNext())
				throw new NoSuchElementException();
			IndexEntry next = this.next;
			updatePosition();
			return next;
		}

		private void updatePosition() {
			position++;
			next = null;
			if (current.resultInfo.currentPage == current.resultInfo.pageCount || position < current.resultInfo.count)
				return;
			position = 0;
			queryBuilder.page(current.resultInfo.currentPage + 1);
			current = searchRaw(queryBuilder.build());
		}

		public boolean isEmpty() {
			return current.resultInfo.totalCount == 0;
		}

		public int size() {
			if (filters == null || filters.length == 0)
				return (int) current.resultInfo.totalCount;
			throw new IllegalStateException("Can not determine size, filters may apply");
		}

	}

}
