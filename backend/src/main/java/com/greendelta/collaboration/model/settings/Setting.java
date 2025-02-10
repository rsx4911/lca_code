package com.greendelta.collaboration.model.settings;

import java.nio.charset.StandardCharsets;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greendelta.collaboration.model.AbstractEntity;

@Entity
@Table
public class Setting extends AbstractEntity {

	@Column
	@Enumerated(EnumType.STRING)
	private SettingType type;

	@Column
	private String owner;

	@Column
	private String name;

	@Column(length = 65535)
	private String value;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] data;

	private Setting() {
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue() {
		var key = getKey();
		if (key.getType().equals(byte[].class))
			return (V) data;
		var type = key.getType();
		if (type.isEnum())
			return getEnumValue(type, value);
		if (type == Boolean.class && value != null)
			return (V) Boolean.valueOf(Boolean.parseBoolean(value));
		if (type == Integer.class && value != null)
			return (V) Integer.valueOf(Integer.parseInt(value));
		if (type == Long.class && value != null)
			return (V) Long.valueOf(Long.parseLong(value));
		if (type == Object.class && data != null) {
			try {
				String json = new String(data, StandardCharsets.UTF_8);
				return new ObjectMapper().readValue(json, key.getSubType());
			} catch (JsonProcessingException e) {
				return key.getDefaultValue();
			}
		}
		if (type == Object.class)
			return null;
		if (type == byte[].class)
			return (V) data;
		if (value == null)
			return key.getDefaultValue();
		return (V) value;
	}

	@SuppressWarnings("unchecked")
	private <V> V getEnumValue(Class<?> type, String value) {
		for (var v : type.getEnumConstants())
			if (v.toString().equals(value))
				return (V) v;
		return null;
	}

	public void setValue(Object value) {
		var type = getKey().getType();
		if (value == null) {
			this.value = null;
			this.data = null;
			return;
		}
		if (type.isEnum()) {
			this.value = value.toString();
		} else if (type == Boolean.class) {
			if (value.getClass() == Boolean.class) {
				this.value = Boolean.toString((boolean) value);
			} else {
				var s = value.toString().toLowerCase();
				if (!s.equals("true") && !s.equals("false"))
					throw new IllegalArgumentException("Invalid value for type boolean: " + value);
				this.value = s;
			}
		} else if (type == Integer.class) {
			if (value.getClass() == Integer.class) {
				this.value = Integer.toString((int) value);
			} else {
				var s = value.toString();
				try {
					Integer.parseInt(s);
					this.value = s;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid value for type int: " + value);
				}
			}
		} else if (type == Long.class) {
			if (value.getClass() == Long.class) {
				this.value = Long.toString((long) value);
			} else {
				var s = value.toString();
				try {
					Long.parseLong(s);
					this.value = s;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid value for type long: " + value);
				}
			}
		} else if (type == Object.class) {
			if (value instanceof String) {
				this.data = value.toString().getBytes(StandardCharsets.UTF_8);
			} else {
				try {
					this.data = new ObjectMapper().writeValueAsBytes(value);
				} catch (JsonProcessingException e) {
					throw new IllegalArgumentException("Invalid value for type Object: " + value);
				}
			}
		} else if (type == byte[].class) {
			if (value.getClass() != byte[].class)
				throw new IllegalArgumentException("Invalid value for type byte[]: " + value);
			this.data = (byte[]) value;
		} else if (type == String.class) {
			this.value = value.toString();
		}
	}

	public SettingKey getKey() {
		return type.getSettingKey(name);
	}

	public static Setting create(SettingType type, SettingKey key, String owner) {
		var setting = new Setting();
		setting.name = key.name();
		setting.type = type;
		setting.owner = owner;
		return setting;
	}

}
