package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.DiffReference;
import org.openlca.cloud.api.git.DiffType;
import org.openlca.cloud.api.git.Reference;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.GsonTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchResult;

public class SearchService {

	private static final Logger log = LogManager.getLogger(SearchService.class);
	private final SettingsService settingsService;
	private final QueryService queryService;
	private final DsEntryParser parser = new DsEntryParser();
	private final Gson gson = new Gson();

	@Inject
	public SearchService(SettingsService settingsService, QueryService queryService) {
		this.settingsService = settingsService;
		this.queryService = queryService;
	}

	public SearchResult<DsEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	public void index(Repository repo) {
		repo.commits.find().all().forEach(commit -> index(repo, commit));
	}

	public void index(Repository repo, Commit commit) {
		DsEntryManager manager = new DsEntryManager(repo, commit);
		new Thread(() -> {
			List<DiffReference> diffs = repo.references.diff().withPrevious(commit.id).all();
			DiffReference.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
					.forEach(diff -> index(repo, manager, diff.right));
			DiffReference.filter(diffs, DiffType.DELETED)
					.forEach(diff -> remove(manager, diff.left));
		}).start();
	}

	private void index(Repository repo, DsEntryManager manager, Reference ref) {
		String json = repo.datasets.get(ref.objectId);
		Map<String, Object> data = gson.fromJson(json, GsonTypes.OBJECT_MAP);
		if (data.isEmpty())
			return;
		DsEntry entry = find(ref);
		entry = manager.createOrUpdate(entry, ref, data);
		getClient().index(entry.toIndexId(), ObjectMap.fromObject(entry));
	}

	private void remove(DsEntryManager manager, Reference ref) {
		DsEntry entry = find(ref);
		if (entry == null)
			return;
		manager.remove(entry, ref);
		if (entry.versions.isEmpty()) {
			getClient().remove(entry.toIndexId());
		} else {
			getClient().update(entry.toIndexId(), ObjectMap.fromObject(entry));
		}
	}

	private DsEntry find(Reference ref) {
		Map<String, Object> entry = getClient().get(DsEntry.toIndexId(ref.type, ref.refId));
		return parser.parse(entry);
	}

	public void update(Repository repo) {
		update(repo, repo);
	}

	public void update(Repository oldRepo, Repository newRepo) {
		// TODO more efficient
		remove(oldRepo);
		index(newRepo);
	}

	public void remove(Repository repo) {
		List<Commit> commits = repo.commits.find().all();
		Collections.reverse(commits);
		commits.forEach(commit -> {
			DsEntryManager manager = new DsEntryManager(repo, commit);
			List<DiffReference> diffs = repo.references.diff().withPrevious(commit.id).all();
			DiffReference.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
					.forEach(diff -> remove(manager, diff.right));
		});
	}

	public void clearIndex() {
		getClient().delete();
		createIndex();
	}

	private void createIndex() {
		try {
			Map<String, String> settings = new HashMap<>();
			settings.put("config", readJson("es-config.json"));
			settings.put("mapping", readJson("es-mapping.json"));
			getClient().create(settings);
		} catch (IOException e) {
			log.error("Error creating search index", e);
		}
	}

	private String readJson(String resource) throws IOException {
		URL url = getClass().getResource(resource);
		byte[] data = Resources.toByteArray(url);
		return new String(data, "utf-8");
	}

	private SearchClient getClient() {
		return settingsService.searchConfig.getSearchClient();
	}

}
