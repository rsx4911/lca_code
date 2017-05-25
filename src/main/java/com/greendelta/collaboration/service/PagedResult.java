package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.util.Client;

public class PagedResult<T> {

	public final long page;
	public final String filter;
	public final long total;
	public final long subTotal;
	public final long pageSize;
	public final List<T> data;

	public PagedResult(long page, long pageSize, String filter, long total, long subTotal, List<T> data) {
		this.page = page;
		this.pageSize = pageSize;
		this.filter = filter;
		this.total = total;
		this.subTotal = subTotal;
		this.data = data;
	}

	public PagedResult(long page, String filter, long total, long subTotal, List<T> data) {
		this(page, 10, filter, total, subTotal, data);
	}

	public PagedResult(List<T> data) {
		this(0, data.size(), null, data.size(), data.size(), data);
	}

	public PagedResult<ObjectMap> toClient(Function<T, ObjectMap> mapper) {
		return new PagedResult<>(page, pageSize, filter, total, subTotal, Client.map(data, mapper));
	}

	public PagedResult<ObjectMap> toClient2(Function<List<T>, List<ObjectMap>> mapper) {
		return new PagedResult<>(page, pageSize, filter, total, subTotal, mapper.apply(data));
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
			else if (i >= (page * 10))
				break;
			else
				paged.add(filtered.get(i));
		return new PagedResult<T>(page, filter, toFilter.size(), filtered.size(), paged);
	}

}
