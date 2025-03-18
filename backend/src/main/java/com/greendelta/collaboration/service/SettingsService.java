package com.greendelta.collaboration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.settings.ImprintSetting;
import com.greendelta.collaboration.model.settings.MailSetting;
import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.model.settings.SearchIndex.SearchIndexType;
import com.greendelta.collaboration.model.settings.SearchSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.Setting;
import com.greendelta.collaboration.model.settings.SettingKey;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.os.OsRestClient;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

@Service
public class SettingsService {

	static final Logger log = LogManager.getLogger(SettingsService.class);
	public final AccessTypes ACCESS = new AccessTypes();
	public final Imprint imprint = new Imprint();
	public final MailConfig mailConfig = new MailConfig();
	public final SearchConfig searchConfig = new SearchConfig();
	public final ServerConfig serverConfig = new ServerConfig();
	private final Dao<Setting> dao;
	private final UserService userService;

	public SettingsService(Dao<Setting> dao, UserService userService) {
		this.dao = dao;
		this.userService = userService;
	}

	public boolean is(SettingKey key) {
		return is(key, null);
	}

	public boolean is(SettingKey key, String owner) {
		var type = SettingType.getFor(key);
		return get(type, owner).is(key);
	}

	public <V> V get(SettingKey key) {
		return get(key, null);
	}

	public <V> V get(SettingKey key, V defaultValue) {
		var type = SettingType.getFor(key);
		return get(type, null).get(key, defaultValue);
	}

	public <V> void set(SettingKey key, V value) {
		var type = SettingType.getFor(key);
		get(type).set(key, value);
	}

	public Map<String, Object> getMap(SettingType type) {
		return get(type).toMap(null, true);
	}

	@SuppressWarnings("unchecked")
	private <T extends SettingKey> Settings<T> get(SettingType type) {
		return (Settings<T>) switch (type) {
			case SERVER_SETTING -> serverConfig;
			case MAIL_SETTING -> mailConfig;
			case SEARCH_SETTING -> searchConfig;
			case SEARCH_INDEX -> searchConfig.indices;
			case IMPRINT_SETTING -> imprint;
			default -> get(type, null);
		};
	}

	private <T extends SettingKey> Settings<T> get(SettingType type, String owner) {
		return get(type, owner, null);
	}

	public <T extends SettingKey> Settings<T> get(SettingType type, String owner, Access access) {
		if (!type.singleton && owner == null)
			throw new IllegalArgumentException("Owner can not be null");
		return new Settings<T>(type, owner, access);
	}

	private <T extends SettingKey> Setting get(SettingType type, T key, String owner) {
		var attributes = new HashMap<String, Object>();
		attributes.put("type", type);
		attributes.put("name", key.name());
		attributes.put("owner", owner);
		return (Setting) dao.getFirstForAttributes(attributes);
	}

	@SuppressWarnings("unchecked")
	public <T extends SettingKey, V> List<V> getValues(T key) {
		var type = SettingType.getFor(key);
		var attributes = new HashMap<String, Object>();
		attributes.put("type", type);
		attributes.put("name", key.name());
		return (List<V>) dao.getForAttributes(attributes).stream()
				.map(Setting::getValue)
				.collect(Collectors.toList());
	}

	private <T extends SettingKey> void set(SettingType type, T key, String owner, Object value) {
		var setting = get(type, key, owner);
		var update = setting != null;
		if (!update) {
			setting = Setting.create(type, key, owner);
		}
		if (value == null) {
			dao.delete(setting);
		} else {
			setting.setValue(value);
			if (update) {
				dao.update(setting);
			} else {
				dao.insert(setting);
			}
		}
	}

	private void move(SettingType type, String owner, String newOwner) {
		find(type, owner).forEach(setting -> {
			dao.delete(setting);
			Setting newSetting = Setting.create(type, setting.getKey(), newOwner);
			dao.insert(newSetting);
		});
	}

	private void delete(SettingType type, String owner) {
		find(type, owner).forEach(setting -> {
			dao.delete(setting);
		});
	}

	private List<Setting> find(SettingType type, String owner) {
		return dao.getForAttributes(Map.of("type", type, "owner", owner));
	}

	public class ServerConfig extends Settings<ServerSetting> {

		private ServerConfig() {
			super(SettingType.SERVER_SETTING);
		}

		public ModelType[] getModelTypes() {
			var value = getFilteredModelTypes();
			List<String> hidden = new ArrayList<String>();
			var subject = SecurityContextHolder.getContext().getAuthentication();
			var isLoggedIn = subject != null && subject.isAuthenticated();
			if (!isLoggedIn) {
				hidden = get(ServerSetting.MODEL_TYPES_HIDDEN, new ArrayList<>());
			}
			var types = new ArrayList<ModelType>();
			for (var i = 0; i < value.size(); i++) {
				var type = value.get(i);
				if (hidden.contains(type))
					continue;
				types.add(ModelType.valueOf(value.get(i)));
			}
			for (var type : ModelType.values()) {
				if (types.contains(type) || hidden.contains(type.name()))
					continue;
				types.add(type);
			}
			return types.toArray(new ModelType[types.size()]);
		}

