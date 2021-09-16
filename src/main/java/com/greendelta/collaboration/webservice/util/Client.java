package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import org.openlca.util.Strings;

import com.greendelta.collaboration.util.ObjectMap;
import com.sun.jersey.api.uri.UriComponent;

public class Client {

	private Client() {
		// only static access
	}

	public static <T> List<ObjectMap> map(List<T> list, Function<T, ObjectMap> function) {
		List<ObjectMap> all = new ArrayList<>();
		for (T element : list) {
			all.add(function.apply(element));
		}
		return all;
	}

	public static String removeStringFilter(String name, Map<String, Set<String>> filters) {
		return removeFilter(name, filters, "");
	}

	public static int removeIntFilter(String name, Map<String, Set<String>> filters, int defaultValue) {
		String value = removeFilter(name, filters, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}

	public static boolean removeBoolFilter(String name, Map<String, Set<String>> filters, boolean defaultValue) {
		String value = removeFilter(name, filters, Boolean.toString(defaultValue));
		return Boolean.parseBoolean(value);
	}

	private static String removeFilter(String name, Map<String, Set<String>> filters, String defaultValue) {
		Set<String> value = filters.remove(name);
		if (value == null)
			return defaultValue;
		if (value.size() == 0)
			return defaultValue;
		String first = value.iterator().next();
		if (Strings.nullOrEmpty(first))
			return defaultValue;
		return first;
	}

	public static Map<String, Set<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, Set<String>> filters = new HashMap<>();
		for (String key : uriInfo.getQueryParameters().keySet()) {
			Set<String> filterBy = filters.get(key);
			if (filterBy == null)
				filters.put(decode(key), filterBy = new HashSet<>());
			List<String> values = uriInfo.getQueryParameters().get(key);
			if (values == null)
				continue;
			for (String value : values)
				filterBy.add(decode(value));
		}
		return filters;
	}

	private static String decode(String value) {
		return UriComponent.decode(value, UriComponent.Type.PATH_SEGMENT);
	}

}
