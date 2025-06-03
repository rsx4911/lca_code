package com.greendelta.collaboration.service;

import com.greendelta.search.wrapper.SearchResult;

public class RepositorySearchResult extends SearchResult<Repository> implements AutoCloseable {

	RepositorySearchResult() {
	}

	RepositorySearchResult(SearchResult<Repository> result) {
		aggregations.addAll(result.aggregations);
		data.addAll(result.data);
		resultInfo.count = result.resultInfo.count;
		resultInfo.currentPage = result.resultInfo.currentPage;
		resultInfo.pageCount = result.resultInfo.pageCount;
		resultInfo.pageSize = result.resultInfo.pageSize;
		resultInfo.totalCount = result.resultInfo.totalCount;
	}

	@Override
	public void close() {
		data.forEach(Repository::close);
	}

}
