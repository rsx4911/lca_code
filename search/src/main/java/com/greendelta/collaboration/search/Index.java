package com.greendelta.collaboration.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.TypedRefId;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;

import com.greendelta.search.wrapper.SearchClient;

public class Index {

	private static final Logger log = LogManager.getLogger(Index.class);
	private static final int BUFFER_SIZE = 1000;
	private final List<SearchClient> clients;
	private final DsEntryParser parser = new DsEntryParser();

	public Index(SearchClient... clients) {
		this.clients = Arrays.asList(clients).stream().filter(Objects::nonNull).toList();
	}

	public void index(String path, OlcaRepository repo, List<String> tags, Commit previousCommit, Commit commit) {
		if (commit == null)
			return;
		var diffs = repo.diffs.find()
				.unsorted()
				.commit(previousCommit)
				.excludeCategories()
				.excludeLibraries()
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

	public void updateTags(String path, OlcaRepository repo, Commit commit, List<String> tags) {
		if (commit == null)
			return;
		var buffer = new EntryBuffer(clients, BUFFER_SIZE);
		update(buffer, path, repo, commit, e -> {
			e.versions.forEach(v -> {
				v.repos.stream()
						.filter(r -> r.path.equals(path))
						.forEach(r -> {
							r.tags = tags;
						});
			});
		});
		buffer.flush();
	}

	public void move(String oldPath, String newPath, OlcaRepository newRepo, Commit commit) {
		if (commit == null)
			return;
		var id = newPath.split("/");
		var buffer = new EntryBuffer(clients, BUFFER_SIZE);
		update(buffer, newPath, newRepo, commit, e -> {
			e.versions.forEach(v -> {
				v.repos.stream()
						.filter(r -> r.path.equals(oldPath.toString()))
						.forEach(r -> {
							r.group = id[0];
							r.path = id[1];
						});
			});
		});
		buffer.flush();
	}

	private void update(EntryBuffer buffer, String path, OlcaRepository repo, Commit commit, Consumer<DsEntry> update) {
		if (commit == null)
			return;
		var manager = new DsEntryManager(path, repo, commit);
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

	public void remove(String path, OlcaRepository repo, Commit latest) {
		if (latest == null)
			return;
		var buffer = new EntryBuffer(clients, BUFFER_SIZE);
		var manager = new DsEntryManager(path, repo, null);
		repo.references.find().commit(latest.id).iterate(ref -> remove(clients.get(0), buffer, manager, ref));
		buffer.flush();
	}

	public void clear() {
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