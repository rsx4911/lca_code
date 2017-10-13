package com.greendelta.collaboration.webservice.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.core.model.ModelType;
import org.openlca.util.KeyGen;

import com.google.common.base.Strings;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.model.task.ReviewReference;
import com.greendelta.collaboration.service.BrowseService;
import com.greendelta.collaboration.service.Repository;

class ReferenceCollector {
	
	private final BrowseService browseService;
	
	ReferenceCollector(BrowseService browseService) {
		this.browseService = browseService;
	}

	Set<ReviewReference> getReferences(Repository repo, List<Reference> in) {
		Set<ReviewReference> all = new HashSet<>();
		for (Reference reference : in) {
			if (reference.type != null && Strings.isNullOrEmpty(reference.id)) {
				all.addAll(collectForType(repo, reference.type));
			} else if (reference.type == ModelType.CATEGORY && !Strings.isNullOrEmpty(reference.id)) {
				all.addAll(collectForCategory(repo, toId(reference.id)));
			} else {
				all.add(convert(repo, reference));
			}
		}
		return all;
	}

	private List<ReviewReference> collectForType(Repository repo, ModelType type) {
		return convert(repo, browseService.getAll(repo, type));
	}

	private List<ReviewReference> collectForCategory(Repository repo, String id) {
		return convert(repo, browseService.getForCategory(repo, id));
	}

	private String toId(String categoryPath) {
		return KeyGen.get(categoryPath.split("/"));
	}

	private List<ReviewReference> convert(Repository repo, List<IndexEntry> entries) {
		List<ReviewReference> references = new ArrayList<>();
		for (IndexEntry entry : entries) {
			ReviewReference ref = new ReviewReference();
			if (ref.type == ModelType.CATEGORY) {
				references.addAll(convert(repo, browseService.getForCategory(repo, ref.refId)));
			} else {
				ref.type = entry.type;
				ref.refId = entry.refId;
				ref.commitId = entry.commitId;
				ref.name = entry.name;
				references.add(ref);
			}
		}
		return references;
	}

	private ReviewReference convert(Repository repo, Reference ref) {
		ReviewReference reference = new ReviewReference();
		reference.type = ref.type;
		reference.refId = ref.id;
		reference.commitId = ref.commitId;
		reference.name = ref.name;
		return reference;
	}
	
	static class Reference {

		public String id;
		public ModelType type;
		public String name;
		public String commitId;

	}
}
