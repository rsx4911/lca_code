package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ProcessType;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.glad.ModellingApproach;


public class DsVersion {

	public String objectId;
	public List<DsRepo> repos = new ArrayList<>();

	public String name;
	public String category;
	public List<String> categoryPaths;
	public List<String> tags;
	public ModellingApproach modellingApproach = ModellingApproach.UNKNOWN;

	// if flow
	public FlowType flowType;

	// if process
	public ProcessType processType;
	public String contact;
	public String location;
	public Integer validFromYear;
	public Integer validUntilYear;

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
