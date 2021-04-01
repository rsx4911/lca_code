package com.greendelta.collaboration.model.settings;

import java.nio.charset.Charset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.greendelta.collaboration.model.AbstractEntity;

@Entity
@Table(name = "settings")
public class Setting<T extends SettingKey> extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private SettingType type;

	@Column(name = "owner")
	private String owner;

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	private String value;

	@Lob
	@Column(name = "data")
	private byte[] data;

	private Setting() {
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue() {
		T key = getKey();
		if (key.getType().equals(byte[].class))
			return (V) data;
		Class<?> type = key.getType();
		if (type == Boolean.class && value != null)
			return (V) new Boolean(Boolean.parseBoolean(value));
		if (type == Integer.class && value != null)
			return (V) new Integer(Integer.parseInt(value));
		if (type == Long.class && value != null)
			return (V) new Integer(Integer.parseInt(value));
		if (type == Object.class && data != null) {
			try {
				String json = new String(data, Charset.forName("utf-8"));
				return new Gson().fromJson(json, key.getSubType());
			} catch (JsonSyntaxException e) {
				return key.getDefaultValue();
			}
		}
		if (type == Object.class)
			return null;
		if (type == byte[].class)
			return (V) data;
		if (value == null || value.isEmpty())
			return key.getDefaultValue();
		return (V) value;
	}

	public void setValue(Object value) {
		Class<?> type = getKey().getType();
		if (value == null) {
			this.value = null;
			this.data = null;
			return;
		}
		if (type == Boolean.class) {
			if (value.getClass() == Boolean.class) {
				this.value = Boolean.toString((boolean) value);
			} else {
				String s = value.toString().toLowerCase();
				if (!s.equals("true") && !s.equals("false"))
					throw new IllegalArgumentException("Invalid value for type boolean: " + value);
				this.value = s;
			}
		} else if (type == Integer.class) {
			if (value.getClass() == Integer.class) {
				this.value = Integer.toString((int) value);
			} else {
				String s = value.toString();
				try {
					Integer.parseInt(s);
					this.value = s;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid value for type int: " + value);
				}
			}
		} else if (type == Long.class) {
			if (value.getClass() == Long.class) {
				this.value = Long.toString((int) value);
			} else {
				String s = value.toString();
				try {
					Long.parseLong(s);
					this.value = s;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid value for type long: " + value);
				}
			}
		} else if (type == Object.class) {
			if (value instanceof String) {
				this.data = value.toString().getBytes(Charset.forName("utf-8"));
			} else {
				this.data = new Gson().toJson(value).getBytes(Charset.forName("utf-8"));
			}
		} else if (type == byte[].class) {
			if (value.getClass() != byte[].class)
				throw new IllegalArgumentException("Invalid value for type byte[]: " + value);
			this.data = (byte[]) value;
		} else if (type == String.class) {
			this.value = value.toString();
		}
	}

	private T getKey() {
		return type.getSettingKey(name);
	}

	public static <T extends SettingKey> Setting<T> create(SettingType type, T key, String owner) {
		Setting<T> setting = new Setting<T>();
		setting.name = key.name();
		setting.type = type;
		setting.owner = owner;
		return setting;
	}

}
