package com.greendelta.collaboration.search;

public class SearchParameterValue {

	public final String value;
	public final Type type;

	public SearchParameterValue(String value, Type type) {
		this.value = value;
		this.type = type;
	}

	public static enum Type {

		PHRASE, WILDCART;

	}

}
