package com.greendelta.collaboration.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.greendelta.search.wrapper.SearchClient;

class EntryBuffer {

	private final int bufferSize;
	private List<SearchClient> clients;
	private Set<String> toRemove = new HashSet<>();
	private Map<String, Object> toUpdate = new HashMap<>();
	private Map<String, Object> toInsert = new HashMap<>();

	EntryBuffer(SearchClient client, int bufferSize) {
		this.clients = Arrays.asList(client);
		this.bufferSize = bufferSize;
	}

	EntryBuffer(List<SearchClient> clients, int bufferSize) {
		this.clients = clients;
		this.bufferSize = bufferSize;
	}

	void flush() {
		if (!toInsert.isEmpty()) {
			for (var client : clients) {
				client.index(convert(toInsert));
			}
			toInsert.clear();
		}
		if (!toUpdate.isEmpty()) {
			for (var client : clients) {
				client.update(convert(toUpdate));
			}
			toUpdate.clear();
		}
		if (!toRemove.isEmpty()) {
			for (var client : clients) {
				client.remove(toRemove);
			}
			toRemove.clear();
		}
	}

	void putInsert(String id, Object entry) {
		toInsert.put(id, entry);
		checkFlush();
	}

	void putUpdate(String id, Object entry) {
		toUpdate.put(id, entry);
		checkFlush();
	}

	void putRemove(String id) {
		toRemove.add(id);
		checkFlush();
	}

	private void checkFlush() {
		if (toInsert.size() + toUpdate.size() + toRemove.size() == bufferSize) {
			flush();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Object>> convert(Map<String, Object> data) {
		var entries = new HashMap<String, Map<String, Object>>();
		data.forEach((refId, entry) -> entries.put(refId,
				entry instanceof Map ? (Map<String, Object>) entry : Maps.of(entry)));
		return entries;
	}

}