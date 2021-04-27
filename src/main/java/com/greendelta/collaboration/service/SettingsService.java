package com.greendelta.collaboration.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.openlca.cloud.error.UnauthorizedAccessException;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.collaboration.model.settings.ImprintSetting;
import com.greendelta.collaboration.model.settings.MailSetting;
import com.greendelta.collaboration.model.settings.SearchSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.Setting;
import com.greendelta.collaboration.model.settings.SettingKey;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;

public class SettingsService {

	static final Logger log = LogManager.getLogger(SettingsService.class);
	public final Imprint imprint = new Imprint();
	public final MailConfig mailConfig = new MailConfig();
	public final SearchConfig searchConfig = new SearchConfig();
	public final ServerConfig serverConfig = new ServerConfig();
	private final Dao<Setting<?>> dao;
	private final Provider<Subject> subjectProvider;

	@Inject
	public SettingsService(Dao<Setting<?>> dao, Provider<Subject> subjectProvider) {
		this.dao = dao;
		this.subjectProvider = subjectProvider;
	}

	public <T extends SettingKey> Settings<T> create() {
		return new Settings<T>();
	}

	public boolean is(SettingKey key) {
		return is(key, null);
	}

	public boolean is(SettingKey key, String owner) {
		SettingType type = SettingType.getFor(key);
		return get(type, owner).is(key);
	}

	public <V> V get(SettingKey key) {
		return get(key, null);
	}

	public <V> V get(SettingKey key, V defaultValue) {
		return get(key, null, defaultValue);
	}

	public <V> V get(SettingKey key, String owner) {
		return get(key, owner, null);
	}

	public <V> V get(SettingKey key, String owner, V defaultValue) {
		SettingType type = SettingType.getFor(key);
		return get(type, owner).get(key, defaultValue);
	}

	public <V> void set(SettingKey key, V value) {
		SettingType type = SettingType.getFor(key);
		get(type).set(key, value);
	}

	public Map<String, Object> getMap(SettingType type) {
		return get(type).toMap(null, true);
	}

	@SuppressWarnings("unchecked")
	private <T extends SettingKey> Settings<T> get(SettingType type) {
		switch (type) {
		case SERVER_SETTING:
			return (Settings<T>) serverConfig;
		case MAIL_SETTING:
			return (Settings<T>) mailConfig;
		case SEARCH_SETTING:
			return (Settings<T>) searchConfig;
		case IMPRINT_SETTING:
			return (Settings<T>) imprint;
		default:
			return get(type, null);
		}
	}

	private <T extends SettingKey> Settings<T> get(SettingType type, String owner) {
		return get(type, owner, null);
	}

	public <T extends SettingKey> Settings<T> get(SettingType type, String owner, Access access) {
		if (!type.singleton && owner == null)
			throw new IllegalArgumentException("Owner can not be null");
		return new Settings<T>(type, owner, access);
	}

