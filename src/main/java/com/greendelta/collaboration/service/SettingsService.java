package com.greendelta.collaboration.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;

public class SettingsService {

	private static final Logger log = LogManager.getLogger(SettingsService.class);
	private static Imprint imprint;
	private static MailConfig mailConfig;
	private static SearchConfig searchConfig;
	private final Dao<Setting> dao;

	@Inject
	public SettingsService(Dao<Setting> dao) {
		this.dao = dao;
	}

	public void set(Key key, Object value) {
		Setting setting = dao.getFirstForAttribute("name", key);
		if (setting == null) {
			setting = new Setting();
			setting.name = key;
			setting.value = key.toString(value);
			dao.insert(setting);
		} else {
			setting.value = key.toString(value);
			dao.update(setting);
		}
		if (key.isImprint() && imprint != null) {
			update(imprint, key, value);
		} else if (key.isMailConfig() && mailConfig != null) {
			update(mailConfig, key, value);
			mailConfig.session = null;
		} else if (key.isSearchConfig() && searchConfig != null) {
			update(searchConfig, key, value);
			searchConfig.close();
			searchConfig.client = null;
			searchConfig.searchClient = null;
		}
	}

	public boolean is(Key key) {
		return get(key);
	}

	public <T> T get(Key key) {
		return get(key, key.getDefaultValue());
	}

	private <T> T get(Key key, T defaultValue) {
		Setting setting = dao.getFirstForAttribute("name", key);
		if (setting == null)
			return defaultValue;
		return key.parse(setting.value);
	}

	public Imprint getImprint() {
		if (imprint == null) {
			for (Key key : Key.values()) {
				if (!key.isImprint())
					continue;
				Object value = get(key, null);
				if (value == null || (value instanceof String && value.toString().isEmpty()))
					continue;
				if (imprint == null) {
					imprint = new Imprint();
				}
				update(imprint, key, value);
			}
		}
		return imprint;
	}

	public MailConfig getMailConfig() {
		if (mailConfig == null) {
			boolean onlyDefaults = true;
			for (Key key : Key.values()) {
				if (!key.isMailConfig())
					continue;
				Object value = get(key, null);
				if (value != null) {
					onlyDefaults = false;
				}
				value = get(key, key.getDefaultValue());
				if (value == null || (value instanceof String && value.toString().isEmpty()))
					continue;
				if (mailConfig == null) {
					mailConfig = new MailConfig();
				}
				update(mailConfig, key, value);
			}
			if (onlyDefaults) {
				mailConfig = null;
			}
		}
		return mailConfig;
	}

	public SearchConfig getSearchConfig() {
		if (searchConfig == null) {
			for (Key key : Key.values()) {
				if (!key.isSearchConfig())
					continue;
				Object value = get(key, key.getDefaultValue());
				if (value == null || value.toString().isEmpty())
					continue;
				if (searchConfig == null) {
					searchConfig = new SearchConfig();
				}
				update(searchConfig, key, value);
			}
		}
		return searchConfig;
	}

	private void update(Object object, Key key, Object value) {
		String field = getFieldName(key);
		if (value != null) {
			value = key.parse(value.toString());
		}
		try {
			object.getClass().getDeclaredField(field).set(object, value);
		} catch (Exception e) {
			log.error("Error setting field", e);
		}
	}

	private String getFieldName(Key key) {
		String name = "";
		boolean nextUpper = false;
		for (char c : key.name().toLowerCase().substring(key.name().indexOf('_') + 1).toCharArray()) {
			if (nextUpper) {
				c = Character.toUpperCase(c);
				nextUpper = false;
			}
			if (c == '_') {
				nextUpper = true;
			} else {
				name += c;
			}
		}
		return name;
	}

	public List<Setting> getAll() {
		List<Setting> settings = new ArrayList<>();
		for (Key key : Key.values()) {
			Setting setting = dao.getFirstForAttribute("name", key);
			if (setting == null) {
				setting = new Setting();
				setting.name = key;
				setting.value = key.toString(key.getDefaultValue());
			}
			settings.add(setting);
		}
		return settings;
	}

	public class Imprint {

		public String company;
		public String ceo;
		public String street;
		public String zipCode;
		public String city;
		public String country;
		public String phone;
		public String fax;
		public String email;
		public String website;
		public String registration;
		public String vat;

		private Imprint() {

		}

	}

	public class MailConfig {

		public String user;
		public String pass;
		public String proto;
		public String host;
		public Integer port;
		public Boolean ssl;
		public Boolean tls;
		public String defaultFrom;
		public String defaultReplyTo;
		private Session session;

		private MailConfig() {

		}

		public Session getSession() {
			if (session == null) {
				boolean useAuth = user != null && !user.isEmpty();
				Properties props = new Properties();
				props.put("mail." + proto + ".auth", useAuth ? "true" : "false");
				props.put("mail." + proto + ".host", host);
				props.put("mail." + proto + ".port", port);
				try {
					props.put("mail." + proto + ".from", new InternetAddress(defaultFrom).getAddress());
				} catch (AddressException e) {
					log.error("Error setting 'from'", e);
				}
				if (ssl != null && ssl)
					props.put("mail." + proto + ".ssl.enable", "true");
				if (tls != null && tls)
					props.put("mail." + proto + ".starttls.enable", "true");
				session = Session.getInstance(props);
			}
			return session;
		}

		public boolean isValid() {
			if (Strings.isNullOrEmpty(defaultFrom) || Strings.isNullOrEmpty(proto) || Strings.isNullOrEmpty(host) || port == null || port == 0)
				return false;
			return true;
		}
		
	}

	public class SearchConfig {

		public String cluster;
		public String host;
		public int port;
		public String indexName;
		private Client client;
		private SearchClient searchClient;

		public Client getClient() throws UnknownHostException {
			if (client == null) {
				Builder settingsBuilder = Settings.builder()
						.put("cluster.name", cluster);
				Settings settings = settingsBuilder.build();
				TransportClient client = new PreBuiltTransportClient(settings);
				try {
				  
					client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port + 100));
				} catch (UnknownHostException e) {
					throw e;
				}
				this.client = client;
			}
			return client;
		}

		public SearchClient getSearchClient() {
			if (searchClient == null) {
				try {
					searchClient = new EsClient(getClient(), indexName, "dataset");
				} catch (Exception e) {
					log.error("Error getting search client", e);
				}
			}
			return searchClient;
		}

		public void close() {
			if (client == null)
				return;
			client.close();
		}

	}

}
