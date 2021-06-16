package com.greendelta.collaboration.webservice.admin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.settings.SearchSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingKey;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.platform.mail.EmailJob;
import com.greendelta.collaboration.platform.mail.EmailService;
import com.greendelta.collaboration.platform.servlet.RequestListener;
import com.greendelta.collaboration.service.AnnouncementService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.SearchConfig;
import com.greendelta.collaboration.service.SettingsService.ServerConfig;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.webservice.Respond;

@Path("admin/area")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAreaResource {

	// request listener is not counting calls to the server info, to avoid
	// counting itself. To avoid hiding this information in the RequestListener
	// task, the path is specified here (avoid error if changes on the path
	// occur)
	public static final String SERVER_INFO_PATH = "admin/area/serverInfo";
	private final RepositoryService repoService;
	private final SearchService searchService;
	private final SettingsService settingsService;
	private final EmailService emailService;
	private final LibraryService libraryService;
	private final AnnouncementService announcementService;

	@Inject
	public AdminAreaResource(RepositoryService repoService, SearchService searchService,
			SettingsService settingsService, EmailService emailService, LibraryService libraryService,
			AnnouncementService announcementService) {
		this.repoService = repoService;
		this.searchService = searchService;
		this.settingsService = settingsService;
		this.emailService = emailService;
		this.libraryService = libraryService;
		this.announcementService = announcementService;
	}

	@GET
	@Path("testGladConfig")
	public Response testGladConfig() {
		try {
			ServerConfig config = settingsService.serverConfig;
			String gladUrl = config.get(ServerSetting.GLAD_URL);
			if (gladUrl == null || gladUrl.isEmpty())
				return Respond.error("No glad url specified");
			String gladHeaderField = config.get(ServerSetting.GLAD_API_KEY_HEADER);
			String gladHeaderValue = config.get(ServerSetting.GLAD_API_KEY);
			String result = get(gladUrl, gladHeaderField, gladHeaderValue);
			if (result.contains("\"resultInfo\":") && result.contains("\"data\":")
					&& result.contains("\"aggregations\":"))
				return Respond.ok(new HashMap<>());
			return Respond.error("GLAD testcall returned unexpected content: " + result);
		} catch (Exception e) {
			return Respond.error("Could not reach GLAD service");
		}
	}

	@GET
	@Path("testSearchConfig")
	public Response testSearchConfig() {
		SearchConfig config = settingsService.searchConfig;
		try {
			RestHighLevelClient client = config.getClient();
			String indexName = config.get(SearchSetting.INDEX_NAME);
			boolean exists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
			if (!exists)
				return Respond.error("Index " + indexName + " does not exist");
			return Respond.ok(new HashMap<>());
		} catch (UnknownHostException e) {
			String host = config.get(SearchSetting.HOST);
			return Respond.error("Could not connect to host " + host);
		} catch (Exception e) {
			return Respond.error(e.getMessage());
		}
	}

	@GET
	@Path("testMailConfig/{email}")
	public Response testMailConfig(@PathParam("email") String recipient) {
		EmailJob mail = new EmailJob();
		mail.setSubject("Collaboration server test email");
		mail.setRecipient(recipient);
		emailService.send(mail);
		return Respond.ok(new HashMap<>());
	}

	@GET
	@Path("serverInfo")
	public Response getServerInfo() {
		List<ServerSetting> relevantSettings = Arrays.asList(new ServerSetting[] {
				ServerSetting.MAINTENANCE_MODE, ServerSetting.MAINTENANCE_MESSAGE, ServerSetting.ANNOUNCEMENT_MESSAGE,
				ServerSetting.LICENSE_AGREEMENT_TEXT, ServerSetting.HOME_TITLE, ServerSetting.HOME_TEXT,
				ServerSetting.MODEL_TYPES_ORDER, ServerSetting.MODEL_TYPES_HIDDEN
		});
		Map<String, Object> info = settingsService.serverConfig.toMap(setting -> relevantSettings.contains(setting));
		info.put("openWebServiceRequests", RequestListener.getInstance().openRequest);
		info.put("repositoriesOrder", repoService.getPublicRepositoryOrder());
		info.put("repositoriesHidden", repoService.getPublicHiddenRepositories());
		return Respond.ok(info);
	}

	@PUT
	@Path("clearIndex")
	public Response clearIndex() {
		searchService.clearIndex();
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("reindex")
	public Response reindex() {
		searchService.clearIndex();
		List<Repository> repos = repoService.getAllAccessible();
		for (Repository repo : repos) {
			searchService.index(repo);
		}
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("reindex/{group}/{repository}")
	public Response reindex(@PathParam("group") String group, @PathParam("repository") String repository) {
		Repository repo = repoService.get(group, repository);
		searchService.remove(repo);
		searchService.index(repo);
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("announce")
	public Response announce(String message) {
		announcementService.announce(message);
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("clearAnnouncement")
	public Response clearAnnouncement() {
		announcementService.clear();
		return Respond.ok(new HashMap<>());
	}

	@GET
	@Path("settings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSettings() {
		Map<String, Object> settings = new HashMap<>();
		for (SettingType type : SettingType.values()) {
			if (!type.singleton)
				continue;
			settings.put(type.name(), settingsService.getMap(type));
		}
		return Respond.ok(settings);
	}

	@PUT
	@Path("settings")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setSetting(Map<String, String> data) {
		SettingType type = SettingType.valueOf(data.get("type"));
		SettingKey key = type.getSettingKey(data.get("key"));
		String value = data.get("value");
		if (value != null && value.trim().isEmpty()) {
			value = null;
		}
		settingsService.set(key, value);
		if (key == ServerSetting.LIBRARY_PATH)
			libraryService.resetLibraries();
		return Respond.ok(new HashMap<>());
	}

	private String get(String gladBaseUrl, String headerField, String headerValue) throws Exception {
		URL object = new URL(gladBaseUrl + "/search");
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("GET");
		if (!Strings.nullOrEmpty(headerField) && !Strings.nullOrEmpty(headerValue)) {
			con.addRequestProperty(headerField, headerValue);
		}
		int status = con.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK)
			return null;
		InputStream s = con.getInputStream();
		if (s == null)
			return null;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(s, "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}

}
