package com.greendelta.collaboration.model;

public class Restriction {

	public final String datasetRefId;
	public final String name;
	public final RestrictionType type;

	public Restriction(String datasetRefId, String name, RestrictionType type) {
		this.datasetRefId = datasetRefId;
		this.name = name;
		this.type = type;
	}

	public enum RestrictionType {

		WARNING,

		FORBIDDEN;

	}
	
}
