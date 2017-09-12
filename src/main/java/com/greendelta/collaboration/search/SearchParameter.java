package com.greendelta.collaboration.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SearchParameter {

	public final String name;
	protected final Set<SearchParameterValue> values = new HashSet<>();
	protected final Conjunction type;

	public SearchParameter(String name, Conjunction type) {
		this.name = name;
		this.type = type;
	}

	public void addAll(Collection<SearchParameterValue> values) {
		this.values.addAll(values);
	}

	public void add(SearchParameterValue value) {
		this.values.add(value);
	}

	public enum Conjunction {
		AND, OR;
	}
}
