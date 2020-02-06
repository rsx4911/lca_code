package com.greendelta.collaboration.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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

class DatasetWriter {

	final SearchService searchService;
	private final LibraryService libraryService;
	final Repository repo;
	final Commit commit;
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
			InputOutputList ioList = new InputOutputList(searchService, repo, commit);
			while (reader.hasMore()) {
				Dataset dataset = reader.readNextPartAsDataset();
				if (!checkLibraryRestrictions(dataset))
					throw new AuthorizationException("User is not allowed to commit changes to "
							+ dataset.type.name() + " " + dataset.refId);
				datasets.add(dataset);
				File file = repo.getDatasetFile(dataset.type, dataset.refId, commit.id, true);
				if (isDeletedDataset(reader, dataset, file)) {
					indexEntries.add(indexEntryCreator.create(dataset));
					continue;
				}
				createIndexEntry(dataset, ioList);
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

	private boolean isDeletedDataset(ModelStreamReader reader, Dataset dataset, File file) throws IOException {
		int size = 0;
		try (OutputStream out = new FileOutputStream(file)) {
			size = reader.readNextPartToStream(out);
			out.close();
		}
		if (size == 0)
			return true;
		currentRepoSize += size;
		if (maxUserGroupSize > 0) {
			currentUserGroupSize += size;
		}
		checkSize();
		return false;
	}

	@SuppressWarnings("unchecked")
	private void createIndexEntry(Dataset dataset, InputOutputList ioList) {
		IndexAction lastAction = searchService.getMostRecentAction(repo, dataset.type, dataset.refId);
		if (!(dataset.type == ModelType.PROCESS || dataset.type == ModelType.FLOW)) {
			indexEntries.add(indexEntryCreator.create(dataset, lastAction));
			return;
		}
		Map<String, Object> data = repo.readData(dataset.type, dataset.refId, commit.id);
		if (dataset.type == ModelType.PROCESS) {
			ioList.append(dataset.refId, (List<Map<String, Object>>) data.get("exchanges"));
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

}
