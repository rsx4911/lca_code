package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.Direction;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.util.Diffs;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class InputOutputDataService {

	private static final Logger log = LogManager.getLogger(InputOutputDataService.class);
	private final SettingsService settings;
	private final EntryBuffer buffer;

	@Autowired
	public InputOutputDataService(SettingsService settings) {
		this.settings = settings;
		this.buffer = new EntryBuffer(getClient(), 100);
	}

	public SearchResult<Map<String, Object>> get(Repository repo, Commit commit, String flowRefId, Direction direction,
			int page, int pageSize, String filter) {
		var query = new SearchQueryBuilder();
		query.filter("repositoryPath", SearchFilterValue.term(repo.path()));
		query.page(page);
		query.pageSize(pageSize);
		query.fields("refId");
		var value = SearchFilterValue.term(flowRefId);
		if (direction == Direction.INPUT) {
			query.filter("inputs", value);
		} else if (direction == Direction.OUTPUT) {
			query.filter("outputs", value);
		} else {
			query.filter(new String[] { "inputs", "outputs" }, value);
		}
		if (!Strings.nullOrEmpty(filter)) {
			query.filter("versions.name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		query.filter("versions.commitId", SearchFilterValue.term(commit.id));
		return getClient().search(query.build());
	}

	void remove(Repository repo) {
		var ids = getIds(repo, null);
		if (ids.isEmpty())
			return;
		getClient().remove(ids);
	}

	private Set<String> getIds(Repository repo, Commit commit) {
		var query = new SearchQueryBuilder()
				.filter("repositoryPath", SearchFilterValue.term(repo.path()));
		if (commit != null) {
			query.filter("versions.commitId", SearchFilterValue.term(commit.id));
		}
		return getClient().searchIds(query.build());
	}

	public void clearIndex() {
		getClient().delete();
		createIndex();
	}

	private void createIndex() {
		try {
			getClient().create(Map.of(
					"config", readJson("os-io-config.json"),
					"mapping", readJson("os-io-mapping.json")));
		} catch (IOException e) {
			log.error("Error creating search io index", e);
		}
	}

	private String readJson(String resource) throws IOException {
		var stream = getClass().getResourceAsStream(resource);
		if (stream == null)
			return "{}";
		return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
	}

	void update(Repository repo, Repository newRepo) {
		var ids = getIds(repo, null);
		Map<String, Object> update = new HashMap<>();
		update.put("repositoryPath", newRepo.path());
		getClient().update(ids, update);
	}

	void index(Repository repo) {
		var commits = repo.commits().find().all();
		Commit previousCommit = null;
		for (var commitIndex = 0; commitIndex < commits.size(); commitIndex++) {
			var commit = commits.get(commitIndex);
			var diffs = Diffs.of(repo.gitRepo(), commit)
					.filter(Collections.singletonList(ModelType.PROCESS.name()))
					.withPreviousCommit();
			var previous = previousCommit != null ? getIds(repo, previousCommit) : new HashSet<String>();
			var skip = new HashSet<String>();
			for (var diff : diffs) {
				skip.add(diff.refId);
				if (diff.diffType == DiffType.DELETED)
					continue;
				var data = createData(repo, commit, commitIndex, diff);
				buffer.putInsert(getIndexId(repo.path(), commit.id, diff.refId), data);
			}
			updatePrevious(repo, previous, skip, commit);
			previousCommit = commit;
			buffer.flush();
		}
	}

	private void updatePrevious(Repository repo, Set<String> ids, Set<String> skipRefIds, Commit commit) {
		var stack = new Stack<String>();
		stack.addAll(ids);
		while (!stack.isEmpty()) {
			var nextIds = new HashSet<String>();
			while (nextIds.size() < 100 && !stack.isEmpty()) {
				nextIds.add(stack.pop());
			}
			var nextElements = getClient().get(nextIds);
			for (var next : nextElements) {
				var data = new ObjectMapper().convertValue(next, InputOutputData.class);
				if (skipRefIds.contains(data.refId))
					continue;
				data.addCommitId(commit.id);
				buffer.putUpdate(getIndexId(data.repositoryPath, data.commitId, data.refId), Maps.of(data));
			}
		}
		buffer.flush();
	}

	@SuppressWarnings("unchecked")
	private InputOutputData createData(Repository repo, Commit commit, int commitIndex, Diff diff) {
		var oid = repo.ids().get(diff.path, commit.id);
		var map = repo.datasets().parse(oid, "name", "processType", "exchanges.flow.@id", "exchanges.isInput");
		var flowRefIds = (List<String>) map.get("exchanges.flow.@id");
		var isInput = (List<String>) map.get("exchanges.isInput");
		var name = Maps.getString(map, "name");
		var type = Maps.getString(map, "processType");
		var processType = ProcessType.LCI_RESULT.name().equals(type)
				? ProcessType.LCI_RESULT
				: ProcessType.UNIT_PROCESS;
		var data = new InputOutputData(repo.path(), commit.id, name, processType, diff.refId);
		for (var i = 0; i < flowRefIds.size(); i++) {
			var flowRefId = flowRefIds.get(i);
			if (Boolean.parseBoolean(isInput.get(i))) {
				data.inputs.add(flowRefId);
			} else {
				data.outputs.add(flowRefId);
			}
		}
		return data;
	}

	private SearchClient getClient() {
		return settings.searchConfig.getIoDataSearchClient();
	}

	private String getIndexId(String repositoryPath, String commitId, String processRefId) {
		return repositoryPath + "/" + commitId + "/" + processRefId;
	}

}
