package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.greendelta.search.wrapper.SearchResult;

public class SearchResults {

	public static <T> SearchResult<T> from(Collection<T> data) {
		return from(data, 0, 0, data.size());
	}

	public static <T> SearchResult<T> from(Collection<T> data, int page, int pageSize, long total) {
		return from(data, data.size(), page, pageSize, total);
	}

	public static <T> SearchResult<T> from(Collection<T> data, long count, int page, int pageSize, long total) {
		var result = new SearchResult<T>();
		result.data.addAll(data);
		result.resultInfo.count = count;
		result.resultInfo.currentPage = page == 0 ? 1 : page;
		result.resultInfo.pageSize = page == 0 ? 0 : pageSize;
		if (page > 0 && pageSize > 0) {
			result.resultInfo.pageCount = (int) Math.ceil(total / (double) pageSize);
		} else {
			result.resultInfo.pageCount = 1;
		}
		result.resultInfo.totalCount = total;
		return result;
	}

	public static <T, V> SearchResult<V> convert(Collection<T> data, Function<T, V> converter) {
		return convert(from(data), converter);
	}

	public static <T, V> SearchResult<V> convert(SearchResult<T> r, Function<T, V> converter) {
		var data = r.data.stream().map(converter).toList();
		var converted = from(data, r.resultInfo.currentPage, r.resultInfo.pageSize, r.resultInfo.totalCount);
		converted.aggregations.addAll(r.aggregations);
		return converted;
	}

	public static <T, V> SearchResult<V> listConvert(SearchResult<T> r, Function<List<T>, List<V>> converter) {
		var data = converter.apply(r.data);
		var converted = from(data, r.resultInfo.currentPage, r.resultInfo.pageSize, r.resultInfo.totalCount);
		converted.aggregations.addAll(r.aggregations);
		return converted;
	}

	public static <T> SearchResult<T> paged(int page, int pageSize, Collection<T> toFilter) {
		return pagedAndFiltered(page, pageSize, null, toFilter, null);
	}

	public static <T> SearchResult<T> pagedAndFiltered(int page, int pageSize, String filter, Collection<T> toFilter) {
		return pagedAndFiltered(page, pageSize, filter, toFilter, (value) -> {
			return value.toString();
		});
	}

	public static <T extends Map<String, Object>> SearchResult<T> pagedAndFiltered(int page, int pageSize,
			String filter, Collection<T> toFilter, String field) {
		return pagedAndFiltered(page, pageSize, filter, toFilter, map -> Maps.get(map, field));
	}

	public static <T> SearchResult<T> pagedAndFiltered(int page, int pageSize, String filter, Collection<T> toFilter,
			Function<T, String> toString) {
		var filtered = filter == null || filter.isEmpty()
				? new ArrayList<>(toFilter)
				: toFilter.stream()
						.filter(e -> toString.apply(e).toLowerCase().contains(filter.toLowerCase()))
						.toList();
		var paged = new ArrayList<T>();
		if (page == 0 || pageSize == 0) {
			paged = new ArrayList<>(filtered);
		} else {
			for (var i = 0; i < filtered.size(); i++) {
				if (i < ((page - 1) * pageSize))
					continue;
				if (i >= (page * pageSize))
					break;
				paged.add(filtered.get(i));
			}
		}
		return from(paged, page, pageSize, filtered.size());
	}

}
