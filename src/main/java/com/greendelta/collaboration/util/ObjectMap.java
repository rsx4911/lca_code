package com.greendelta.collaboration.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.revwalk.RevCommit;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 8510487677972200938L;
	private static final Logger log = LogManager.getLogger(ObjectMap.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.addMixIn(RevCommit.class, IgnoreMixIn.class);
	}
	
	public ObjectMap() {
		this(new HashMap<>());
	}

	public static ObjectMap fromJson(String json) {
		return new ObjectMap(toMap(json));
	}

	public static ObjectMap fromMap(Map<String, Object> managed) {
		return new ObjectMap(managed);
	}

	public static ObjectMap fromObject(Object object) {
		if (object == null)
			return null;
		return new ObjectMap(mapper.convertValue(object, new TypeReference<Map<String, Object>>() {
		}));
	}

	private ObjectMap(Map<String, Object> managed) {
		if (managed != null) {
			putAll(managed);
		}
	}

	private static Map<String, Object> toMap(String json) {
		if (json == null)
			return null;
		try {
			return new ObjectMap(mapper.readValue(json, new TypeReference<Map<String, Object>>() {
			}));
		} catch (IOException e) {
			log.error("Error mapping json", e);
			return null;
		}
	}

	public void removeAllBut(String... fields) {
		if (fields == null)
			return;
		var fieldSet = new HashSet<String>();
		for (var field : fields) {
			if (field.contains("."))
				throw new IllegalArgumentException("removeAllBut doesn't support complex fields");
			fieldSet.add(field);
		}
		for (var key : new HashSet<>(keySet())) {
			if (!fieldSet.contains(key)) {
				remove(key);
			}
		}
	}

	@Override
	public boolean remove(Object key, Object value) {
		if (key instanceof String && value instanceof String) {
			remove(new String[] { key.toString(), value.toString() });
			return true;
		}
		return super.remove(key, value);
	}

	public void remove(String... fields) {
		if (fields == null)
			return;
		for (String field : fields) {
			remove(field);
		}
	}

	@Override
	public Object remove(Object field) {
		return remove(this, field != null ? field.toString() : null);
	}

	private Object remove(Map<String, Object> map, String field) {
		if (map == null)
			return null;
		if (field.contains(".")) {
			var prefix = field.substring(0, field.lastIndexOf('.'));
			field = field.substring(field.lastIndexOf('.') + 1);
			var allNext = getAll(map, prefix);
			var previous = new ArrayList<>();
			for (var next : allNext) {
				if (next instanceof Map) {
					previous.add(((Map<?, ?>) next).remove(field));
				}
			}
			return previous;
		}
		if (map == this)
			return super.remove(field);
		return map.remove(field);
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> toArray(Object value) {
		if (value == null)
			return Collections.emptyList();
		if (value instanceof Collection)
			return ((Collection<Object>) value);
		return Collections.singletonList(value);
	}

	@Override
	public Object put(String field, Object value) {
		Map<String, Object> map = this;
		if (field.contains(".")) {
			var prefix = field.substring(0, field.lastIndexOf('.'));
			field = field.substring(field.lastIndexOf('.') + 1);
			map = get(map, prefix, true, true);
		}
		if (map == this)
			return super.put(field, value);
		return map.put(field, value);
	}

	public void nullify(String... fields) {
		if (fields == null)
			return;
		for (var field : fields) {
			put(field, null);
		}
	}

	public void removeEmptyOrNull() {
		removeEmptyOrNull(this);
	}

	@SuppressWarnings("unchecked")
	private void removeEmptyOrNull(Map<String, Object> map) {
		for (var key : new HashSet<>(map.keySet())) {
			var value = map.get(key);
			if (map.get(key) == null) {
				map.remove(key);
			} else if (value instanceof String && ((String) value).isEmpty()) {
				map.remove(key);
			} else if (value instanceof Map) {
				removeEmptyOrNull((Map<String, Object>) value);
			}
		}
	}

	@Override
	public Object get(Object field) {
		if (field == null)
			return null;
		return get(this, field.toString(), false, true);
	}

	public <T> T get(String field) {
		if (field == null)
			return null;
		return get(this, field, false, true);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getAll(String field, Class<T> clazz) {
		var values = new ArrayList<T>();
		var all = getAll(this, field);
		for (var value : all) {
			if (clazz == Long.class && value instanceof Integer) {
				value = ((Integer) value).longValue();
			}
			values.add((T) value);
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> getAll(Map<String, Object> map, String field) {
		Collection<Object> all = new ArrayList<>();
		if (!field.contains("."))
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
	private <T> T get(Map<String, Object> map, String field, boolean createMissing, boolean initialCall) {
		if (field.contains(".")) {
			var prefix = field.substring(0, field.lastIndexOf('.'));
			field = field.substring(field.lastIndexOf('.') + 1);
			map = get(map, prefix, createMissing, false);
		}
		Object value = null;
		if (map != null) {
			if (map == this) {
				value = super.get(field);
			} else {
				value = map.get(field);
			}
		}
		if (value == null && createMissing && !initialCall) {
			value = new HashMap<String, Object>();
		}
		return (T) value;
	}

	public int getArrayLength(String field) {
		var array = get(this, field, false, true);
		if (array == null) {
			array = 0;
		}
		if (array instanceof Collection)
			return ((Collection<?>) array).size();
		return 0;
	}

	public String[] getStringArray(String field) {
		var value = get(field);
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
		return null;
	}

	public String getString(String field) {
		var value = get(field);
		if (value == null)
			return null;
		if (value instanceof String[] array)
			return array[0];
		return value.toString();
	}

	public long getLong(String field) {
		var value = get(field);
		if (value == null)
			return 0;
		try {
			if (value instanceof String[] array)
				return Double.valueOf(Double.parseDouble(array[0])).longValue();
			return Double.valueOf(Double.parseDouble(value.toString())).longValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public double getDouble(String field) {
		var value = get(field);
		if (value == null)
			return 0;
		try {
			if (value instanceof String[] array)
				return Double.parseDouble(array[0]);
			return Double.parseDouble(value.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public int getInteger(String field) {
		var value = get(field);
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

	public boolean getBoolean(String field) {
		var value = get(field);
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

	public boolean isArray(String field) {
		var value = get(field);
		if (value == null)
			return false;
		if (value instanceof Collection)
			return true;
		if (value.getClass().isArray())
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getArray(String field) {
		var value = get(field);
		if (value == null)
			return null;
		if (value instanceof Collection)
			return new ArrayList<>((Collection<T>) value);
		if (value.getClass().isArray())
			return Arrays.asList((T[]) value);
		return null;
	}

	public boolean isObject(String field) {
		var value = get(field);
		return value instanceof Map;
	}

	@SuppressWarnings("unchecked")
	public ObjectMap getObject(String field) {
		var value = get(field);
		if (value instanceof Map)
			return ObjectMap.fromMap((Map<String, Object>) value);
		return null;
	}
	
	@JsonIgnoreType
	private class IgnoreMixIn {}

}
