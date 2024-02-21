package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.diff.DiffEntry.Side;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Reference;
import org.openlca.git.util.TypedRefId;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class SearchService {

	private static final Logger log = LogManager.getLogger(SearchService.class);
	private final SettingsService settings;
	private final QueryService queryService;
	private final DsEntryParser parser = new DsEntryParser();

	public SearchService(SettingsService settings, QueryService queryService) {
		this.settings = settings;
		this.queryService = queryService;
	}

	public SearchResult<DsEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	void index(Repository repo) {
		var head = repo.commits.head();
		if (head == null)
			return;
		String previousCommitId = repo.settings.get(RepositorySetting.SEARCH_COMMIT_ID);
		var previous = previousCommitId != null ? repo.commits.get(previousCommitId) : null;
		if (previous != null && previous.equals(head))
			return;
		var manager = new DsEntryManager(repo, head);
		var diffs = repo.diffs.find().commit(previous).excludeCategories().with(head);
		if (diffs.isEmpty())
			return;
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, 1000);
		Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED, DiffType.MOVED)
				.forEach(diff -> index(buffer, repo, manager, diff.toReference(Side.NEW)));
		Diff.filter(diffs, DiffType.DELETED)
				.forEach(diff -> remove(buffer, manager, diff.toReference(Side.OLD)));
		buffer.flush();
	}

	private void index(EntryBuffer buffer, Repository repo, DsEntryManager manager, Reference ref) {
		var entry = find(ref);
		boolean insert = entry == null;
		entry = manager.createOrUpdate(entry, ref);
		if (insert) {
			buffer.putInsert(getIndexId(ref), entry);
		} else {
			buffer.putUpdate(getIndexId(ref), entry);
		}
	}

	private void remove(EntryBuffer buffer, DsEntryManager manager, Reference ref) {
		var entry = find(ref);
		if (entry == null)
			return;
		manager.remove(entry, ref);
		if (entry.versions.isEmpty()) {
			buffer.putRemove(getIndexId(entry));
		} else {
			buffer.putUpdate(getIndexId(ref), entry);
		}
	}

	private DsEntry find(Reference ref) {
		var map = getClient().get(getIndexId(ref));
		return parser.parse(map);
	}

	void updateTags(Repository repo) {
		var head = repo.commits.head();
		if (head == null)
			return;
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, 1000);
		update(buffer, repo,
				e -> e.versions.forEach(
						v -> v.repos.forEach(
								r -> r.tags = repo.settings.get(RepositorySetting.TAGS))));
		buffer.flush();
	}

	void move(RepositoryPath oldPath, Repository newRepo) {
		var head = newRepo.commits.head();
		if (head == null)
			return;
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, 1000);
		update(buffer, newRepo,
				e -> e.versions.forEach(
						v -> v.repos.forEach(
								r -> {
									r.group = newRepo.group;
									r.path = newRepo.path();
								})));
		buffer.flush();
	}

	private void update(EntryBuffer buffer, Repository repo, Consumer<DsEntry> update) {
		var head = repo.commits.head();
		if (head == null)
			return;
		var manager = new DsEntryManager(repo, head);
		repo.references.find().iterate(ref -> update(buffer, manager, ref, update));
	}

	private void update(EntryBuffer buffer, DsEntryManager manager, Reference ref, Consumer<DsEntry> update) {
		var entry = find(ref);
		if (entry == null)
			return;
		update.accept(entry);
		buffer.putUpdate(getIndexId(ref), entry);
	}

	void remove(Repository repo) {
		String previousCommitId = repo.settings.get(RepositorySetting.SEARCH_COMMIT_ID);
		if (previousCommitId == null)
			return;
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, 1000);
		var manager = new DsEntryManager(repo, null);
		repo.references.find().commit(previousCommitId).iterate(ref -> remove(buffer, manager, ref));
		buffer.flush();
	}

	void clearIndex() {
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

	SearchClient getClient() {
		return settings.searchConfig.getSearchClient();
	}

	private String getIndexId(TypedRefId ref) {
		return ref.type.name() + "/" + ref.refId;
	}

}
