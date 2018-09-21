package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Dates;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.search.DataFill;
import com.greendelta.collaboration.service.search.IndexEntryCreator;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;

public class ReindexService {

	private final HistoryService historyService;
	private final SearchService searchService;

	@Inject
	public ReindexService(HistoryService historyService, SearchService searchService) {
		this.historyService = historyService;
		this.searchService = searchService;
	}

	public void reindex(Repository repo) {
		List<Commit> commits = historyService.getCommits(repo);
		Runner runner = new Runner(repo);
		for (Commit commit : commits) {
			List<IndexEntry> entries = runner.run(commit);
			if (entries.isEmpty())
				return;
			searchService.index(repo.toId(), entries);
		}
	}

	private <T> T get(Map<ModelType, Map<String, T>> map, FileReference ref) {
		Map<String, T> inner = map.get(ref.type);
		if (inner == null)
			return null;
		return inner.get(ref.refId);
	}

	private <T> void put(Map<ModelType, Map<String, T>> map, FileReference ref, T value) {
		Map<String, T> inner = map.get(ref.type);
		if (inner == null) {
			map.put(ref.type, inner = new HashMap<>());
		}
		inner.put(ref.refId, value);
	}

	private Dataset toDataset(Commit commit, FileReference ref, Map<String, Object> data) {
		ObjectMap map = ObjectMap.fromMap(data);
		Dataset dataset = new Dataset();
		dataset.type = ref.type;
		dataset.refId = ref.refId;
		dataset.name = map.getString("name");
		dataset.version = map.getString("version");
		dataset.categoryRefId = map.getString("category.@id");
		dataset.lastChange = Dates.getTime(map.getString("lastChange"));
		if (ref.type == ModelType.CATEGORY) {
			dataset.categoryType = ModelType.valueOf(map.getString("modelType"));
		}
		return dataset;
	}

	private class Runner {

		private final Repository repo;
		private final Map<ModelType, Map<String, IndexAction>> lastActions = new HashMap<>();
		private final Map<ModelType, Map<String, Dataset>> lastDatasets = new HashMap<>();
		private final Map<String, List<FileReference>> commitRefs = new HashMap<>();

		private Runner(Repository repo) {
			this.repo = repo;
			collectRefs(ModelType.CATEGORY);
			for (ModelType type : ModelTypes.SORTED) {
				collectRefs(type);
			}
			collectRefs(ModelType.IMPACT_CATEGORY);
			collectRefs(ModelType.NW_SET);
		}

		private List<IndexEntry> run(Commit commit) {
			List<IndexEntry> entries = new ArrayList<>();
			List<FileReference> refs = commitRefs.get(commit.id);
			if (refs == null || refs.isEmpty())
				return entries;
			IndexEntryCreator indexEntryCreator = new IndexEntryCreator(repo, commit);
			for (FileReference ref : refs) {
				IndexAction lastAction = get(lastActions, ref);
				File file = repo.getDatasetFile(ref.type, ref.refId, commit.id, false);
				IndexEntry entry = null;
				Dataset dataset = null;
				Map<String, Object> data = IndexEntryCreator.readData(file);
				if (data.isEmpty()) {
					dataset = get(lastDatasets, ref);
					entry = indexEntryCreator.create(dataset);
				} else {
					dataset = toDataset(commit, ref, data);
					entry = indexEntryCreator.create(dataset, lastAction, data);
				}
				put(lastActions, ref, entry.action);
				put(lastDatasets, ref, dataset);
				entries.add(entry);
			}
			for (IndexEntry entry : entries) {
				Dataset dataset = get(lastDatasets, entry.asFileReference());
				entry.categories = getCategories(dataset, true);
				entry.fullPath = entry.name;
				if (entry.categories != null && entry.categories.size() > 0) {
					entry.fullPath = Strings.join(entry.categories, '/') + '/' + entry.name;
				}
				DataFill.categories(entry, entry.categories);
			}
			return entries;
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

		private void collectRefs(ModelType type) {
			File modelDir = repo.getModelDir(type, false);
			if (!modelDir.exists())
				return;
			for (File file : modelDir.listFiles()) {
				for (File model : file.listFiles()) {
					for (File version : model.listFiles()) {
						String refId = model.getName();
						String commitId = version.getName().substring(0, version.getName().indexOf(".json"));
						List<FileReference> refs = commitRefs.get(commitId);
						if (refs == null) {
							commitRefs.put(commitId, refs = new ArrayList<>());
						}
						FileReference ref = new FileReference();
						ref.type = type;
						ref.refId = refId;
						refs.add(ref);
					}
				}
			}
		}
	}

}
