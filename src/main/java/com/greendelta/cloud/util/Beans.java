package com.greendelta.cloud.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Beans {

	private static final Logger log = LoggerFactory.getLogger(Beans.class);
	private static final BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();

	public static <T> T populate(T destination, Map<String, Object> sourceMap) {
		try {
			beanUtils.populate(destination, sourceMap);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("Error populating bean", e);
		}
		return destination;
	}

	public static void clearEmptyStringValues(Object object) {
		if (object == null)
			return;
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				boolean wasAccessible = field.isAccessible();
				field.setAccessible(true);
				if (field.getType() == String.class) {
					Object value = field.get(object);
					if ("".equals(value))
						field.set(object, null);
				}
				field.setAccessible(wasAccessible);
			}
		} catch (Exception e) {
			log.error("Error clearing empty strings on user");
		}
	}

	public static void populateProperties(Object from, Object to,
			String... fields) {
		if (fields == null)
			return;
		for (String name : fields) {
			Field fromField = getField(from.getClass(), name);
			Field toField = getField(to.getClass(), name);
			if (fromField == null || toField == null)
				continue;
			setFieldValue(from, fromField, to, toField);
		}
	}

	private static Field getField(Class<?> clazz, String name) {
		if (clazz == Object.class)
			return null;
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception e) {
			// ignore exceptions, security is not an issue
			// if field does not exist, move on
		}
		if (field != null)
			return field;
		return getField(clazz.getSuperclass(), name);
	}

	private static void setFieldValue(Object from, Field fromField, Object to,
			Field toField) {
		boolean fromAccessibility = fromField.isAccessible();
		if (!fromAccessibility)
			fromField.setAccessible(true);
		boolean toAccessibility = toField.isAccessible();
		if (!toAccessibility)
			toField.setAccessible(true);
		try {
			Object value = fromField.get(from);
			if (value instanceof List)
				value = new ArrayList<>((List<?>) value);
			else if (value instanceof Set)
				value = new HashSet<>((Set<?>) value);
			toField.set(to, value);
		} catch (Exception e) {
			log.error("Error setting field value", e);
		} finally {
			fromField.setAccessible(fromAccessibility);
			toField.setAccessible(toAccessibility);
		}
	}

}
