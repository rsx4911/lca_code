package com.greendelta.collaboration.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SearchParameter {

	public final String name;
	public final Set<SearchParameterValue> values = new HashSet<>();
	public final Conjunction type;

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

	@Override
	public String toString() {
		if (values.isEmpty())
			return "{" + name + "=}";
		if (values.size() == 1)
			return "{" + name + "=" + values.iterator().next().value + "}";
		String s = "{" + name + "=[";
		Iterator<SearchParameterValue> it = values.iterator();
		while (it.hasNext()) {
			s += it.next().value;
		}
		return s + "]}";
	}
}
