package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class SearchService {

	private static final Logger log = LogManager.getLogger(SearchService.class);
	private final SettingsService settingsService;
	private final QueryService queryService;
	private final DsEntryParser parser = new DsEntryParser();

	@Autowired
	public SearchService(SettingsService settingsService, QueryService queryService) {
		this.settingsService = settingsService;
		this.queryService = queryService;
	}

	public SearchResult<DsEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	public void index(Repository repo) {
		repo.commits().find().all().forEach(commit -> index(repo, commit));
	}

	public void index(Repository repo, Commit commit) {
		var manager = new DsEntryManager(repo, commit);
		var diffs = repo.diffs().find().withPrevious(commit.id).all();
		Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
				.forEach(diff -> index(repo, manager, diff.right));
		Diff.filter(diffs, DiffType.DELETED)
				.forEach(diff -> remove(manager, diff.left));
	}

	private void index(Repository repo, DsEntryManager manager, Reference ref) {
		var entry = find(ref);
		entry = manager.createOrUpdate(entry, ref);
		getClient().index(entry.toIndexId(), Maps.of(entry));
	}

	private void remove(DsEntryManager manager, Reference ref) {
		var entry = find(ref);
		if (entry == null)
			return;
		manager.remove(entry, ref);
		if (entry.versions.isEmpty()) {
			getClient().remove(entry.toIndexId());
		} else {
			getClient().update(entry.toIndexId(), Maps.of(entry));
		}
	}

	private DsEntry find(Reference ref) {
		var entry = getClient().get(DsEntry.toIndexId(ref.type, ref.refId));
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
		var commits = repo.commits().find().all();
		Collections.reverse(commits);
		commits.forEach(commit -> {
			var manager = new DsEntryManager(repo, commit);
			var diffs = repo.diffs().find().withPrevious(commit.id).all();
			Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
					.forEach(diff -> remove(manager, diff.right));
		});
	}

	public void clearIndex() {
		getClient().delete();
		createIndex();
	}

	private void createIndex() {
		try {
			getClient().create(Map.of(
					"config", readJson("os-config.json"),
					"mapping", readJson("os-mapping.json")));
		} catch (IOException e) {
			log.error("Error creating search index", e);
		}
	}

	private String readJson(String resource) throws IOException {
		var stream = getClass().getResourceAsStream(resource);
		if (stream == null)
			return "{}";
		return new String(stream.readAllBytes(), "utf-8");
	}

	private SearchClient getClient() {
		return settingsService.searchConfig.getSearchClient();
	}

}
