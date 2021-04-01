package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.search.SearchService;

/**
 * When committing big amount of system processes (e.g. ecoinvent lci db) the
 * server ran into memory issues while indexing, when directly holding
 * input/output lists of all processes. To avoid incomplete commit indexing,
 * first the (flat) dataset index entries are collected and put into the search
 * index, while collecting the input/output lists in a memory efficient way.
 * After sucessfully indexing the whole commit the index entries with
 * the collected input/output lists are updated in chunks of 100
 */
class InputOutputList {

	private static final Logger log = LogManager.getLogger(InputOutputList.class);
	private final SearchService searchService;
	private final Repository repo;
	private final Commit commit;
	private final Map<String, Integer> flowToCount = new HashMap<>();
	private final Map<Integer, String> countToFlow = new HashMap<>();
	private final Map<String, List<Integer>> processToInputs = new HashMap<>();
	private final Map<String, List<Integer>> processToOutputs = new HashMap<>();
	private int count = 0;

	InputOutputList(SearchService searchService, Repository repo, Commit commit) {
		this.searchService = searchService;
		this.repo = repo;
		this.commit = commit;
	}

	@SuppressWarnings("unchecked")
	void append(String process, List<Map<String, Object>> exchanges) {
		if (exchanges == null)
			return;
		for (Map<String, Object> e : exchanges) {
			Map<String, Object> f = (Map<String, Object>) e.get("flow");
			if (f == null || f.get("@id") == null) {
				log.debug("Missing flow or flow['@id'] on exchange in process {} in repository {}", process,
						repo.toId());
				continue;
			}
			String flow = f.get("@id").toString();
			boolean input = e.get("input") != null && e.get("input").toString().toLowerCase().equals("true");
			append(process, flow, input);
		}
	}

	private void append(String process, String flow, boolean input) {
		Integer c = flowToCount.get(flow);
		if (c == null) {
			flowToCount.put(flow, c = ++count);
			countToFlow.put(c, flow);
		}
		append(process, c, input ? processToInputs : processToOutputs);
	}

	private void append(String process, int count, Map<String, List<Integer>> map) {
		List<Integer> list = map.get(process);
		if (list == null) {
			map.put(process, list = new ArrayList<>());
		}
		list.add(count);
	}

	void index() {
		Set<String> processes = new HashSet<>();
		processes.addAll(processToInputs.keySet());
		processes.addAll(processToOutputs.keySet());
		int count = 0;
		Map<String, Map<String, Object>> updates = new HashMap<>();
		for (String process : processes) {
			String id = IndexEntry.toIndexId(repo.toId(), ModelType.PROCESS, process, commit.id);
			Map<String, Object> update = new HashMap<>();
			update.put("inputs", popFlowList(process, processToInputs));
			update.put("outputs", popFlowList(process, processToOutputs));
			updates.put(id, update);
			if (++count == 100 || count == processes.size()) {
				searchService.update(updates);
				updates.clear();
				count = 0;
			}
		}
	}

	private List<String> popFlowList(String process, Map<String, List<Integer>> map) {
		List<Integer> list = map.remove(process);
		if (list == null || list.isEmpty())
			return null;
		List<String> flowList = new ArrayList<>();
		for (Integer count : list) {
			flowList.add(countToFlow.get(count));
		}
		return flowList;
	}

}