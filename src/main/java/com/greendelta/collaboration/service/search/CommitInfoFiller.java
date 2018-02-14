package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.util.ObjectMap;

class CommitInfoFiller {

	void apply(List<ObjectMap> all, List<ObjectMap> entries, int pathDepth) {
		Map<String, List<ObjectMap>> lastForPath = getForPath(all, pathDepth);
		for (ObjectMap entry : entries) {
			if (entry.get("type") != ModelType.CATEGORY)
				continue;
			// entries are supposed to be sorted by timestamp
			List<ObjectMap> children = lastForPath.get(entry.get("fullPath"));
			if (children != null) {
				ObjectMap lastChild = children.get(0);
				entry.put("commitId", lastChild.get("commitId"));
				entry.put("commitMessage", lastChild.get("commitMessage"));
				entry.put("commitTimestamp", lastChild.get("commitTimestamp"));
				int count = 0;
				for (ObjectMap child : children) {
					if (child.get("type") == ModelType.CATEGORY)
						continue;
					count++;
				}
				entry.put("count", count);
			} else {
				entry.put("count", 0);
			}

		}
	}

	private Map<String, List<ObjectMap>> getForPath(List<ObjectMap> entries, int depth) {
		Map<String, List<ObjectMap>> map = new HashMap<>();
		for (ObjectMap entry : entries) {
			String path = getSubPath(entry.get("fullPath"), depth);
			List<ObjectMap> pathEntries = map.get(path);
			if (pathEntries == null) {
				map.put(path, pathEntries = new ArrayList<>());
			}
			pathEntries.add(entry);
		}
		return map;
	}

	private String getSubPath(String path, int depth) {
		String subPath = "";
		String[] pathSplit = path.split("/");
		for (int i = 0; i < depth; i++) {
			if (!subPath.isEmpty()) {
				subPath += "/";
			}
			subPath += pathSplit[i];
		}
		return subPath;
	}

}
