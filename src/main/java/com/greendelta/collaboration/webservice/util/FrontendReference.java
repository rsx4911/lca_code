package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.cloud.api.git.Reference;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.service.Repository;

public class FrontendReference {

	public ModelType type;
	public String refId;
	public String fullPath;
	public String commitId;
	public String name;

	public static List<Reference> collect(Repository repo, List<FrontendReference> refs) {
		List<Reference> all = new ArrayList<>();
		Set<String> paths = new HashSet<>();
		for (FrontendReference ref : refs) {
			if (ref.refId != null) {
				if (paths.contains(ref.fullPath))
					continue;
				all.add(repo.references.get(ref.type, ref.refId, ref.commitId));
			} else {
				for (Reference r : repo.references.find().path(ref.fullPath).commit(ref.commitId).all()) {
					if (paths.contains(r.fullPath))
						continue;
					all.add(r);
				}
			}
		}
		return all;
	}

}