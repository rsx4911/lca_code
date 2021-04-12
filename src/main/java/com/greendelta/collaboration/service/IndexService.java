package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.repository.Descriptors.Descriptor;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.search.DataFill;
import com.greendelta.collaboration.service.search.IndexEntryCreator;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.GsonTypes;

public class IndexService {

	private final SearchService searchService;

	@Inject
	public IndexService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void index(Repository repo) {
		List<Commit> commits = repo.commits.find().all();
		Runner runner = new Runner(repo);
		for (Commit commit : commits) {
			runner.run(commit);
		}
	}

	private Dataset toDataset(Descriptor descriptor, Map<String, Object> data) {
		ObjectMap map = ObjectMap.fromMap(data);
		Dataset dataset = new Dataset();
		dataset.type = descriptor.type;
		dataset.refId = descriptor.refId;
		dataset.name = map.getString("name");
		dataset.version = map.getString("version");
		dataset.categoryRefId = map.getString("category.@id");
		dataset.lastChange = Dates.getTime(map.getString("lastChange"));
		if (descriptor.type == ModelType.CATEGORY) {
			dataset.categoryType = ModelType.valueOf(map.getString("modelType"));
		} else {
			dataset.categoryType = dataset.type;
		}
		// String[] tags = map.getStringArray("tags");
		// dataset.tags = tags != null ? Arrays.asList(tags) : null;
		return dataset;
	}

	private class Runner {

		private final Repository repo;
		private final Map<ModelType, Map<String, IndexAction>> lastActions = new HashMap<>();
		private final Map<ModelType, Map<String, Dataset>> lastDatasets = new HashMap<>();

		private Runner(Repository repo) {
			this.repo = repo;
		}

		private void run(Commit commit) {
			InputOutputList ioList = new InputOutputList(searchService, repo, commit);
			List<IndexEntry> entries = createIndexEntries(commit, ioList);
			if (entries == null || entries.isEmpty())
				return;
			for (IndexEntry entry : entries) {
				fillCategory(entry);
			}
			searchService.index(repo, commit.id, entries);
			ioList.index();
		}

		@SuppressWarnings("unchecked")
		private List<IndexEntry> createIndexEntries(Commit commit, InputOutputList ioList) {
			List<IndexEntry> entries = new ArrayList<>();
			IndexEntryCreator indexEntryCreator = new IndexEntryCreator(repo, commit);
			Gson gson = new Gson();
			Iterator<Descriptor> descriptors = repo.descriptors.get(commit);
			while (descriptors.hasNext()) {
				Descriptor descriptor = descriptors.next();
				IndexAction lastAction = Collections.get(lastActions, descriptor.type, descriptor.refId);
				String json = repo.datasets.get(descriptor.type, descriptor.refId, commit.id);
				Map<String, Object> data = gson.fromJson(json, GsonTypes.OBJECT_MAP);
				Dataset dataset = data.isEmpty() ? Collections.get(lastDatasets, descriptor.type, descriptor.refId)
						: toDataset(descriptor, data);
				IndexEntry entry = indexEntryCreator.create(dataset, lastAction, data);
				if (dataset.type == ModelType.PROCESS && !data.isEmpty()) {
					ioList.append(dataset.refId, (List<Map<String, Object>>) data.get("exchanges"));
				}
				Collections.put(lastActions, descriptor.type, descriptor.refId, entry.action);
				Collections.put(lastDatasets, descriptor.type, descriptor.refId, dataset);
				entries.add(entry);
			}
			return entries;
		}

		private void fillCategory(IndexEntry entry) {
			Dataset dataset = Collections.get(lastDatasets, entry.type, entry.refId);
			entry.categories = getCategories(dataset, true);
			entry.fullPath = entry.name;
			if (entry.categories != null && entry.categories.size() > 0) {
				entry.fullPath = Strings.join(entry.categories, '/') + '/' + entry.name;
			}
			DataFill.categories(entry, entry.categories);
		}

		private List<String> getCategories(Dataset dataset, boolean initialCall) {
			if (dataset.categoryRefId == null)
				return null;
			Map<String, Dataset> categories = lastDatasets.get(ModelType.CATEGORY);
			if (categories == null)
				return null;
			Dataset parent = categories.get(dataset.categoryRefId);
			if (parent == null)
				return null;
			List<String> list = getCategories(parent, false);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(parent.name);
			return list;
		}

	}

}