	@SuppressWarnings("unchecked")
	private <T extends SettingKey> Setting<T> get(SettingType type, T key, String owner) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("type", type);
		attributes.put("name", key.name());
		attributes.put("owner", owner);
		return (Setting<T>) dao.getFirstForAttributes(attributes);
	}

	private <T extends SettingKey> void set(SettingType type, T key, String owner, Object value) {
		Setting<T> setting = get(type, key, owner);
		boolean update = setting != null;
		if (!update) {
			setting = Setting.create(type, key, owner);
		}
		setting.setValue(value);
		if (update) {
			dao.update(setting);
		} else {
			dao.insert(setting);
		}
	}

	private void move(SettingType type, String owner, String newOwner) {
		List<Setting<?>> settings = find(type, owner);
		for (Setting<?> setting : settings) {
			dao.delete(setting);
			Setting<?> newSetting = Setting.create(type, setting.getKey(), newOwner);
			dao.insert(newSetting);
		}
	}

	private void delete(SettingType type, String owner) {
		List<Setting<?>> settings = find(type, owner);
		for (Setting<?> setting : settings) {
			dao.delete(setting);
		}
	}

	private List<Setting<?>> find(SettingType type, String owner) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("type", type);
		attributes.put("owner", owner);
		return dao.getForAttributes(attributes);
	}

	public class ServerConfig extends Settings<ServerSetting> {

		private ServerConfig() {
			super(SettingType.SERVER_SETTING);
		}

		public ModelType[] getModelTypes() {
			List<String> value = get(ServerSetting.MODEL_TYPES_ORDER);
			List<String> hidden = new ArrayList<>();
			Subject subject = subjectProvider.get();
			boolean isLoggedIn = subject != null && subject.isAuthenticated();
			if (!isLoggedIn) {
				hidden = get(ServerSetting.MODEL_TYPES_HIDDEN);
			}
			List<ModelType> types = new ArrayList<>();
			for (int i = 0; i < value.size(); i++) {
				if (hidden.contains(value.get(i)))
					continue;
				types.add(ModelType.valueOf(value.get(i)));
			}
			for (ModelType type : ModelType.values()) {
				if (!type.isCategorized() || types.contains(type) || hidden.contains(type.name()))
					continue;
				types.add(type);
			}
			return types.toArray(new ModelType[types.size()]);
		}

	}

	public class Imprint extends Settings<ImprintSetting> {

		private Imprint() {
			super(SettingType.IMPRINT_SETTING);
		}

		public String toEmailFooter() {
			return get(ImprintSetting.COMPANY, "") + ", " + get(ImprintSetting.STREET, "") + ", "
					+ get(ImprintSetting.ZIP_CODE, "")
					+ " " + get(ImprintSetting.CITY, "") + ", " + get(ImprintSetting.COUNTRY, "") + "<br>"
					+ "Companies' Register: " + get(ImprintSetting.REGISTRATION, "") + "<br>"
					+ "Managing Director: " + get(ImprintSetting.CEO, "");
		}

	}

	public class MailConfig extends Settings<MailSetting> {

		private Session session;

		private MailConfig() {
			super(SettingType.MAIL_SETTING);
		}

		@Override
		public void set(MailSetting key, Object value) {
			super.set(key, value);
			session = null;
		}

		public Session getSession() {
			if (session != null)
				return session;
			String proto = get(MailSetting.PROTO);
			boolean useAuth = Strings.notEmpty(get(MailSetting.USER));
			Properties props = new Properties();
			props.put("mail." + proto + ".auth", useAuth ? "true" : "false");
			props.put("mail." + proto + ".host", get(MailSetting.HOST));
			props.put("mail." + proto + ".port", get(MailSetting.PORT));
			try {
				props.put("mail." + proto + ".from",
						new InternetAddress(get(MailSetting.DEFAULT_FROM)).getAddress());
			} catch (AddressException e) {
				SettingsService.log.error("Error setting 'from'", e);
			}
			if (is(MailSetting.SSL))
				props.put("mail." + proto + ".ssl.enable", "true");
			if (is(MailSetting.TLS))
				props.put("mail." + proto + ".starttls.enable", "true");
			session = Session.getInstance(props);
			return session;
		}

		public boolean isValid() {
			int port = get(MailSetting.PORT);
			if (Strings.nullOrEmpty(get(MailSetting.DEFAULT_FROM)) || Strings.nullOrEmpty(get(MailSetting.PROTO))
					|| Strings.nullOrEmpty(get(MailSetting.HOST)) || port == 0)
				return false;
			return true;
		}

	}

	public class SearchConfig extends Settings<SearchSetting> {

		private Client client;
		private SearchClient searchClient;

		private SearchConfig() {
			super(SettingType.SEARCH_SETTING);
		}

		@Override
		public void set(SearchSetting key, Object value) {
			super.set(key, value);
			close();
			client = null;
			searchClient = null;
		}

		public Client getClient() throws UnknownHostException {
			if (client != null)
				return client;
			String cluster = get(SearchSetting.CLUSTER);
			Builder builder = org.elasticsearch.common.settings.Settings.builder().put("cluster.name", cluster);
			org.elasticsearch.common.settings.Settings settings = builder.build();
			TransportClient client = new PreBuiltTransportClient(settings);
			try {
				String host = get(SearchSetting.HOST);
				int port = get(SearchSetting.PORT);
				client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port + 100));
			} catch (UnknownHostException e) {
				throw e;
			}
			this.client = client;
			return client;
		}

		public SearchClient getSearchClient() {
			if (searchClient != null)
				return searchClient;
			try {
				searchClient = new EsClient(getClient(), get(SearchSetting.INDEX_NAME), "dataset");
			} catch (Exception e) {
				SettingsService.log.error("Error getting search client", e);
			}
			return searchClient;
		}

		public void close() {
			if (client == null)
				return;
			client.close();
		}

	}

	public class Settings<T extends SettingKey> {

		private final SettingType type;
		private final String owner;
		// if no service is given, use local map
		private final Map<T, Object> local;
		private final Access access;

		private Settings() {
			this(null, null, null);
		}

		private Settings(SettingType type) {
			this(type, null, null);
		}

		private Settings(SettingType type, String owner, Access access) {
			this.type = type;
			this.owner = owner;
			this.local = type == null ? new HashMap<>() : null;
			if (type != null && access == null && owner == null) {
				access = new AdminAccess();
			}
			this.access = access;
		}

		public <V> V get(T key) {
			return get(key, null);
		}

		@SuppressWarnings("unchecked")
		public <V> V get(T key, V defaultValue) {
			V value = null;
			if (local != null) {
				value = (V) local.get(key);
			} else {
				Setting<T> setting = SettingsService.this.get(type, key, owner);
				if (setting == null) {
					value = key.getDefaultValue();
				} else {
					value = setting.getValue();
				}
			}
			if (value == null)
				return defaultValue;
			return value;
		}

		public boolean is(T key) {
			Boolean value = get(key);
			return value != null && value;
		}

		public void set(T key, Object value) {
			if (local != null) {
				local.put(key, value);
			} else {
				checkAccess(owner);
				SettingsService.this.set(type, key, owner, value);
			}
		}

		public void delete() {
			if (local != null) {
				local.clear();
			} else {
				checkAccess(owner);
				SettingsService.this.delete(type, owner);
			}
		}

		public void move(Repository repo) {
			if (local == null) {
				String newOwner = repo.toId();
				checkAccess(owner);
				checkAccess(newOwner);
				SettingsService.this.move(type, owner, newOwner);
			}
		}

		private void checkAccess(String owner) {
			if (type == null || access == null || access.allowed(owner))
				return;
			if (owner == null)
				throw new AuthorizationException();
			throw new UnauthorizedAccessException(owner, "SET_SETTING");
		}

		public Map<String, Object> toMap() {
			return toMap(null, false);
		}

		public Map<String, Object> toMap(Function<T, Boolean> filter) {
			return toMap(filter, false);
		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> toMap(Function<T, Boolean> filter, boolean preserveKeys) {
			Map<String, Object> map = new HashMap<>();
			if (local != null) {
				local.forEach((k, v) -> map.put(preserveKeys ? k.name() : toFieldName(k), v));
				return map;
			}
			for (T key : (T[]) type.enumClass.getEnumConstants()) {
				if (filter != null)
					if (!filter.apply(key))
						continue;
				String field = key.name();
				if (!preserveKeys) {
					field = toFieldName(key);
				}
				map.put(field, get(key));
			}
			return map;
		}

		private String toFieldName(SettingKey key) {
			String name = "";
			boolean nextUpper = false;
			for (char c : key.name().toLowerCase().toCharArray()) {
				if (c == '_') {
					nextUpper = true;
				} else {
					name += nextUpper ? Character.toUpperCase(c) : c;
					nextUpper = false;
				}
			}
			return name;
		}

	}

	public interface Access {

		boolean allowed(String groupOrRepo);

	}

	private class AdminAccess implements Access {

		@Override
		public boolean allowed(String groupOrRepo) {
			Subject subject = subjectProvider.get();
			return subject != null && subject.hasRole("admin");
		}

	}

}
