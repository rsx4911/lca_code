package com.greendelta.cloud.service.repository;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.cloud.index.DatasetIndexer;
import com.greendelta.cloud.model.data.CommitData;
import com.greendelta.cloud.model.data.DatasetIdentifier;
import com.greendelta.cloud.util.Directories;
import com.greendelta.cloud.util.Strings;

public class DatasetService {

	private final static Logger log = LoggerFactory.getLogger(RepositoryPaths.class);
	private final static Charset charset = Charset.forName("utf-8");

	private RepositoryService repositoryService;

	@Inject
	public DatasetService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	void put(String repositoryId, String commitId, CommitData data) {
		DatasetIdentifier identifier = data.getIdentifier();
		RepositoryPaths repository = repositoryService.getForId(repositoryId);
		File datasetDirectory = repository.getDatasetDirectory(identifier.getType(), identifier.getRefId());
		if (!datasetDirectory.exists())
			datasetDirectory.mkdir();
		File datasetFile = repository.getDatasetFile(identifier.getType(), identifier.getRefId(), commitId);
		put(data, datasetFile, getIndexer(repositoryId));
	}

	public DatasetIndexer getIndexer(String repositoryId) {
		File indexDirectory = repositoryService.getForId(repositoryId).getCommitIndexDirectory();
		return new DatasetIndexer(indexDirectory);
	}

	public void streamIndex(String repositoryId, OutputStream stream) throws IOException {
		File indexDirectory = repositoryService.getForId(repositoryId).getCommitIndexDirectory();
		Directories.streamZipped(indexDirectory, stream);
	}

	public String get(String repositoryId, ModelType type, String refId, String commitId) {
		File file = repositoryService.getForId(repositoryId).getDatasetFile(type, refId, commitId);
		return read(file);
	}

	private String read(File file) {
		if (file == null)
			return null;
		if (!file.exists())
			return null;
		if (file.length() == 0)
			return null;
		try {
			byte[] jsonData = Files.readAllBytes(file.toPath());
			return new String(jsonData, charset);
		} catch (IOException e) {
			log.error(Strings.concat("Error reading json data from file ", file.getAbsolutePath()), e);
			return null;
		}
	}

	private void put(CommitData data, File file, DatasetIndexer indexer) {
		try {
			DatasetIdentifier identifier = data.getIdentifier();
			if (data.getJson() == null) {
				file.createNewFile();
				indexer.delete(identifier.getRefId());
			} else {
				Files.write(file.toPath(), data.getJson().getBytes(charset));
				indexer.index(identifier);
			}
		} catch (IOException e) {
			log.error(Strings.concat("Error writing json data to file ", file.getAbsolutePath()), e);
		}
	}

}
