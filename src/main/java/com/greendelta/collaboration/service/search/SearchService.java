package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.diff.DiffEntry.Side;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Reference;
import org.openlca.git.util.Diffs;
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
	private final SettingsService settings;
	private final QueryService queryService;
	private final DsEntryParser parser = new DsEntryParser();
	private ReindexingStatus reindexStatus;

	@Autowired
	public SearchService(SettingsService settings, QueryService queryService) {
		this.settings = settings;
		this.queryService = queryService;
	}

	public SearchResult<DsEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	void index(Repository repo) {
		index(repo, repo.commits().find().all());
	}

	void index(Repository repo, Commit commit) {
		index(repo, Collections.singletonList(commit));
	}

	private void index(Repository repo, List<Commit> commits) {
		commits.stream().forEach(commit -> {
			var manager = new DsEntryManager(repo, commit);
			var diffs = Diffs.of(repo.gitRepo(), commit).withPreviousCommit();
			Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
					.forEach(diff -> index(repo, manager, diff.toReference(Side.NEW)));
			Diff.filter(diffs, DiffType.DELETED)
					.forEach(diff -> remove(manager, diff.toReference(Side.OLD)));
		});
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

	void update(Repository repo) {
		update(repo, repo);
	}

	void update(Repository oldRepo, Repository newRepo) {
		remove(oldRepo);
		index(newRepo);
	}

	public void remove(Repository repo) {
		var commits = repo.commits().find().all();
		Collections.reverse(commits);
		commits.forEach(commit -> {
			var manager = new DsEntryManager(repo, commit);
			var diffs = Diffs.of(repo.gitRepo(), commit).withPreviousCommit();
			Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED)
					.forEach(diff -> remove(manager, diff.toReference(Side.NEW)));
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
		return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
	}

	private SearchClient getClient() {
		return settings.searchConfig.getSearchClient();
	}

	public boolean isReindexing() {
		return reindexStatus != null;
	}

	public ReindexingStatus startReindexing(int total) {
		if (reindexStatus != null)
			throw new IllegalStateException("Already reindexing");
		reindexStatus = new ReindexingStatus(total);
		return reindexStatus;
	}

	public ReindexingStatus getReindexingStatus() {
		return reindexStatus;
	}

	public void endReindexing() {
		reindexStatus = null;
	}

	public class ReindexingStatus {

		public final Date start;
		public final int total;
		public int worked;

		private ReindexingStatus(int total) {
			this.total = total;
			this.start = Calendar.getInstance().getTime();
		}

	}
}