		private List<String> getFilteredModelTypes() {
			List<String> value = super.get(ServerSetting.MODEL_TYPES_ORDER, new ArrayList<>());
			List<String> defaults = ServerSetting.MODEL_TYPES_ORDER.getDefaultValue();
			return value.stream().filter(v -> !"null".equals(v) && defaults.contains(v)).collect(Collectors.toList());
		}

		@Override
		@SuppressWarnings("unchecked")
		public <V> V get(ServerSetting key, V defaultValue) {
			var value = super.get(key, defaultValue);
			if (key != ServerSetting.MODEL_TYPES_ORDER)
				return value;
			return (V) getFilteredModelTypes();
		}

		public String getServerUrl() {
			String url = super.get(ServerSetting.SERVER_URL);
			if (Strings.nullOrEmpty(url))
				return "";
			url = url.strip();
			if (url.endsWith("/"))
				return url.substring(0, url.length() - 1);
			return url;
		}

	}

	public class Imprint extends Settings<ImprintSetting> {

		private Imprint() {
			super(SettingType.IMPRINT_SETTING);
		}

		public String toEmailFooter() {
			return get(ImprintSetting.COMPANY, "") + ", " + get(ImprintSetting.STREET, "") + ", "
					+ get(ImprintSetting.ZIP_CODE, "") + " " + get(ImprintSetting.CITY, "") + ", "
					+ get(ImprintSetting.COUNTRY, "") + "<br>" + "Companies' Register: "
					+ get(ImprintSetting.REGISTRATION, "") + "<br>" + "Managing Director: "
					+ get(ImprintSetting.CEO, "");
		}

	}

	public class MailConfig extends Settings<MailSetting> {

		private JavaMailSender mailSender;

		private MailConfig() {
			super(SettingType.MAIL_SETTING);
		}

		@Override
		public void set(MailSetting key, Object value) {
			super.set(key, value);
		}

		public JavaMailSender getMailSender() {
			if (mailSender != null)
				return mailSender;
			var mailSender = new JavaMailSenderImpl();
			mailSender.setHost(get(MailSetting.HOST));
			mailSender.setPort(get(MailSetting.PORT));
			String user = get(MailSetting.USER);
			mailSender.setUsername(user);
			mailSender.setPassword(get(MailSetting.PASS));
			var props = mailSender.getJavaMailProperties();
			var proto = get(MailSetting.PROTO);
			var useAuth = Strings.notEmpty(get(MailSetting.USER));
			props.put("mail.transport.protocol", proto);
			props.put("mail." + proto + ".auth", useAuth ? "true" : "false");
			// if (proto.equals("smtps")) {
			// props.put("mail.smtps.ssl.protocols", "TLSv1.2");
			// }
			String from = user != null ? user : get(MailSetting.DEFAULT_FROM);
			if (from != null) {
				try {
					props.put("mail." + proto + ".from", new InternetAddress(from).getAddress());
				} catch (AddressException e) {
					SettingsService.log.error("Error setting 'from'", e);
				}
			}
			if (is(MailSetting.SSL))
				props.put("mail." + proto + ".ssl.enable", "true");
			if (is(MailSetting.TLS))
				props.put("mail." + proto + ".starttls.enable", "true");
			return mailSender;
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

		private RestHighLevelClient restClient;
		private final Map<SearchIndex, SearchClient> clients = new HashMap<>();
		public final Settings<SearchIndex> indices;

		private SearchConfig() {
			super(SettingType.SEARCH_SETTING);
			indices = new Settings<>(SettingType.SEARCH_INDEX);
		}

		@Override
		public void set(SearchSetting key, Object value) {
			super.set(key, value);
			close();
			restClient = null;
			synchronized (clients) {
				clients.clear();
			}
		}

		public boolean isSearchAvailable() {
			if (!SettingsService.this.is(ServerSetting.SEARCH_ENABLED))
				return false;
			try {
				return getRestClient() != null;
			} catch (IOException e) {
				return false;
			}
		}

		public boolean isUsageSearchEnabled() {
			if (!SettingsService.this.is(ServerSetting.USAGE_SEARCH_ENABLED))
				return false;
			try {
				return getRestClient() != null;
			} catch (IOException e) {
				return false;
			}
		}

		public RestHighLevelClient getRestClient() throws IOException {
			if (restClient != null) {
				if (!restClient.ping(RequestOptions.DEFAULT))
					throw new IOException("Could not ping search cluster");
				return restClient;
			}
			String host = get(SearchSetting.HOST);
			int port = get(SearchSetting.PORT);
			String schema = get(SearchSetting.SCHEMA);
			var client = new RestHighLevelClient(
					RestClient.builder(new HttpHost(host, port, schema), new HttpHost(host, port + 1, schema)));
			if (!client.ping(RequestOptions.DEFAULT))
				throw new IOException("Could not ping search cluster");
			this.restClient = client;
			return client;
		}

		public SearchClient getSearchClient(SearchIndex index) {
			if (!SettingsService.this.is(ServerSetting.SEARCH_ENABLED))
				return null;
			if (index.type == SearchIndexType.USAGE && !SettingsService.this.is(ServerSetting.USAGE_SEARCH_ENABLED))
				return null;
			if (index.isPublic && !SettingsService.this.is(ServerSetting.RELEASES_ENABLED))
				return null;
			synchronized (clients) {
				return clients.computeIfAbsent(index, i -> {
					try {
						return new OsRestClient(getRestClient(), indices.get(i));
					} catch (IOException e) {
						SettingsService.log.error("Error getting search client", e);
						return null;
					}
				});
			}
		}

		public void close() {
			if (restClient == null)
				return;
			try {
				restClient.close();
			} catch (IOException e) {
				SettingsService.log.error("Error closing search client", e);
			}
		}

	}

