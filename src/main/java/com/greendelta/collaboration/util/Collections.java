package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Collections {

	public static <K, V> Set<V> addToSet(Map<K, Set<V>> map, K key, V setValue) {
		Set<V> set = map.get(key);
		if (set == null) {
			set = new HashSet<>();
			map.put(key, set);
		}
		set.add(setValue);
		return set;
	}

	public static <K, V> List<V> addToList(Map<K, List<V>> map, K key, V setValue) {
		List<V> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(setValue);
		return list;
	}

	public static <K, V> K remove(Map<K, ? extends Collection<V>> map, V value) {
		K match = null;
		for (K key : new ArrayList<>(map.keySet())) {
			Collection<V> col = map.get(key);
			if (!col.contains(value))
				continue;
			col.remove(value);
			if (!col.isEmpty())
				continue;
			match = key;
		}
		return match;
	}

	public static <K, V, S> K remove(Map<K, ? extends Collection<V>> map, S value, Function<V, S> converter) {
		K match = null;
		for (K key : new ArrayList<>(map.keySet())) {
			Collection<V> col = map.get(key);
			for (V val : new ArrayList<>(col)) {
				if (converter.apply(val).equals(value)) {
					col.remove(val);
				}
			}
			if (!col.isEmpty())
				continue;
			match = key;
		}
		return match;
	}

	public static <V> List<V> parseList(String values, Function<String, V> parse) {
		return parseList(values, ',', parse);
	}

	public static <V> List<V> parseList(String values, char splitChar, Function<String, V> parse) {
		List<V> list = new ArrayList<>();
		parseInto(list, values, splitChar, parse);
		return list;
	}

	public static <V> Set<V> parseSet(String values, Function<String, V> parse) {
		return parseSet(values, ',', parse);
	}

	public static <V> Set<V> parseSet(String values, char splitChar, Function<String, V> parse) {
		Set<V> set = new HashSet<>();
		parseInto(set, values, splitChar, parse);
		return set;
	}

	private static <C extends Collection<V>, V> void parseInto(C col, String values, char splitChar,
			Function<String, V> parse) {
		if (values == null)
			return;
		String[] split = values.split(Character.toString(splitChar));
		for (String value : split) {
			V parsed = parse.apply(value);
			col.add(parsed);
		}
	}

	public static <V, T> List<T> convertToList(Collection<V> col, Function<V, T> converter) {
		return (List<T>) convert(col, new ArrayList<>(), converter);
	}

	public static <V, T> Set<T> convertToSet(Collection<V> col, Function<V, T> converter) {
		return (Set<T>) convert(col, new HashSet<>(), converter);
	}
	
	private static <V, T> Collection<T> convert(Collection<V> from, Collection<T> to, Function<V, T> converter) {
		for (V elem : from) {
			to.add(converter.apply(elem));
		}
		return to;
	}

	public static <T, K, V> Map<K, V> map(Collection<T> col, Function<T, K> keyGenerator, Function<T, V> valueMapper) {
		Map<K, V> map = new HashMap<>();
		for (T elem : col) {
			map.put(keyGenerator.apply(elem), valueMapper.apply(elem));
		}
		return map;
	}

	public static <V> String stringify(Collection<V> col) {
		return stringify(col, ',');
	}

	public static <V> String stringify(Collection<V> col, char splitChar) {
		StringBuilder value = new StringBuilder();
		for (V entry : col) {
			if (value.length() != 0) {
				value.append(splitChar);
			}
			value.append(entry.toString());
		}
		return value.toString();
	}

	public static <V> List<V> filter(List<V> list, Function<V, Boolean> filter) {
		return filter(list, new ArrayList<>(), filter);
	}

	public static <V> Set<V> filter(Set<V> set, Function<V, Boolean> filter) {
		return filter(set, new HashSet<>(), filter);
	}

	private static <C extends Collection<V>, V> C filter(C col, C filtered, Function<V, Boolean> filter) {
		for (V value : col) {
			if (filter.apply(value))
				continue;
			filtered.add(value);
		}
		return filtered;
	}

	public static <T> List<T> pop(List<T> col, int amount) {
		List<T> sublist = new ArrayList<>();
		while (sublist.size() < amount && !col.isEmpty()) {
			sublist.add(col.remove(0));
		}
		return sublist;
	}

	public static <T> Set<T> pop(Set<T> col, int amount) {
		Set<T> sublist = new HashSet<>();
		Iterator<T> it = col.iterator();
		while (sublist.size() < amount && it.hasNext()) {
			sublist.add(it.next());
		}
		col.removeAll(sublist);
		return sublist;
	}

}
