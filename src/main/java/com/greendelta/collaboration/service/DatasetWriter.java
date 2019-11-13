package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.AuthorizationException;
import org.openlca.cloud.api.data.ModelStreamReader;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.CommitService.InsufficientStorageException;
import com.greendelta.collaboration.service.search.IndexEntryCreator;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.search.SearchService.BulkUpdate;

class DatasetWriter {

	private final InputOutputListAppending ioList = new InputOutputListAppending();
	private final SearchService searchService;
	private final LibraryService libraryService;
	private final Repository repo;
	private final Commit commit;
	private final List<Dataset> datasets = new ArrayList<>();
	private final List<IndexEntry> indexEntries = new ArrayList<>();
	private final IndexEntryCreator indexEntryCreator;
	private final Role userRole;
	private final long maxUserGroupSize;
	private long currentRepoSize;
	private long currentUserGroupSize;

	DatasetWriter(SearchService searchService, LibraryService libraryService, long currentUserGroupSize,
			long maxUserGroupSize, Repository repo, Commit commit, Role userRole) {
		this.searchService = searchService;
		this.libraryService = libraryService;
		this.repo = repo;
		this.commit = commit;
		this.indexEntryCreator = new IndexEntryCreator(repo, commit);
		this.currentRepoSize = repo.getSize();
		this.userRole = userRole;
		this.currentUserGroupSize = currentUserGroupSize;
		this.maxUserGroupSize = maxUserGroupSize;
	}

	void writeDatasets(ModelStreamReader reader) throws IOException {
		try {
			while (reader.hasMore()) {
				Dataset dataset = reader.readNextPartAsDataset();
				if (!checkLibraryRestrictions(dataset))
					throw new AuthorizationException("User is not allowed to commit changes to "
							+ dataset.type.name() + " " + dataset.refId);
				datasets.add(dataset);
				File file = repo.getDatasetFile(dataset.type, dataset.refId, commit.id, true);
				if (!write(reader, dataset, file)) {
					indexEntries.add(indexEntryCreator.create(dataset));
					continue;
				}
				createIndexEntry(dataset);
				writeBinaries(reader, dataset);
			}
			repo.updateSize(currentRepoSize);
			searchService.index(repo, commit.id, indexEntries);
			ioList.index();
		} catch (Exception e) {
			cleanup(repo, datasets, commit);
			throw e;
		}
	}

	private boolean checkLibraryRestrictions(Dataset dataset) {
		Map<String, Role> libraryRestrictions = libraryService.getRestrictions(repo);
		Set<String> libraries = libraryService.getLibraryNames(dataset.refId);
		for (String library : libraries) {
			Role restrictedTo = libraryRestrictions.get(library);
			if (restrictedTo == null)
				continue;
			if (!restrictedTo.matches(userRole))
				return false;
		}
		return true;
	}

	private boolean write(ModelStreamReader reader, Dataset dataset, File file) throws IOException {
		int size = 0;
		try (OutputStream out = new FileOutputStream(file)) {
			size = reader.readNextPartToStream(out);
			out.close();
		}
		if (size == 0)
			return false;
		currentRepoSize += size;
		if (maxUserGroupSize > 0) {
			currentUserGroupSize += size;
		}
		checkSize();
		return true;
	}

	@SuppressWarnings("unchecked")
	private void createIndexEntry(Dataset dataset) {
		IndexAction lastAction = searchService.getMostRecentAction(repo, dataset.type, dataset.refId);
		Map<String, Object> data = new HashMap<>();
		if (dataset.type == ModelType.PROCESS || dataset.type == ModelType.FLOW) {
			data = repo.readData(dataset.type, dataset.refId, commit.id);
			if (dataset.type == ModelType.PROCESS) {
				ioList.append(dataset.refId, (List<Map<String, Object>>) data.get("exchanges"));
			}
		}
		indexEntries.add(indexEntryCreator.create(dataset, lastAction, data));
	}

	private void checkSize() {
		if (repo.settings.maxSize > 0 && currentRepoSize > repo.settings.maxSize)
			throw new InsufficientStorageException("Insufficient storage in repository");
		if (maxUserGroupSize > 0 && currentUserGroupSize > maxUserGroupSize)
			throw new InsufficientStorageException("Insufficient storage in user group");
	}

