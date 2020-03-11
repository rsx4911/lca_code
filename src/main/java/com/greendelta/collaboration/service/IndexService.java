package com.greendelta.collaboration.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.collaboration.util.ObjectMap;

public class IndexService {

	private final HistoryService historyService;
	private final SearchService searchService;

	@Inject
	public IndexService(HistoryService historyService, SearchService searchService) {
		this.historyService = historyService;
		this.searchService = searchService;
	}

	public void index(Repository repo) {
		List<Commit> commits = historyService.getCommits(repo);
		Runner runner = new Runner(repo);
		for (Commit commit : commits) {
			runner.run(commit);
		}
	}

	private Dataset toDataset(FileReference ref, Map<String, Object> data) {
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
		} else {
			dataset.categoryType = dataset.type;
		}
		String[] tags = map.getStringArray("tags");
		dataset.tags = tags != null ? Arrays.asList(tags) : null;
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
			List<FileReference> refs = commitRefs.get(commit.id);
			if (refs == null || refs.isEmpty())
				return null;
			List<IndexEntry> entries = new ArrayList<>();
			IndexEntryCreator indexEntryCreator = new IndexEntryCreator(repo, commit);
			for (FileReference ref : refs) {
				IndexAction lastAction = Collections.get(lastActions, ref.type, ref.refId);
				Map<String, Object> data = repo.readData(ref.type, ref.refId, commit.id);
				Dataset dataset = data.isEmpty() ? Collections.get(lastDatasets, ref.type, ref.refId)
						: toDataset(ref, data);
				IndexEntry entry = indexEntryCreator.create(dataset, lastAction, data);
				if (dataset.type == ModelType.PROCESS && !data.isEmpty()) {
					ioList.append(dataset.refId, (List<Map<String, Object>>) data.get("exchanges"));
				}
				Collections.put(lastActions, ref.type, ref.refId, entry.action);
				Collections.put(lastDatasets, ref.type, ref.refId, dataset);
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
