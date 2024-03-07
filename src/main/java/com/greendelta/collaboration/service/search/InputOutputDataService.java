package com.greendelta.collaboration.service.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.Direction;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.util.FieldDefinition;
import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Maps;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;

@Service
public class InputOutputDataService {

	private static final int BUFFER_SIZE = 100;
	private static final Logger log = LogManager.getLogger(InputOutputDataService.class);
	private final SettingsService settings;

	public InputOutputDataService(SettingsService settings) {
		this.settings = settings;
	}

	public SearchResult<Map<String, Object>> query(Repository repo, Commit commit, String flowRefId,
			Direction direction,
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
		var ids = getIds(repo.path(), null);
		if (ids.isEmpty())
			return;
		getClient().remove(ids);
	}

	private Set<String> getIds(String path, Commit commit) {
		var query = new SearchQueryBuilder()
				.filter("repositoryPath", SearchFilterValue.term(path));
		if (commit != null) {
			query.filter("versions.commitId", SearchFilterValue.term(commit.id));
		}
		return getClient().searchIds(query.build());
	}

	void clearIndex() {
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

	void move(RepositoryPath path, Repository newRepo) {
		var ids = getIds(path.toString(), null);
		Map<String, Object> update = new HashMap<>();
		update.put("repositoryPath", newRepo.path());
		getClient().update(ids, update);
	}

	void index(Repository repo) {
		String previousCommitId = repo.settings.get(RepositorySetting.SEARCH_COMMIT_ID);
		var commits = repo.commits.find().after(previousCommitId).all();
		Commit previousCommit = previousCommitId != null ? repo.commits.get(previousCommitId) : null;
		var client = getClient();
		if (client == null)
			return;
		var buffer = new EntryBuffer(client, 1000);
		for (var commitIndex = 0; commitIndex < commits.size(); commitIndex++) {
			var commit = commits.get(commitIndex);
			var diffs = repo.diffs.find().commit(commit)
					.filter(ModelType.PROCESS.name())
					.excludeCategories()
					.withPreviousCommit();
			var skip = new HashSet<String>();
			for (var diff : diffs) {
				skip.add(diff.refId);
				if (diff.diffType == DiffType.DELETED)
					continue;
				var data = createData(repo, commit, commitIndex, diff);
				buffer.putInsert(getIndexId(repo.path(), commit.id, diff.refId), data);
			}
			buffer.flush();
			updatePrevious(buffer, repo, skip, previousCommit, commit);
			previousCommit = commit;
		}
	}

	private void updatePrevious(EntryBuffer buffer, Repository repo, Set<String> skipRefIds, Commit previousCommit, Commit commit) {
		if (previousCommit == null)
			return;
		var previous = getForCommit(repo, previousCommit);
		while (previous.hasNext()) {
			var data = previous.next();
			var refId = Maps.getString(data, "refId");
			var commitId = Maps.getString(data, "commitId");
			List<Map<String, Object>> versions = Maps.get(data, "versions");
			if (skipRefIds.contains(refId))
				continue;
			var last = versions.get(versions.size() - 1);
			var lastName = Maps.getString(last, "name");
			var lastType = ProcessType.valueOf(Maps.getString(last, "processType"));
			versions.add(Map.of("name", lastName,
					"processType", lastType,
					"commitId", commit.id));
			data.put("versions", versions);
			buffer.putUpdate(getIndexId(repo.path(), commitId, refId), data);
		}
		buffer.flush();
	}

	private IndexIterator getForCommit(Repository repo, Commit commit) {
		var query = new SearchQueryBuilder()
				.filter("repositoryPath", SearchFilterValue.term(repo.path()));
		if (commit != null) {
			query.filter("versions.commitId", SearchFilterValue.term(commit.id));
		}
		query.fields("refId", "commitId");
		query.arrayFields("versions.name", "versions.processType", "versions.commitId");
		return new IndexIterator(getClient(), query, BUFFER_SIZE);
	}

	private InputOutputData createData(Repository repo, Commit commit, int commitIndex, Diff diff) {
		var map = repo.datasets.parse(diff.newRef,
				FieldDefinition.firstOf("name"),
				FieldDefinition.firstOf("processType", ProcessType::valueOf),
				FieldDefinition.allOf("exchanges.flow.@id").ifIs("exchanges.isInput").name("inputs"),
				FieldDefinition.allOf("exchanges.flow.@id").ifIsNot("exchanges.isInput").name("outputs"),
				FieldDefinition.firstOf("exchanges.flow.flowType", FlowType::valueOf)
						.ifIs("exchanges.isQuantitativeReference")
						.name("flowType"));
		var name = Maps.getString(map, "name");
		ProcessType processType = Maps.get(map, "processType");
		FlowType flowType = Maps.get(map, "flowType");
		List<String> inputs = Maps.get(map, "inputs");
		List<String> outputs = Maps.get(map, "outputs");
		var data = new InputOutputData(repo.path(), commit.id, name, processType, flowType, diff.refId);
		if (inputs != null) {
			data.inputs.addAll(inputs);
		}
		if (outputs != null) {
			data.outputs.addAll(outputs);
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