	private void writeBinaries(ModelStreamReader reader, Dataset dataset) throws IOException {
		File binDir = repo.getBinDir(dataset.type, dataset.refId, commit.id, false);
		int count = 0;
		int noOfFiles = reader.readNextInt();
		while (count++ < noOfFiles) {
			String path = reader.readNextPartAsString();
			File binFile = new File(binDir, path + ".gz");
			binFile.getParentFile().mkdirs();
			try (OutputStream out = new FileOutputStream(binFile)) {
				int size = reader.readNextPartToStream(out);
				out.close();
				currentRepoSize += size;
				if (maxUserGroupSize > 0) {
					currentUserGroupSize += size;
				}
			}
			checkSize();
		}
	}

	private void cleanup(Repository repo, List<Dataset> datasets, Commit commit) {
		for (Dataset dataset : datasets) {
			File file = repo.getDatasetFile(dataset.type, dataset.refId, commit.id, false);
			if (file.exists()) {
				file.delete();
			}
			file = file.getParentFile();
			while (file != null && file.exists() && !file.equals(repo.repoDir)
					&& (file.listFiles() == null || file.listFiles().length == 0)) {
				file.delete();
				file = file.getParentFile();
			}
			file = repo.getBinDir(dataset.type, dataset.refId, commit.id, false);
			if (file.exists()) {
				Directories.delete(file);
				while (!(file = file.getParentFile()).equals(repo.repoDir) && file.listFiles().length == 0) {
					file.delete();
				}
			}
		}
	}

	// When committing big amount of system processes (e.g. ecoinvent lci db)
	// the server ran into memory issues while indexing, when directly holding
	// input/output lists of all processes. To avoid incomplete commit indexing,
	// we first collect the (flat) dataset index entries and put them into the
	// search index, while collecting the input/output lists in a memory
	// efficient way. After sucessfully indexing the whole commit we update the
	// index entries with the collected input/output lists in chunks of 100
	private class InputOutputListAppending {

		private final Map<String, Integer> flowToCount = new HashMap<>();
		private final Map<Integer, String> countToFlow = new HashMap<>();
		private final Map<String, List<Integer>> processToInputs = new HashMap<>();
		private final Map<String, List<Integer>> processToOutputs = new HashMap<>();
		private int count = 0;

		@SuppressWarnings("unchecked")
		private void append(String process, List<Map<String, Object>> exchanges) {
			if (exchanges == null)
				return;
			for (Map<String, Object> e : exchanges) {
				Map<String, Object> f = (Map<String, Object>) e.get("flow");
				String flow = f.get("@id").toString();
				boolean input = e.get("input") != null && e.get("input").toString().toLowerCase().equals("true");
				append(process, flow, input);
			}
		}

		private void append(String process, String flow, boolean input) {
			Integer c = flowToCount.get(flow);
			if (c == null) {
				flowToCount.put(flow, c = ++count);
				countToFlow.put(c, flow);
			}
			append(process, c, input ? processToInputs : processToOutputs);
		}

		private void append(String process, int count, Map<String, List<Integer>> map) {
			List<Integer> list = map.get(process);
			if (list == null) {
				map.put(process, list = new ArrayList<>());
			}
			list.add(count);
		}
		
		private void index() {
			BulkUpdate bulkUpdate = searchService.new BulkUpdate();			
			Set<String> processes = new HashSet<>();
			processes.addAll(processToInputs.keySet());
			processes.addAll(processToOutputs.keySet());
			int count = 0;
			for (String process : processes) {
				String id = IndexEntry.toIndexId(repo.toId(), ModelType.PROCESS, process, commit.id);
				bulkUpdate.update(id, (data) -> {
					data.put("inputs", popFlowList(process, processToInputs));
					data.put("outputs", popFlowList(process, processToOutputs));
				});
				if (++count == 100) {
					bulkUpdate.commit();
					count = 0;
				}
			}
			bulkUpdate.commit();
		}
		
		private List<String> popFlowList(String process, Map<String, List<Integer>> map) {
			List<Integer> list = map.remove(process);
			if (list == null || list.isEmpty())
				return null;
			List<String> flowList = new ArrayList<>();
			for (Integer count : list) {
				flowList.add(countToFlow.get(count));
			}
			return flowList;
		}

	}

	
}
