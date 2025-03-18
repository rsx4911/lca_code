package com.greendelta.collaboration.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.revwalk.RevCommit;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

class Maps {

	private static final Logger log = LogManager.getLogger(Maps.class);
	private static final JsonMapper mapper = JsonMapper.builder()
			.configure(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS, true)
			.addMixIn(RevCommit.class, IgnoreMixIn.class)
			.build();

	private Maps() {
	}

	static Map<String, Object> of(String key, Object value) {
		return new HashMap<>(Map.of(key, value));
	}

	@SuppressWarnings("unchecked")
	static Map<String, Object> of(Object object) {
		if (object == null)
			return null;
		if (object instanceof String json)
			return toMap(json);
		if (object instanceof Map)
			return (Map<String, Object>) object;
		return mapper.convertValue(object, new TypeReference<Map<String, Object>>() {
		});
	}

	private static Map<String, Object> toMap(String json) {
		try {
			return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			log.error("Error mapping json", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static Collection<Object> toArray(Object value) {
		if (value == null)
			return Collections.emptyList();
		if (value instanceof Collection)
			return ((Collection<Object>) value);
		return Collections.singletonList(value);
	}

	public static <T> T get(Map<String, Object> map, String field) {
		if (field == null)
			return null;
		return get(map, field, false, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAll(Map<String, Object> map, String field, Class<T> clazz) {
		var values = new ArrayList<T>();
		var all = getAll(map, field);
		for (var value : all) {
			if (clazz == Long.class && value instanceof Integer) {
				value = ((Integer) value).longValue();
			}
			values.add((T) value);
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	private static Collection<Object> getAll(Map<String, Object> map, String field) {
		Collection<Object> all = new ArrayList<>();
		if (!field.contains(".") || map.containsKey(field))
			return toArray(map.get(field));
		var prefix = field.substring(0, field.lastIndexOf('.'));
		field = field.substring(field.lastIndexOf('.') + 1);
		var allNext = getAll(map, prefix);
		for (var next : allNext) {
			if (next instanceof Map) {
				all.addAll(getAll((Map<String, Object>) next, field));
			}
		}
		return all;
	}

	// boolean initialCall is to distinguish between recursive call and initial
	// call, createMissing only applies to recursive calls
	@SuppressWarnings("unchecked")
	private static <T> T get(Map<String, Object> map, String field, boolean createMissing, boolean initialCall) {
		if (field.contains(".") && !map.containsKey(field)) {
			var prefix = field.substring(0, field.lastIndexOf('.'));
			field = field.substring(field.lastIndexOf('.') + 1);
			map = get(map, prefix, createMissing, false);
		}
		Object value = null;
		if (map != null) {
			value = map.get(field);
		}
		if (value == null && createMissing && !initialCall) {
			value = new HashMap<String, Object>();
		}
		return (T) value;
	}

	public static String[] getStringArray(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value == null)
			return null;
		if (value instanceof String[] array)
			return array;
		if (value instanceof Collection) {
			var values = new ArrayList<String>();
			for (var v : (Collection<?>) value) {
				values.add(v.toString());
			}
			return values.toArray(new String[values.size()]);
		}
		return new String[] { value.toString() };
	}

	public static String getString(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value == null)
			return null;
		if (value instanceof String[] array)
			return array[0];
		return value.toString();
	}

	public static int getInteger(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value == null)
			return 0;
		try {
			if (value instanceof String[] array)
				return Integer.parseInt(array[0]);
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static boolean getBoolean(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value == null)
			return false;
		String stringValue = null;
		if (value instanceof String[] array) {
			stringValue = array[0].toLowerCase();
		} else {
			stringValue = value.toString().toLowerCase();
		}
		return switch (stringValue) {
			case "true", "on", "yes" -> true;
			default -> false;
		};
	}

	public static boolean isArray(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value == null)
			return false;
		if (value instanceof Collection)
			return true;
		if (value.getClass().isArray())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getArray(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value == null)
			return null;
		if (value instanceof Collection)
			return new ArrayList<>((Collection<T>) value);
		if (value.getClass().isArray())
			return Arrays.asList((T[]) value);
		return Arrays.asList((T) value);
	}

	public static Set<String> getSet(Map<String, Object> map, String field) {
		var array = Maps.getStringArray(map, field);
		if (array == null)
			return null;
		return new HashSet<>(Arrays.asList(array));
	}

	public static boolean isObject(Map<String, Object> map, String field) {
		var value = get(map, field);
		return is(value);
	}

	public static boolean is(Object value) {
		return value instanceof Map;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getObject(Map<String, Object> map, String field) {
		var value = get(map, field);
		if (value instanceof Map)
			return (Map<String, Object>) value;
		return null;
	}

	@JsonIgnoreType
	private class IgnoreMixIn {
	}

}
