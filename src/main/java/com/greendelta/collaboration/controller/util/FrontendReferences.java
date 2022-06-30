package com.greendelta.collaboration.controller.util;

import java.util.List;
import java.util.stream.Collectors;

import org.openlca.git.model.Reference;

import com.greendelta.collaboration.service.Repository;

public class FrontendReferences {

	public String commitId;
	public List<String> paths;

	public static List<Reference> collect(Repository repo, FrontendReferences references) {
		return references.paths.stream()
				.map(path -> repo.references().find()
						.path(path)
						.commit(references.commitId)
						.all())
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

}