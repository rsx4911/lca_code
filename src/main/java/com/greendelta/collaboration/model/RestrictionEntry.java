package com.greendelta.collaboration.model;

public class RestrictionEntry {

	public final String datasetRefId;
	public final String restriction;
	public final RestrictionType type;

	public RestrictionEntry(String datasetRefId, String restriction, RestrictionType type) {
		this.datasetRefId = datasetRefId;
		this.restriction = restriction;
		this.type = type;
	}

	public enum RestrictionType {

		WARNING,

		FORBIDDEN;

	}
	
}
