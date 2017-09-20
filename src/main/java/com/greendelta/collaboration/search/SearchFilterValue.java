package com.greendelta.collaboration.search;

public class SearchFilterValue {

	public final String value;
	public final Type type;

	public SearchFilterValue(String value, Type type) {
		this.value = value;
		this.type = type;
	}

	public static enum Type {

		PHRASE, WILDCART;

	}

}
