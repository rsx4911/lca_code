package com.greendelta.collaboration.model;

import java.util.Arrays;
import java.util.List;

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

		// features
		MESSAGING_ENABLED(Boolean.class, true),
		TASKS_ENABLED(Boolean.class, true),
		COMMENTS_ENABLED(Boolean.class, true),
		PUBLIC_REPOSITORY_ENABLED(Boolean.class, true),
		NOTIFICATIONS_ENABLED(Boolean.class, true),
		USER_REGISTRATION_ENABLED(Boolean.class, true),
		USER_REGISTRATION_APPROVAL_ENABLED(Boolean.class, true),
		DASHBOARD_ACTIVITIES_ENABLED(Boolean.class, true),
		REPOSITORY_ACTIVITIES_ENABLED(Boolean.class, true),
		HOMEPAGE_ENABLED(Boolean.class, true),

		// basic settings
		SERVER_NAME(String.class, "LCA Collaboration Server"),
		SERVER_URL(String.class),
		REPOSITORY_PATH(String.class),
		LIBRARY_PATH(String.class),
		GLAD_URL(String.class),
		GLAD_API_KEY_HEADER(String.class, "api-key"),
		GLAD_API_KEY(String.class),

		// search settings
		SEARCH_CLUSTER(String.class, "elasticsearch"),
		SEARCH_HOST(String.class, "localhost"),
		SEARCH_INDEX_NAME(String.class, "collaboration-server"),
		SEARCH_PORT(Integer.class, 9200),
		
		// home settings
		HOME_TITLE(String.class, ""),
		HOME_TEXT(String.class, ""),
		HOME_REPOSITORY_ORDER(String.class, ""),
		HOME_HIDDEN_REPOSITORIES(String.class, ""),
		
		// imprint
		IMPRINT_COMPANY(String.class),
		IMPRINT_CEO(String.class),
		IMPRINT_STREET(String.class),
		IMPRINT_ZIP_CODE(String.class),
		IMPRINT_CITY(String.class),
		IMPRINT_COUNTRY(String.class),
		IMPRINT_PHONE(String.class),
		IMPRINT_FAX(String.class),
		IMPRINT_EMAIL(String.class),
		IMPRINT_WEBSITE(String.class),
		IMPRINT_REGISTRATION(String.class),
		IMPRINT_VAT(String.class),

		// mail configuration
		MAIL_USER(String.class),
		MAIL_PASS(String.class),
		MAIL_PROTO(String.class, "smtps"),
		MAIL_HOST(String.class),
		MAIL_PORT(Integer.class, 465),
		MAIL_SSL(Boolean.class, true),
		MAIL_TLS(Boolean.class, false),
		MAIL_DEFAULT_FROM(String.class),
		MAIL_DEFAULT_REPLY_TO(String.class),

		// maintenance
		MAINTENANCE_MODE(Boolean.class, false),
		MAINTENANCE_MESSAGE(String.class, "Server is in maintenance mode. Please try again later"),

		// announcements
		ANNOUNCEMENT_ID(String.class),
		ANNOUNCEMENT_MESSAGE(String.class),

		// license agreement
		LICENSE_AGREEMENT_TEXT(String.class);

		public final Class<?> type;
		private final Object defaultValue;

		private Key(Class<?> type) {
			this(type, null);
		}

		private Key(Class<?> type, Object defaultValue) {
			this.type = type;
			this.defaultValue = defaultValue;
		}

		public String toString(Object value) {
			checkValue(value);
			if (type == Boolean.class)
				if (value instanceof Boolean)
					return Boolean.toString((boolean) value);
			if (type == Integer.class)
				if (value instanceof Integer)
					return Integer.toString((int) value);
			if (value == null)
				return null;
			return value.toString();
		}

		@SuppressWarnings("unchecked")
		public <T> T parse(String value) {
			checkValue(value);
			if (type == Boolean.class)
				return (T) new Boolean(Boolean.parseBoolean(value));
			if (type == Integer.class && value != null)
				return (T) new Integer(Integer.parseInt(value));
			if (type == String.class && value == null || value.isEmpty())
				return (T) defaultValue;
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
			} else if (type == Integer.class && value != null) {
				try {
					Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(value.toString() + " is not a valid Integer value");
				}
			} else if (value != null && type != value.getClass()) {
				throw new IllegalArgumentException("Value type does not match key type: "
						+ value.getClass().getCanonicalName() + " != " + type.getCanonicalName());
			}
		}

		public boolean isFeature() {
			List<Key> features = Arrays.asList(MESSAGING_ENABLED, TASKS_ENABLED, COMMENTS_ENABLED,
					PUBLIC_REPOSITORY_ENABLED);
			return features.contains(this);
		}

		public boolean isImprint() {
			return this.name().startsWith("IMPRINT_");
		}

		public boolean isMailConfig() {
			return this.name().startsWith("MAIL_");
		}

		public boolean isSearchConfig() {
			return this.name().startsWith("SEARCH_");
		}

		public boolean isPublic() {
			return !isMailConfig() && !isSearchConfig() && this != GLAD_API_KEY && this != REPOSITORY_PATH
					&& this != LIBRARY_PATH;
		}
	}

}
