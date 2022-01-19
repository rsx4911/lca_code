package com.greendelta.collaboration.controller.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openlca.core.model.ModelType;
import org.openlca.git.model.Reference;

import com.greendelta.collaboration.service.Repository;

public class FrontendReference {

	public ModelType type;
	public String refId;
	public String fullPath;
	public String commitId;
	public String name;

	public static List<Reference> collect(Repository repo, List<FrontendReference> refs) {
		var all = new ArrayList<Reference>();
		var paths = new HashSet<String>();
		for (var ref : refs) {
			if (ref.refId != null) {
				if (paths.contains(ref.fullPath))
					continue;
				all.add(repo.references().get(ref.type, ref.refId, ref.commitId));
			} else {
				all.addAll(repo.references().find()
						.path(ref.fullPath).commit(ref.commitId)
						.all().stream().filter(r -> !paths.contains(r.fullPath))
						.toList());
			}
		}
		return all;
	}

}