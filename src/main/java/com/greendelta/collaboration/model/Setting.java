package com.greendelta.collaboration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Setting extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	public Key name;

	@Column(name = "value")
	public String value;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public enum Key {

		MESSAGING_ENABLED(Boolean.class, true),
		TASKS_ENABLED(Boolean.class, true),
		COMMENTS_ENABLED(Boolean.class, true),
		PUBLIC_REPOSITORY_ENABLED(Boolean.class, true);

		public final Class<?> type;
		private final Object defaultValue;

		private Key(Class<?> type, Object defaultValue) {
			this.type = type;
			this.defaultValue = defaultValue;
		}

		public String toString(Object value) {
			checkValue(value);
			if (type == Boolean.class)
				if (value instanceof Boolean)
					return Boolean.toString((boolean) value);
			return value.toString();
		}

		@SuppressWarnings("unchecked")
		public <T> T parse(String value) {
			checkValue(value);
			if (type == Boolean.class)
				return (T) new Boolean(Boolean.parseBoolean(value));
			return (T) value;
		}

		@SuppressWarnings("unchecked")
		public <T> T getDefaultValue() {
			return (T) defaultValue;
		}

		public void checkValue(Object value) {
			if (type == Boolean.class) {
				if (value == null)
					throw new IllegalArgumentException("Null value not allowed for type Boolean");
				if (!value.toString().equals("true") && !value.toString().equals("false"))
					throw new IllegalArgumentException(value.toString() + " is not a valid Boolean value");
			} else if (value != null && type != value.getClass()) {
				throw new IllegalArgumentException("Value type does not match key type: "
						+ value.getClass().getCanonicalName() + " != " + type.getCanonicalName());
			}
		}
		
	}

}
