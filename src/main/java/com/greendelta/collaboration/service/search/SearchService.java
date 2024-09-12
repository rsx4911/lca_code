package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
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
	private static final int BUFFER_SIZE = 1000;
	private final SettingsService settings;
	private final QueryService queryService;

	public SearchService(SettingsService settings, QueryService queryService) {
		this.settings = settings;
		this.queryService = queryService;
	}

	public SearchResult<DsEntry> search(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		return queryService.query(query, page, pageSize, filters);
	}

	Index on(SearchIndex... indices) {
		var clients = Arrays.asList(indices).stream().map(settings.searchConfig::getSearchClient).toList();
		return new Index(clients.toArray(new SearchClient[clients.size()]));
	}

	static class Index {

		private final List<SearchClient> clients;
		private final DsEntryParser parser = new DsEntryParser();

		Index(SearchClient... clients) {
			this.clients = Arrays.asList(clients).stream().filter(Objects::nonNull).toList();
		}

		void index(String path, OlcaRepository repo, List<String> tags, Commit previousCommit, Commit commit) {
			if (commit == null)
				return;
			var diffs = repo.diffs.find()
					.unsorted()
					.commit(previousCommit)
					.excludeCategories()
					.with(commit);
			if (diffs.isEmpty())
				return;
			var manager = new DsEntryManager(path, repo, commit);
			var buffer = new EntryBuffer(clients, BUFFER_SIZE);
			Diff.filter(diffs, DiffType.ADDED, DiffType.MODIFIED, DiffType.MOVED)
					.forEach(diff -> index(buffer, repo, tags, manager, diff.newRef));
			Diff.filter(diffs, DiffType.DELETED)
					.forEach(diff -> remove(clients.get(0), buffer, manager, diff.oldRef));
			buffer.flush();
		}

		private void index(EntryBuffer buffer, OlcaRepository repo, List<String> tags, DsEntryManager manager,
				Reference ref) {
			if (ref == null)
				return;
			var entry = find(clients.get(0), ref);
			boolean insert = entry == null;
			entry = manager.createOrUpdate(entry, ref, tags);
			if (insert) {
				buffer.putInsert(getIndexId(ref), entry);
			} else {
				buffer.putUpdate(getIndexId(ref), entry);
			}
		}

		private void remove(SearchClient client, EntryBuffer buffer, DsEntryManager manager, Reference ref) {
			if (ref == null)
				return;
			var entry = find(client, ref);
			if (entry == null)
				return;
			manager.remove(entry, ref);
			if (entry.versions.isEmpty()) {
				buffer.putRemove(getIndexId(entry));
			} else {
				buffer.putUpdate(getIndexId(ref), entry);
			}
		}

		private DsEntry find(SearchClient client, Reference ref) {
			if (ref == null)
				return null;
			var map = client.get(getIndexId(ref));
			return parser.parse(map);
		}

		private String getIndexId(TypedRefId ref) {
			return ref.type.name() + "/" + ref.refId;
		}

		void updateTags(Repository repo, Commit commit, List<String> tags) {
			if (commit == null)
				return;
			var buffer = new EntryBuffer(clients, BUFFER_SIZE);
			update(buffer, repo, commit, e -> {
				e.versions.forEach(v -> {
					v.repos.stream()
							.filter(r -> r.path.equals(repo.path()))
							.forEach(r -> {
								r.tags = tags;
							});
				});
			});
			buffer.flush();
		}

		void move(RepositoryPath oldPath, Repository newRepo, Commit commit) {
			if (commit == null)
				return;
			var buffer = new EntryBuffer(clients, BUFFER_SIZE);
			update(buffer, newRepo, commit, e -> {
				e.versions.forEach(v -> {
					v.repos.stream()
							.filter(r -> r.path.equals(oldPath.toString()))
							.forEach(r -> {
								r.group = newRepo.group;
								r.path = newRepo.path();
							});
				});
			});
			buffer.flush();
		}

		private void update(EntryBuffer buffer, Repository repo, Commit commit, Consumer<DsEntry> update) {
			if (commit == null)
				return;
			var manager = new DsEntryManager(repo.path(), repo, commit);
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

		private DsEntry find(Reference ref) {
			if (ref == null)
				return null;
			var map = clients.get(0).get(getIndexId(ref));
			return parser.parse(map);
		}

		void remove(Repository repo, Commit latest) {
			if (latest == null)
				return;
			var buffer = new EntryBuffer(clients, BUFFER_SIZE);
			var manager = new DsEntryManager(repo.path(), repo, null);
			repo.references.find().commit(latest.id).iterate(ref -> remove(clients.get(0), buffer, manager, ref));
			buffer.flush();
		}

		void clear() {
			for (var client : clients) {
				client.delete();
				createIndex(client);
			}
		}

		private void createIndex(SearchClient client) {
			try {
				client.create(Map.of(
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

	}

}
