package com.greendelta.collaboration.webservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.openlca.cloud.util.ObjectMap;

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
	
}
