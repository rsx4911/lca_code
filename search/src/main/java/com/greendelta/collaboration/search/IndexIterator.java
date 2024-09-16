package com.greendelta.collaboration.search;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

class IndexIterator implements Iterator<Map<String, Object>> {

	private final SearchClient client;
	private final SearchQueryBuilder queryBuilder;
	private SearchResult<Map<String, Object>> current;
	private int position = 0;
	private Map<String, Object> next = null;

	IndexIterator(SearchClient client, SearchQueryBuilder queryBuilder, int pageSize) {
		queryBuilder.pageSize(pageSize);
		queryBuilder.page(1);
		current = client.search(queryBuilder.build());
		this.client = client;
		this.queryBuilder = queryBuilder;
	}

	@Override
	public boolean hasNext() {
		if (next != null)
			return true;
		if (current.resultInfo.currentPage >= current.resultInfo.pageCount && position >= current.resultInfo.count)
			return false;
		next = current.data.get(position);
		return true;
	}

	@Override
	public Map<String, Object> next() {
		if (!hasNext())
			throw new NoSuchElementException();
		Map<String, Object> next = this.next;
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
		current = client.search(queryBuilder.build());
	}

}
