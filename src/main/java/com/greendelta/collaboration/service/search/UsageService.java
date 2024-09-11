package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.DiffType;
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

	public SearchResult<Map<String, Object>> query(Repository repo, String refId, String field, int page, int pageSize,
			String filter) {
		var query = new SearchQueryBuilder();
		query.page(page);
		query.pageSize(pageSize);
		query.filter("path", SearchFilterValue.term(repo.path()));
		query.fields("type", "refId", "name", "processType", "flowType");
		if (Strings.nullOrEmpty(field)) {
			field = "others";
		}
		query.filter(field, SearchFilterValue.term(refId));
		if (!Strings.nullOrEmpty(filter)) {
			query.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		return getClient().search(query.build());
	}

	void index(Repository repo, Commit previousCommit, Commit currentCommit) {
		if (currentCommit == null)
			return;
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, BUFFER_SIZE);
		var commits = repo.commits.find()
				.after(previousCommit != null ? previousCommit.id : null)
				.until(currentCommit.id)
				.all();
		for (var commit : commits) {
			new UsageIndexer(buffer, repo, commit).index();
		}
		buffer.flush();
	}

	public class UsageIndexer {

		private final Repository repo;
		private final Commit commit;
		private final EntryBuffer buffer;

		private UsageIndexer(EntryBuffer buffer, Repository repo, Commit commit) {
			this.repo = repo;
			this.commit = commit;
			this.buffer = buffer;
		}

		private void index() {
			var diffs = repo.diffs.find().commit(commit)
					.excludeCategories()
					.withPreviousCommit();
			for (var diff : diffs) {
				if (diff.diffType == DiffType.DELETED)
					continue;
				var ref = diff.newRef;
				var dataset = repo.datasets.get(ref);
				var json = Maps.of(dataset);
				var location = Maps.getString(json, "location.name");
				var name = Maps.getString(json, "name");
				var processType = Enums.getValue(Maps.getString(json, "processType"), ProcessType.class);
				var flowType = ref.type == ModelType.PROCESS
						? getQuantitativeReferenceFlowType(json)
						: Enums.getValue(Maps.getString(json, "flowType"), FlowType.class);
				var n = !Strings.nullOrEmpty(location)
						? name += " - " + location
						: name;
				var entry = new Entry(repo.path(), ref.type, ref.refId, processType, flowType, n);
				collectReferences(entry, null, json);
				buffer.putInsert(entry.getId(), entry);
			}
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

		private void collectReferences(Entry entry, String parentField, Map<String, Object> value) {
			if (value == null)
				return;
			for (var nextField : value.keySet()) {
				var field = parentField != null
						? parentField + "." + nextField
						: nextField;
				if (Maps.isArray(value, nextField)) {
					for (var arrayElement : Maps.getArray(value, nextField)) {
						if (!Maps.is(arrayElement))
							continue;
						var map = Maps.of(arrayElement);
						if ("exchanges".equals(field)) {
							if (Maps.getBoolean(map, "isInput")) {
								collectReference(entry, "inputs", map);
							} else {
								collectReference(entry, "outputs", map);
							}
						} else {
							collectReference(entry, field, map);
						}
					}
					continue;
				}
				if (!Maps.isObject(value, nextField))
					continue;
				collectReference(entry, field, Maps.getObject(value, nextField));
			}
		}

		private void collectReference(Entry entry, String field, Map<String, Object> object) {
			if (!(object.containsKey("@type") && object.containsKey("@id"))) {
				collectReferences(entry, field, object);
				return;
			}
			var type = getModelType(Maps.getString(object, "@type"));
			if (type == null)
				return;
			var refId = Maps.getString(object, "@id");
			if (field.equals("inputs.flow")) {
				entry.inputs.add(refId);
			} else if (field.equals("outputs.flow")) {
				entry.outputs.add(refId);
			} else {
				entry.others.add(refId);
			}
		}

		private ModelType getModelType(String simpleClassName) {
			for (var type : ModelType.values())
				if (type.getModelClass() != null && type.getModelClass().getSimpleName().equals(simpleClassName))
					return type;
			return null;
		}

	}

	void move(RepositoryPath path, Repository newRepo) {
		var client = getClient();
		if (client == null)
			return;
		var builder = new SearchQueryBuilder()
				.filter("path", SearchFilterValue.term(path));
		var ids = client.searchIds(builder.build());
		client.update(ids, Maps.of("path", newRepo.path()));
	}

	void remove(Repository repo) {
		var client = getClient();
		if (client == null)
			return;
		var builder = new SearchQueryBuilder()
				.filter("path", SearchFilterValue.term(repo.path()));
		var ids = client.searchIds(builder.build());
		client.remove(ids);
	}

	void clearIndex() {
		getClient().delete();
		createIndex();
	}

	private SearchClient getClient() {
		return settings.searchConfig.getSearchClient(SearchIndex.USAGE);
	}

	public record Entry(String path, ModelType type, String refId, ProcessType processType, FlowType flowType,
			String name, Set<String> inputs, Set<String> outputs, Set<String> others) {

		private Entry(String path, ModelType type, String refId, ProcessType processType, FlowType flowType,
				String name) {
			this(path, type, refId, processType, flowType, name, new HashSet<>(), new HashSet<>(), new HashSet<>());
		}

		private String getId() {
			return Strings.join(Arrays.asList(path, type.name(), refId), '/');
		}

	}
}
