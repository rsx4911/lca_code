package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.openlca.core.model.ModelType;
import org.openlca.util.KeyGen;

import com.google.common.base.Strings;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.BrowseService.BrowseParameter;
import com.greendelta.collaboration.util.ObjectMap;

public class ReferenceCollector<T> {

	private final BrowseService browseService;
	private final Function<Reference, T> converter;

	public ReferenceCollector(BrowseService browseService, Function<Reference, T> converter) {
		this.browseService = browseService;
		this.converter = converter;
	}

	public Set<T> getReferences(Repository repo, List<Reference> in) {
		Set<T> all = new HashSet<>();
		for (Reference reference : in) {
			if (reference.type != null && Strings.isNullOrEmpty(reference.id)) {
				all.addAll(collectForType(repo, reference.type));
			} else if (reference.type == ModelType.CATEGORY && !Strings.isNullOrEmpty(reference.id)) {
				all.addAll(collectForCategory(repo, reference.categoryType, toId(reference.id)));
			} else {
				all.add(converter.apply(reference));
			}
		}
		return all;
	}

	private List<T> collectForType(Repository repo, ModelType type) {
		return convert(repo, browseService.getAll(type, new BrowseParameter(repo)));
	}

	private List<T> collectForCategory(Repository repo, ModelType type, String id) {
		return convert(repo, browseService.getForCategory(type, id, new BrowseParameter(repo)));
	}

	private String toId(String categoryPath) {
		return KeyGen.get(categoryPath.split("/"));
	}

	private List<T> convert(Repository repo, List<ObjectMap> entries) {
		List<T> references = new ArrayList<>();
		for (ObjectMap e : entries) {
			Reference ref = new Reference();
			if (e.get("type") == ModelType.CATEGORY) {
				references.addAll(convert(repo, browseService.getForCategory(e.get("categoryType"), e.get("refId"),
						new BrowseParameter(repo))));
			} else {
				ref.type = e.get("type");
				ref.categoryType = e.get("categoryType");
				ref.id = e.getString("refId");
				ref.commitId = e.getString("commitId");
				ref.name = e.getString("name");
				references.add(converter.apply(ref));
			}
		}
		return references;
	}

	public static class Reference {

		public ModelType type;
		public String id;
		public ModelType categoryType;
		public String name;
		public String commitId;

	}
}
