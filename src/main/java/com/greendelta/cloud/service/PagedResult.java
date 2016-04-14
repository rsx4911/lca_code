package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PagedResult<T> {

	public final long page;
	public final String filter;
	public final long total;
	public final long subTotal;
	public final long pageSize;
	public final List<T> data;

	public PagedResult(long page, String filter, long total, long subTotal, List<T> data) {
		this.page = page;
		this.pageSize = 10;
		this.filter = filter;
		this.total = total;
		this.subTotal = subTotal;
		this.data = data;
	}

	public PagedResult(String filter, long total, long subTotal, List<T> data) {
		this.page = -1;
		this.pageSize = -1;
		this.filter = filter;
		this.total = total;
		this.subTotal = subTotal;
		this.data = data;
	}

	public PagedResult<Map<String, Object>> toClient(Function<List<T>, List<Map<String, Object>>> mapper) {
		return new PagedResult<>(page, filter, total, subTotal, mapper.apply(data));
	}

	static <T> PagedResult<T> pagedAndFiltered(int page, String filter, List<T> toFilter) {
		return pagedAndFiltered(page, filter, toFilter, (value) -> {
			return value.toString();
		});
	}

	static <T> PagedResult<T> pagedAndFiltered(int page, String filter, List<T> toFilter, Function<T, String> toString) {
		List<T> filtered = new ArrayList<>();
		if (filter == null || filter.isEmpty())
			filtered = toFilter;
		else
			for (T group : toFilter)
				if (toString.apply(group).contains(filter))
					filtered.add(group);
		List<T> paged = new ArrayList<>();
		for (int i = 0; i < filtered.size(); i++)
			if (i < ((page - 1) * 10))
				continue;
			else if (i > (page * 10))
				break;
			else
				paged.add(filtered.get(i));
		return new PagedResult<T>(page, filter, toFilter.size(),
				filtered.size(), paged);
	}

}
