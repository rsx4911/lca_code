package com.greendelta.collaboration.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.DiffType;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.jsonld.Enums;
import org.openlca.util.Strings;

import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;

public class UsageIndex {
	
	private static final Logger log = LogManager.getLogger(UsageIndex.class);
	private static final int BUFFER_SIZE = 100;

	private final List<SearchClient> clients;

	public UsageIndex(SearchClient... clients) {
		this.clients = Arrays.asList(clients).stream().filter(Objects::nonNull).toList();
	}

	public void index(String path, OlcaRepository repo, Commit previousCommit, Commit commit) {
		if (commit == null)
			return;
		var buffer = new EntryBuffer(clients, BUFFER_SIZE);
		var diffs = repo.diffs.find()
				.commit(previousCommit)
				.excludeCategories()
				.with(commit);
		for (var diff : diffs) {
			if (diff.diffType == DiffType.DELETED) {
				buffer.putRemove(Entry.toId(path, diff.type, diff.refId));
				continue;
			}
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
			var entry = new Entry(path, ref.type, ref.refId, processType, flowType, n);
			collectReferences(entry, null, json);
			if (entry.inputs.isEmpty() && entry.outputs.isEmpty() && entry.others.isEmpty())
				continue;
			buffer.putInsert(entry.getId(), entry);
		}
		buffer.flush();
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
			if ("allocationFactors".equals(field))
				continue;
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

	public void move(String oldPath, String newPath) {
		for (var client : clients) {
			var builder = new SearchQueryBuilder()
					.filter("path", SearchFilterValue.term(oldPath));
			var ids = client.searchIds(builder.build());
			if (ids.isEmpty())
				continue;
			client.update(ids, Maps.of("path", newPath));
		}
	}

	public void remove(String path) {
		for (var client : clients) {
			var builder = new SearchQueryBuilder()
					.filter("path", SearchFilterValue.term(path));
			var ids = client.searchIds(builder.build());
			if (ids.isEmpty())
				continue;
			client.remove(ids);
		}
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

	public record Entry(String path, ModelType type, String refId, ProcessType processType, FlowType flowType,
			String name, Set<String> inputs, Set<String> outputs, Set<String> others) {

		private Entry(String path, ModelType type, String refId, ProcessType processType, FlowType flowType,
				String name) {
			this(path, type, refId, processType, flowType, name, new HashSet<>(), new HashSet<>(), new HashSet<>());
		}

		private String getId() {
			return toId(path, type, refId);
		}

		private static String toId(String path, ModelType type, String refId) {
			return Strings.join(Arrays.asList(path, type.name(), refId), '/');
		}

	}

}