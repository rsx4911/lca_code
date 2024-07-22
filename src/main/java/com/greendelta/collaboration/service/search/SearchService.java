package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Reference;
import org.openlca.git.util.TypedRefId;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.SearchIndex;
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

	Index on(SearchIndex name) {
		return new Index(name);
	}

	class Index {

		private final SearchIndex index;

		private Index(SearchIndex index) {
			this.index = index;
		}

		void index(Repository repo, List<String> tags, Commit previousCommit, Commit commit) {
			if (commit == null)
				return;
			var client = getClient();
			if (client == null)
				return;
			var diffs = repo.diffs.find().unsorted().commit(previousCommit).excludeCategories().with(commit);
			if (diffs.isEmpty())
				return;
			var manager = new DsEntryManager(repo, commit);
			var buffer = new EntryBuffer(client, 1000);
			Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED, DiffType.MOVED)
					.forEach(diff -> index(buffer, repo, tags, manager, diff.newRef));
			Diff.filter(diffs, DiffType.DELETED)
					.forEach(diff -> remove(buffer, manager, diff.oldRef));
			buffer.flush();
		}

		private void index(EntryBuffer buffer, Repository repo, List<String> tags, DsEntryManager manager,
				Reference ref) {
			if (ref == null)
				return;
			var entry = find(ref);
			boolean insert = entry == null;
			entry = manager.createOrUpdate(entry, ref, tags);
			if (insert) {
				buffer.putInsert(getIndexId(ref), entry);
			} else {
				buffer.putUpdate(getIndexId(ref), entry);
			}
		}

		private void remove(EntryBuffer buffer, DsEntryManager manager, Reference ref) {
			if (ref == null)
				return;
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
			if (ref == null)
				return null;
			var map = getClient().get(getIndexId(ref));
			return parser.parse(map);
		}

		void updateTags(Repository repo, Commit commit, List<String> tags) {
			if (commit == null)
				return;
			var client = getClient();
			if (client == null)
				return;
			var buffer = new EntryBuffer(client, 1000);
			update(buffer, repo, commit,
					e -> e.versions.forEach(
							v -> v.repos.forEach(
									r -> r.tags = tags)));
			buffer.flush();
		}

		void move(RepositoryPath oldPath, Repository newRepo, Commit commit) {
			if (commit == null)
				return;
			var client = getClient();
			if (client == null)
				return;
			var buffer = new EntryBuffer(client, 1000);
			update(buffer, newRepo, commit,
					e -> e.versions.forEach(
							v -> v.repos.forEach(
									r -> {
										r.group = newRepo.group;
										r.path = newRepo.path();
									})));
			buffer.flush();
		}

		private void update(EntryBuffer buffer, Repository repo, Commit commit, Consumer<DsEntry> update) {
			if (commit == null)
				return;
			var manager = new DsEntryManager(repo, commit);
			repo.references.find().commit(commit.id).iterate(ref -> update(buffer, manager, ref, update));
		}

		private void update(EntryBuffer buffer, DsEntryManager manager, Reference ref, Consumer<DsEntry> update) {
			if (ref == null)
				return;
			var entry = find(ref);
			if (entry == null)
				return;
			update.accept(entry);
			buffer.putUpdate(getIndexId(ref), entry);
		}

		void remove(Repository repo, Commit latest) {
			if (latest == null)
				return;
			var client = getClient();
			if (client == null)
				return;
			var buffer = new EntryBuffer(client, 1000);
			var manager = new DsEntryManager(repo, null);
			repo.references.find().commit(latest.id).iterate(ref -> remove(buffer, manager, ref));
			buffer.flush();
		}

		void clear() {
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

		private String getIndexId(TypedRefId ref) {
			return ref.type.name() + "/" + ref.refId;
		}

		SearchClient getClient() {
			return settings.searchConfig.getSearchClient(index);
		}

	}

}