	public class Settings<T extends SettingKey> {

		private final SettingType type;
		private final String owner;
		// if no service is given, use local map
		private final Map<T, Object> local;
		// services can set default values
		private final Map<T, Object> defaults = new HashMap<>();
		private final Access access;

		private Settings(SettingType type) {
			this(type, null, null);
		}

		private Settings(SettingType type, String owner, Access access) {
			this.type = type;
			this.owner = owner;
			this.local = type == null ? new HashMap<>() : null;
			if (type != null && access == null && owner == null) {
				access = ACCESS.ADMIN;
			}
			this.access = access;
		}

		@SuppressWarnings("unchecked")
		public <V> V get(T key) {
			return get(key, (V) defaults.get(key));
		}

		@SuppressWarnings("unchecked")
		public <V> V get(T key, V defaultValue) {
			V value = null;
			if (local != null) {
				value = (V) local.get(key);
			} else {
				Setting setting = SettingsService.this.get(type, key, owner);
				if (setting == null) {
					value = key.getDefaultValue();
				} else {
					value = setting.getValue();
				}
			}
			if (value == null || (defaultValue != null && value instanceof String s && s.trim().isEmpty()))
				return defaultValue;
			return value;
		}

		public boolean is(T key) {
			Boolean value = get(key);
			return value != null && value;
		}

		public void setDefault(T key, Object value) {
			defaults.put(key, value);
		}

		public void set(T key, Object value) {
			if (local != null) {
				local.put(key, value);
			} else {
				checkAccess(owner);
				SettingsService.this.set(type, key, owner, value);
			}
		}

		public void delete(T key) {
			if (local != null) {
				local.remove(key);
			} else {
				checkAccess(owner);
				SettingsService.this.set(type, key, owner, null);
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
				var newOwner = repo.path();
				checkAccess(owner);
				checkAccess(newOwner);
				SettingsService.this.move(type, owner, newOwner);
			}
		}

		private void checkAccess(String owner) {
			if (type == null || access == null || access.allowed(owner))
				return;
			throw Response.forbidden(owner, Permission.SET_SETTINGS);
		}

		public Map<String, Object> toMap() {
			return toMap(null, false);
		}

		public Map<String, Object> toMap(Function<T, Boolean> filter) {
			return toMap(filter, false);
		}

		public Map<String, Object> toPreservedMap() {
			return toMap(null, true);
		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> toMap(Function<T, Boolean> filter, boolean preserveKeys) {
			var map = new HashMap<String, Object>();
			var user = userService.getCurrentUser();
			if (local != null) {
				local.forEach((k, v) -> map.put(preserveKeys ? k.name() : toFieldName(k), v));
				return map;
			}
			for (var key : (T[]) type.enumClass.getEnumConstants()) {
				if (filter != null)
					if (!filter.apply(key))
						continue;
				if (key.isAdminSetting() && !user.isAdmin())
					continue;
				if (user.isAnonymous() && !key.isPublicSetting())
					continue;
				var field = key.name();
				if (!preserveKeys) {
					field = toFieldName(key);
				}
				map.put(field, get(key));
			}
			return map;
		}

		private String toFieldName(SettingKey key) {
			var name = "";
			var nextUpper = false;
			for (var c : key.name().toLowerCase().toCharArray()) {
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

		boolean allowed(String owner);

	}

	public class AccessTypes {

		private AccessTypes() {
		}

		public Access ADMIN = owner -> userService.getCurrentUser().isAdmin();
		public Access DATA_MANAGER = owner -> userService.getCurrentUser().isDataManager();
		public Access LIBRARY_MANAGER = owner -> userService.getCurrentUser().isLibraryManager();
		public Access USER = owner -> userService.getCurrentUser().id != 0;

		public Access TEAM_DATA(Team team) {
			return owner -> {
				if (userService.getCurrentUser().isLibraryManager())
					return true;
				if (team == null)
					return false;
				return team.users.contains(userService.getCurrentUser());
			};
		}

	}

}
