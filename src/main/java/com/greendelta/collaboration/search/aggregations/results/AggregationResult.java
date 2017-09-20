package com.greendelta.collaboration.search.aggregations.results;

import java.util.List;
import java.util.Map;

public class AggregationResult {

	public final String name;
	public final String type;
	public final long totalCount;
	public final Map<String, String> data;
	public final List<AggregationResultEntry> entries;

	AggregationResult(String name, String type, long totalCount, Map<String, String> data,
			List<AggregationResultEntry> entries) {
		this.name = name;
		this.type = type;
		this.totalCount = totalCount;
		this.data = data;
		this.entries = entries;
	}

	@Override
	public String toString() {
		String s = "{name=" + name + ", ";
		s += "type=" + type + ", ";
		s += "totalCount=" + totalCount + ", ";
		s += "entries=[";
		if (entries != null) {
			int i = 0;
			for (AggregationResultEntry e : entries) {
				s += e.toString();
				i++;
				if (i < entries.size()) {
					s += ", ";
				}
			}
		}
		return s + "]}";
	}

}
