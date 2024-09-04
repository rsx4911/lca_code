package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.util.TypedRefIdSet;
import org.openlca.jsonld.Enums;
import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class UsageService {

	private static final Logger log = LogManager.getLogger(UsageService.class);
	private static final int BUFFER_SIZE = 100;
	private final SettingsService settings;

	public UsageService(SettingsService settings) {
		this.settings = settings;
	}

	private void createIndex() {
		try {
			getClient().create(Map.of(
					"config", readJson("os-usage-config.json"),
					"mapping", readJson("os-usage-mapping.json")));
		} catch (IOException e) {
			log.error("Error creating search ref index", e);
		}
	}

	private String readJson(String resource) throws IOException {
		var stream = getClass().getResourceAsStream(resource);
		if (stream == null)
			return "{}";
		return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
	}

	public SearchResult<Map<String, Object>> query(Repository repo, ModelType type, String refId, String field,
			Commit commit, int page, int pageSize, String filter) {
		var query = new SearchQueryBuilder();
		query.filter("repositoryPath", SearchFilterValue.term(repo.path()));
		query.page(page);
		query.pageSize(pageSize);
		query.fields("type", "refId", "name", "processType", "flowType");
		query.filter("references.type", SearchFilterValue.term(type.name()));
		query.filter("references.refId", SearchFilterValue.term(refId));
		if (field != null) {
			query.filter("references.field", SearchFilterValue.term(field));
		}
		if (!Strings.nullOrEmpty(filter)) {
			query.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		query.filter("commitIds", SearchFilterValue.term(commit.id));
		return getClient().search(query.build());
	}

	void index(Repository repo, Commit previousCommit, Commit currentCommit) {
		if (currentCommit == null)
			return;
		var commits = findCommits(repo, previousCommit, currentCommit);
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, BUFFER_SIZE);
		for (var commit : commits) {
			var diffs = repo.diffs.find().commit(commit)
					.excludeCategories()
					.withPreviousCommit();
			var skip = new TypedRefIdSet();
			for (var diff : diffs) {
				skip.add(diff);
				if (diff.diffType == DiffType.DELETED)
					continue;
				var entry = createEntry(repo, diff);
				buffer.putInsert(getIndexId(repo.path(), diff.type.name(), diff.refId, diff.newRef.commitId), entry);
			}
			buffer.flush();
			updatePrevious(buffer, repo, skip, previousCommit, commit);
			previousCommit = commit;
		}
	}

	private List<Commit> findCommits(Repository repo, Commit previousCommit, Commit currentCommit) {
		if (previousCommit == null)
			return repo.commits.find().until(currentCommit.id).all();
		return repo.commits.find().after(previousCommit.id).until(currentCommit.id).all();
	}

	private Entry createEntry(Repository repo, Diff diff) {
		var dataset = repo.datasets.get(diff.newRef);
		var json = Maps.of(dataset);
		var name = Maps.getString(json, "name");
		var location = Maps.getString(json, "location.name");
		if (!Strings.nullOrEmpty(location)) {
			name += " - " + location;
		}
		var processType = Enums.getValue(Maps.getString(json, "processType"), ProcessType.class);
		var flowType = Enums.getValue(Maps.getString(json, "flowType"), FlowType.class);
		if (diff.type == ModelType.PROCESS) {
			flowType = getQuantitativeReferenceFlowType(json);
		}
		var refs = new ArrayList<>(collectReferences(json));
		return new Entry(repo.path(), diff.type, diff.refId, diff.newRef.commitId, processType, flowType, name, refs);
	}

	private FlowType getQuantitativeReferenceFlowType(Map<String, Object> json) {
		var exchanges = Maps.getArray(json, "exchanges");
		if (exchanges == null)
			return null;
		for (var e : exchanges) {
			if (Maps.is(e))
				continue;
			var exchange = Maps.of(e);
			if (!Maps.getBoolean(exchange, "isQuantitativeReference"))
				continue;
			var flow = Maps.getObject(exchange, "flow");
			if (flow == null)
				continue;
			return Enums.getValue(Maps.getString(flow, "flowType"), FlowType.class);
		}
		return null;
	}

	private void updatePrevious(EntryBuffer buffer, Repository repo, TypedRefIdSet skip, Commit previousCommit,
			Commit commit) {
		if (previousCommit == null)
			return;
		var previous = getForCommit(repo, previousCommit);
		while (previous.hasNext()) {
			var data = previous.next();
			var type = Maps.getString(data, "type");
			var refId = Maps.getString(data, "refId");
			if (skip.contains(ModelType.valueOf(type), refId))
				continue;
			var commitId = Maps.getString(data, "commitId");
			var commitIds = new ArrayList<>(Arrays.asList(Maps.getStringArray(data, "commitIds")));
			commitIds.add(commit.id);
			buffer.putUpdate(getIndexId(repo.path(), type, refId, commitId), Maps.of("commitIds", commitIds));
		}
		buffer.flush();
	}

	private IndexIterator getForCommit(Repository repo, Commit commit) {
		var query = new SearchQueryBuilder()
				.filter("repositoryPath", SearchFilterValue.term(repo.path()));
		if (commit != null) {
			query.filter("commitIds", SearchFilterValue.term(commit.id));
		}
		query.fields("type", "refId", "commitId", "commitIds");
		return new IndexIterator(getClient(), query, BUFFER_SIZE);
	}

	private Set<Ref> collectReferences(Map<String, Object> object) {
		if (object == null)
			return new HashSet<>();
		var references = new HashSet<Ref>();
		for (var field : object.keySet()) {
			if (Maps.isArray(object, field)) {
				for (var arrayElement : Maps.getArray(object, field)) {
					if (!Maps.is(arrayElement))
						continue;
					references.addAll(collectReference(field, Maps.of(arrayElement)));
				}
				continue;
			}
			if (!Maps.isObject(object, field))
				continue;
			references.addAll(collectReference(field, Maps.getObject(object, field)));
		}
		return references;
	}

	private Set<Ref> collectReference(String field, Map<String, Object> object) {
		if (!(object.containsKey("@type") && object.containsKey("@id")))
			return collectReferences(object);
		var type = getType(Maps.getString(object, "@type"));
		if (type == null)
			return new HashSet<>();
		var refId = Maps.getString(object, "@id");
		if (refId == null)
			return new HashSet<>();
		if (field.equals("exchanges")) {
			var isInput = Maps.getBoolean(object, "isInput");
			field = isInput ? "inputs" : "outputs";
		}
		return Collections.singleton(new Ref(field, type, refId));
	}

	private ModelType getType(String name) {
		for (var type : ModelType.values()) {
			if (!type.getModelClass().getSimpleName().equals(name))
				continue;
			return type;
		}
		return null;
	}

	void move(RepositoryPath path, Repository newRepo) {
		var ids = getIds(path.toString());
		Map<String, Object> update = new HashMap<>();
		update.put("repositoryPath", newRepo.path());
		getClient().update(ids, update);
	}

	void remove(Repository repo) {
		var ids = getIds(repo.path());
		if (ids.isEmpty())
			return;
		getClient().remove(ids);
	}

	private Set<String> getIds(String path) {
		var query = new SearchQueryBuilder()
				.filter("repositoryPath", SearchFilterValue.term(path));
		return getClient().searchIds(query.build());
	}

	void clearIndex() {
		getClient().delete();
		createIndex();
	}

	private String getIndexId(String repositoryPath, String type, String refId, String commitId) {
		return repositoryPath + "/" + type + "/" + refId + "/" + commitId;
	}

	private SearchClient getClient() {
		return settings.searchConfig.getSearchClient(SearchIndex.USAGE);
	}

	public class Entry {

		public final String repositoryPath;
		public final ModelType type;
		public final String refId;
		public final String commitId;
		public final ProcessType processType;
		public final FlowType flowType;
		public final String name;
		public final List<Ref> references;
		public final List<String> commitIds;

		private Entry(String repositoryPath, ModelType type, String refId, String commitId, ProcessType processType,
				FlowType flowType, String name, List<Ref> references) {
			this.repositoryPath = repositoryPath;
			this.type = type;
			this.refId = refId;
			this.commitId = commitId;
			this.processType = processType;
			this.flowType = flowType;
			this.name = name;
			this.references = references;
			this.commitIds = Arrays.asList(commitId);
		}

	}

	public class Ref {

		public final String field;
		public final ModelType type;
		public final String refId;

		private Ref(String field, ModelType type, String refId) {
			this.field = field;
			this.type = type;
			this.refId = refId;
		}

	}

}
