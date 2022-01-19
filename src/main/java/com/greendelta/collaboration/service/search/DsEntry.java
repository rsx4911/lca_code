package com.greendelta.collaboration.service.search;

import java.util.HashSet;
import java.util.Set;

import org.openlca.core.model.ModelType;

public class DsEntry {

	public ModelType type;
	public String refId;
	public Set<DsVersion> versions = new HashSet<>();

	public String toIndexId() {
		return toIndexId(type, refId);
	}

	public static String toIndexId(ModelType type, String refId) {
		return type.name() + "/" + refId;
	}
	
}
