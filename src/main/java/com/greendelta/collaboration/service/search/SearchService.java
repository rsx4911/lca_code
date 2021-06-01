package com.greendelta.collaboration.service.search;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.Reference;
import org.openlca.cloud.api.git.DiffReference;
import org.openlca.cloud.api.git.DiffType;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.collaboration.util.GsonTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

public class SearchService {

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

	private SearchQueryBuilder builder(Repository repo) {
		return new SearchQueryBuilder().page(0)
				.filter(Aggregations.REPOSITORY.field, SearchFilterValue.term(repo.toId()));
	}

	public void index(Repository repo) {
		index(repo, repo.references.find().all());
	}

	public void updateAsync(Repository repo) {
		Commit head = repo.commits.find().latest();
		if (head == null)
			return;
		List<DiffReference> diffs = repo.references.diff().withPrevious(head.id).all();
		new Thread(() -> {
			List<Reference> toAddOrUpdate = DiffReference
					.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
					.stream().map(d -> d.right)
					.collect(Collectors.toList());
			Set<String> toDelete = DiffReference
					.filter(diffs, DiffType.DELETED)
					.stream().map(d -> IndexEntry.toIndexId(repo.toId(), d.left.type, d.left.refId))
					.collect(Collectors.toSet());
			index(repo, toAddOrUpdate);
			getClient().remove(toDelete);
		}).start();
	}

	private void index(Repository repo, List<Reference> refs) {
		IndexEntryCreator indexEntryCreator = new IndexEntryCreator(repo);
		Gson gson = new Gson();
		for (Reference ref : refs) {
			// TODO check performance
			String json = repo.datasets.get(ref.objectId);
			Map<String, Object> data = gson.fromJson(json, GsonTypes.OBJECT_MAP);
			if (data.isEmpty())
				continue;
			IndexEntry entry = indexEntryCreator.create(ref, data);
			Map<String, Object> content = ObjectMap.fromObject(entry);
			getClient().index(entry.toIndexId(), content);
		}
	}

	public void update(Repository repo, Map<String, Object> content) {
		Set<String> ids = getDocumentIds(repo);
		getClient().update(ids, content);
	}

	public void remove(Repository repo) {
		Set<String> ids = getDocumentIds(repo);
		getClient().remove(ids);
	}

	private Set<String> getDocumentIds(Repository repo) {
		SearchQueryBuilder builder = builder(repo);
		return getClient().searchIds(builder.build());
	}

	public void clearIndex() {
		getClient().clear();
	}

	private SearchClient getClient() {
		return settingsService.searchConfig.getSearchClient();
	}

	public class IndexIterator implements Iterator<IndexEntry> {

		private static final int PAGE_SIZE = 1000;
		private final SearchQueryBuilder queryBuilder;
		private SearchResult<ObjectMap> current;
		private int position = 0;
		private IndexEntry next = null;

		private IndexIterator(SearchQueryBuilder queryBuilder) {
			queryBuilder.pageSize(PAGE_SIZE);
			queryBuilder.page(1);
			current = searchRaw(queryBuilder.build());
			this.queryBuilder = queryBuilder;
		}

		private SearchResult<ObjectMap> searchRaw(SearchQuery query) {
			return SearchResults.convert(getClient().search(query), parser::convert);
		}

		public boolean hasNext() {
			if (next != null)
				return true;
			if (current.resultInfo.currentPage >= current.resultInfo.pageCount && position >= current.resultInfo.count)
				return false;
			next = parser.parse(current.data.get(position));
			return true;
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
			return (int) current.resultInfo.totalCount;
		}

	}

}
