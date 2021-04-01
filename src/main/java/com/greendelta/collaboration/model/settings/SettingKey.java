package com.greendelta.collaboration.model.settings;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public interface SettingKey {

	String name();

	Class<?> getType();

	default Type getSubType() {
		return null;
	}

	default <T> T getDefaultValue() {
		return null;
	}

	default String toString(Object value) {
		checkValue(value);
		if (getType() == Boolean.class)
			if (value instanceof Boolean)
				return Boolean.toString((boolean) value);
		if (getType() == Integer.class)
			if (value instanceof Integer)
				return Integer.toString((int) value);
		if (getType() == Object.class)
			if (value != null)
				return new Gson().toJson(value);
		if (value == null)
			return null;
		return value.toString();
	}

	@SuppressWarnings("unchecked")
	default <T> T parse(String value) {
		checkValue(value);
		if (getType() == Boolean.class)
			return (T) new Boolean(Boolean.parseBoolean(value));
		if (getType() == Integer.class && value != null)
			return (T) new Integer(Integer.parseInt(value));
		if (getType() == String.class)
			if (value == null || value.isEmpty())
				return (T) getDefaultValue();
			else
				return (T) value;
		if (getType() == Object.class && value != null)
			return new Gson().fromJson(value, getSubType());
		return (T) value;
	}

	default void checkValue(Object value) {
		if (getType() == Boolean.class) {
			if (value == null)
				throw new IllegalArgumentException("Null value not allowed for type Boolean");
			if (!value.toString().equals("true") && !value.toString().equals("false"))
				throw new IllegalArgumentException(value.toString() + " is not a valid Boolean value");
		} else if (getType() == Integer.class && value != null) {
			try {
				Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(value.toString() + " is not a valid Integer value");
			}
		} else if (value != null && getType() != value.getClass()) {
			throw new IllegalArgumentException("Value type does not match key type: "
					+ value.getClass().getCanonicalName() + " != " + getType().getCanonicalName());
		}
	}

}