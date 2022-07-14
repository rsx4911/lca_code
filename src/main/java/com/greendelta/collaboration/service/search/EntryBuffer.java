package com.greendelta.collaboration.service.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.greendelta.collaboration.util.Maps;
import com.greendelta.search.wrapper.SearchClient;

class EntryBuffer {

	private final int bufferSize;
	private SearchClient client;
	private Set<String> toRemove = new HashSet<>();
	private Map<String, Object> toUpdate = new HashMap<>();
	private Map<String, Object> toInsert = new HashMap<>();

	EntryBuffer(SearchClient client, int bufferSize) {
		this.client = client;
		this.bufferSize = bufferSize;
	}

	void flush() {
		if (!toInsert.isEmpty()) {
			client.index(convert(toInsert));
			toInsert.clear();
		}
		if (!toUpdate.isEmpty()) {
			client.update(convert(toUpdate));
			toUpdate.clear();
		}
		if (!toRemove.isEmpty()) {
			client.remove(toRemove);
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