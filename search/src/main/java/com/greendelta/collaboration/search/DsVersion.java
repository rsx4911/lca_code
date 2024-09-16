package com.greendelta.collaboration.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ProcessType;
import org.openlca.util.Strings;

public class DsVersion {

	public String objectId;
	public List<DsRepo> repos = new ArrayList<>();

	public String name;
	public String category;
	public List<String> categoryPaths;
	public List<String> tags;

	// if process or flow
	public FlowType flowType;

	// if process or epd
	public Integer validFromYear;
	public Integer validUntilYear;

	// if process
	public ProcessType processType;
	public ModellingApproach modellingApproach = ModellingApproach.UNKNOWN;
	public String contact;
	public String location;
	public List<String> reviewTypes;
	public List<String> complianceDeclarations;
	public List<String> flowCompleteness;
	
	void completeData() {
		if (Strings.nullOrEmpty(category))
			return;
		categoryPaths = new ArrayList<>();
		String path = null;
		for (var category : Arrays.asList(category.split("/"))) {
			path = path == null ? category : path + "/" + category;
			categoryPaths.add(path);
		}
	}
}
