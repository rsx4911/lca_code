package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.greendelta.lca.search.SearchResult;

public class SearchResults {

	public static <T> SearchResult<T> from(List<T> data) {
		return from(data, 0, 0, data.size());
	}

	public static <T> SearchResult<T> from(List<T> data, int page, int pageSize, long total) {
		SearchResult<T> result = new SearchResult<>();
		result.data.addAll(data);
		result.resultInfo.count = data.size();
		result.resultInfo.currentPage = page;
		result.resultInfo.pageSize = page == 0 ? 0 : pageSize;
		if (page > 0) {
			result.resultInfo.pageCount = (int) Math.ceil(total / (double) pageSize);
		}
		result.resultInfo.totalCount = total;
		return result;
	}

	public static <T, V> SearchResult<V> convert(List<T> data, Function<T, V> converter) {
		return convert(from(data), converter);
	}

	public static <T, V> SearchResult<V> convert(SearchResult<T> result, Function<T, V> converter) {
		List<V> data = new ArrayList<>();
		for (T element : result.data) {
			data.add(converter.apply(element));
		}
		SearchResult<V> converted = from(data, result.resultInfo.currentPage, result.resultInfo.pageSize,
				result.resultInfo.totalCount);
		converted.aggregations.addAll(result.aggregations);
		return converted;
	}

	public static <T> SearchResult<T> pagedAndFiltered(int page, String filter, List<T> toFilter) {
		return pagedAndFiltered(page, filter, toFilter, (value) -> {
			return value.toString();
		});
	}

	public static <T> SearchResult<T> pagedAndFiltered(int page, String filter, List<T> toFilter,
			Function<T, String> toString) {
		List<T> filtered = new ArrayList<>();
		if (filter == null || filter.isEmpty()) {
			filtered = new ArrayList<>(toFilter);
		} else {
			for (T group : toFilter) {
				if (!toString.apply(group).contains(filter))
					continue;
				filtered.add(group);
			}
		}
		List<T> paged = new ArrayList<>();
		if (page == 0) {
			paged = new ArrayList<>(filtered);
		} else {
			for (int i = 0; i < filtered.size(); i++) {
				if (i < ((page - 1) * 10))
					continue;
				if (i >= (page * 10))
					break;
				paged.add(filtered.get(i));
			}
		}
		return from(paged, page, 10, filtered.size());
	}

}
