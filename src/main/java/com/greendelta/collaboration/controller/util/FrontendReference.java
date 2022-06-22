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
	public String path;
	public String commitId;

	public static List<Reference> collect(Repository repo, List<FrontendReference> refs) {
		var all = new ArrayList<Reference>();
		var paths = new HashSet<String>();
		for (var ref : refs) {
			if (ref.refId != null) {
				var r = repo.references().get(ref.type, ref.refId, ref.commitId);
				if (!paths.contains(r.path)) {
					all.add(r);
					paths.add(r.path);
				}
			} else {
				repo.references().find()
						.path(ref.path)
						.commit(ref.commitId)
						.all().stream()
						.filter(r -> !paths.contains(r.path))
						.forEach(r -> {
							all.add(r);
							paths.add(r.path);
						});
			}
		}
		return all;
	}

}