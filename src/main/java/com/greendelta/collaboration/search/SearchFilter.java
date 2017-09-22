package com.greendelta.collaboration.search;

import java.util.HashSet;
import java.util.Set;

public class SearchFilter {

	public final String field;
	public final Set<SearchFilterValue> values;
	public final Conjunction conjunction;

	public SearchFilter(String field) {
		this(field, new HashSet<>());
	}

	public SearchFilter(String field, SearchFilterValue value) {
		this(field, toSet(value), null);
	}

	public SearchFilter(String field, Set<SearchFilterValue> values) {
		this(field, values, null);
	}

	public SearchFilter(String field, Set<SearchFilterValue> values, Conjunction conjunction) {
		this.field = field;
		this.values = values;
		this.conjunction = conjunction == null ? Conjunction.OR : conjunction;
	}

	public enum Conjunction {
		AND, OR;
	}

	@Override
	public String toString() {
		String s = "{" + field + "=";
		if (values.size() == 0)
			return s + "}";
		if (values.size() == 1)
			return s + values.iterator().next().value + "}";
		String[] values = this.values.toArray(new String[this.values.size()]);
		for (int i = 0; i < values.length; i++) {
			s += values[i] + "=";
			for (int j = 0; j < values.length; j++) {
				s += values[j];
				if (j < values.length - 1) {
					s += " " + conjunction.name() + " ";
				}
			}
		}
		return s + "}";
	}
	
	private static Set<SearchFilterValue> toSet(SearchFilterValue value) {
		Set<SearchFilterValue> set = new HashSet<>();
		set.add(value);
		return set;
	}
	
}
