package com.greendelta.cloud.platform.guice.util;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class BindUtils {

	@SafeVarargs
	public static <T> void multibind(Binder binder, Class<T> bindTo, Class<? extends T>... toBind) {
		namedMultibind(binder, bindTo, null, toBind);
	}

	@SafeVarargs
	public static <T> void namedMultibind(Binder binder, Class<T> bindTo, String name, Class<? extends T>... toBind) {
		if (toBind == null || toBind.length == 0)
			return;
		Multibinder<T> bindings = getMultibinder(binder, bindTo, name);
		for (Class<? extends T> implementation : toBind)
			bindings.addBinding().to(implementation);
	}

	private static <T> Multibinder<T> getMultibinder(Binder binder, Class<T> bindTo, String name) {
		if (name != null)
			return Multibinder.newSetBinder(binder, bindTo, Names.named(name));
		return Multibinder.newSetBinder(binder, bindTo);
	}

}
