package com.greendelta.collaboration.model;

public class RestrictionEntry {

	public final String datasetRefId;
	public final String name;
	public final RestrictionType type;

	public RestrictionEntry(String datasetRefId, String name, RestrictionType type) {
		this.datasetRefId = datasetRefId;
		this.name = name;
		this.type = type;
	}

	public enum RestrictionType {

		WARNING,

		FORBIDDEN;

	}
	
}
