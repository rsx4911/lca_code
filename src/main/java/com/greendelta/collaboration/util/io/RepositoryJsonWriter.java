package com.greendelta.collaboration.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.openlca.cloud.model.data.Commit;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;
import org.openlca.util.BinUtils;

import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.search.SearchService.IndexIterator;

public class RepositoryJsonWriter implements Closeable {

	private final ZipStore zipStore;
	private final FetchService fetchService;
	private final Repository repo;

	public static void writeCurrent(FetchService fetchService, SearchService searchService,
			HistoryService historyService, Repository repo) throws IOException {
		Commit commit = historyService.getLastCommit(repo);
		if (commit == null)
			return;
		RepositoryJsonWriter writer = new RepositoryJsonWriter(fetchService, repo, repo.getCachedJsonFile());
		IndexIterator iterator = searchService.getMostRecentUntil(repo, commit.id);
		while (iterator.hasNext()) {
			writer.put(iterator.next());
		}
		writer.close();
	}

	public RepositoryJsonWriter(FetchService fetchService, Repository repo, File file) throws IOException {
		this.zipStore = ZipStore.open(file);
		this.fetchService = fetchService;
		this.repo = repo;
	}

	public String put(IndexEntry entry) throws IOException {
		String data = fetchService.getDataset(repo, entry.type, entry.refId, entry.commitId);
		if (data == null)
			return null;
		zipStore.put(ModelPath.get(entry.type, entry.refId), data.getBytes("utf-8"));
		File binDir = fetchService.getBinDir(repo, entry.type, entry.refId, entry.commitId);
		if (!binDir.exists())
			return data;
		for (File file : binDir.listFiles()) {
			String filename = file.getName();
			if (filename.endsWith(".gz")) {
				filename = filename.substring(0, filename.lastIndexOf(".gz"));
			}
			zipStore.putBin(entry.type, entry.refId, filename, BinUtils.gunzip(Files.readAllBytes(file.toPath())));
		}
		return data;
	}

	@Override
	public void close() throws IOException {
		zipStore.close();
	}

}
