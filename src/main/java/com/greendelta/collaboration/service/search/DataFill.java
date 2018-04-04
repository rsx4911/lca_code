package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.greendelta.collaboration.model.index.IndexEntry;

public class DataFill {

	public static void categories(IndexEntry entry, List<String> categories) {
		CategoryInfo info = new CategoryInfo(categories);
		entry.categories = categories;
		if (entry.category == null) {
			entry.category = info.category;
		}
		if (entry.categoryPaths == null) {
			entry.categoryPaths = info.categoryPaths;
		}
	}

	public static void categories(Map<String, Object> map, List<String> categories) {
		CategoryInfo info = new CategoryInfo(categories);
		map.put("categories", categories);
		if (!map.containsKey("category")) {
			map.put("category", info.category);
		}
		if (!map.containsKey("categoryPaths")) {
			map.put("categoryPaths", info.categoryPaths);
		}
	}

	private static class CategoryInfo {

		private final String category;
		private final List<String> categoryPaths;

		private CategoryInfo(List<String> categories) {
			if (categories == null || categories.size() == 0) {
				this.category = null;
				this.categoryPaths = null;
				return;
			}
			String category = "";
			for (String cat : categories) {
				if (!category.isEmpty()) {
					category += '/';
				}
				category += cat;
			}
			this.category = category;
			this.categoryPaths = new ArrayList<>();
			String current = null;
			for (String cat : categories) {
				if (current == null) {
					current = cat;
				} else {
					current += '/' + cat;
				}
				categoryPaths.add(current);
			}
		}

	}

}
