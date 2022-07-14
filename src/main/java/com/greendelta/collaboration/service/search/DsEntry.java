package com.greendelta.collaboration.service.search;

import java.util.HashSet;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.git.util.TypedRefId;

public class DsEntry extends TypedRefId {

	public DsEntry(ModelType type, String refId) {
		super(type, refId);
	}
	
	public Set<DsVersion> versions = new HashSet<>();

}
