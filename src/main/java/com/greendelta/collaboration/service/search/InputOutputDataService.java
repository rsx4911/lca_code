package com.greendelta.collaboration.service.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.util.Diffs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.InputOutputData;
import com.greendelta.collaboration.model.InputOutputData.ProcessDescriptor;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Maps;

@Service
public class InputOutputDataService {

	private Dao<InputOutputData> dao;

	@Autowired
	public InputOutputDataService(Dao<InputOutputData> dao) {
		this.dao = dao;
	}

	public InputOutputData insert(InputOutputData data) {
		return dao.insert(data);
	}

	public InputOutputData get(Repository repo, Commit commit) {
		return dao.getFirst("SELECT data FROM InputOutputData data "
				+ "WHERE data.repositoryPath = :repositoryPath "
				+ "AND data.commitId = :commitId",
				Map.of("repositoryPath", repo.path(),
						"commitId", commit.id));
	}

	public void remove(Repository repo) {
		dao.update("DELETE FROM InputOutputData data WHERE data.repositoryPath = :repositoryPath",
				Maps.of("repositoryPath", repo.path()));
	}

	public void clear() {
		dao.update("DELETE FROM InputOutputData data", Collections.emptyMap());
	}

	void update(Repository repo, Repository newRepo) {
		dao.update("UPDATE InputOutputData data SET data.repositoryPath = :newPath WHERE data.repositoryPath = :path",
				Map.of("path", repo.path(),
						"newPath", newRepo.path()));
	}

	void update(Repository repo) {
		var commits = repo.commits().find().all();
		var previous = new InputOutputData();
		for (var commit : commits) {
			var data = get(repo, commit);
			if (data != null) {
				previous = data;
				continue;
			}
			var current = new InputOutputData();
			current.repositoryPath = repo.path();
			current.commitId = commit.id;
			current.descriptors = new HashMap<>(previous.descriptors);
			current.inputs = new HashMap<>(previous.inputs);
			current.outputs = new HashMap<>(previous.outputs);
			Diffs.of(repo.gitRepo(), commit)
					.filter(Collections.singletonList(ModelType.PROCESS.name()))
					.withPreviousCommit().stream()
					.forEach(diff -> put(repo, commit, diff, current));
			previous = insert(current);
		}
	}

	@SuppressWarnings("unchecked")
	private void put(Repository repo, Commit commit, Diff diff, InputOutputData data) {
		data.inputs.remove(diff.refId);
		data.outputs.remove(diff.refId);
		data.descriptors.remove(diff.refId);
		if (diff.diffType != DiffType.DELETED) {
			var oid = repo.ids().get(diff.path, commit.id);
			var d = repo.datasets().parse(oid, "name", "processType", "exchanges.flow.@id",
					"exchanges.isInput");
			var name = d.get("name") != null ? d.get("name").toString() : diff.refId;
			var processType = getProcessType(d.get("processType"));
			data.descriptors.put(diff.refId, new ProcessDescriptor(diff.refId, name, processType));
			var flowRefIds = (List<String>) d.get("exchanges.flow.@id");
			var isInput = (List<String>) d.get("exchanges.isInput");
			for (var i = 0; i < flowRefIds.size(); i++) {
				var flowRefId = flowRefIds.get(i);
				if (Boolean.parseBoolean(isInput.get(i))) {
					data.inputs.computeIfAbsent(diff.refId, k -> new HashSet<>()).add(flowRefId);
				} else {
					data.outputs.computeIfAbsent(diff.refId, k -> new HashSet<>()).add(flowRefId);
				}
			}
		}
	}

	private ProcessType getProcessType(Object value) {
		if (value != null && value.toString().equals("LCI_RESULT"))
			return ProcessType.LCI_RESULT;
		return ProcessType.UNIT_PROCESS;
	}

}
