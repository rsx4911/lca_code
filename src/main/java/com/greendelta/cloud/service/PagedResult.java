package com.greendelta.cloud.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PagedResult<T> {

	public final long page;
	public final String filter;
	public final long total;
	public final long subTotal;
	public final long pageSize = 10;
	public final List<T> data;

	PagedResult(long page, String filter, long total, long subTotal,
			List<T> data) {
		this.page = page;
		this.filter = filter;
		this.total = total;
		this.subTotal = subTotal;
		this.data = data;
	}

	public PagedResult<Map<String, Object>> toClient(
			Function<List<T>, List<Map<String, Object>>> mapper) {
		return new PagedResult<>(page, filter, total, subTotal,
				mapper.apply(data));
	}

}
