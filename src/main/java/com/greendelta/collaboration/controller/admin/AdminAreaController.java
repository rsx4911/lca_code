package com.greendelta.collaboration.controller.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.opensearch.client.RequestOptions;
import org.opensearch.client.indices.GetIndexRequest;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.SearchSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.AnnouncementService;
import com.greendelta.collaboration.service.EmailService;
import com.greendelta.collaboration.service.EmailService.EmailJob;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.SearchConfig;
import com.greendelta.collaboration.service.search.SearchService;

@RestController
@RequestMapping("ws/admin/area")
public class AdminAreaController {

	// request listener is not counting calls to the server info, to avoid
	// counting itself. To avoid hiding this information in the RequestListener
	// task, the path is specified here (avoid error if changes on the path
	// occur)
	public static final String SERVER_INFO_PATH = "/ws/admin/area/serverInfo";
	private final RepositoryService repoService;
	private final SearchService searchService;
	private final SettingsService settingsService;
	private final EmailService emailService;
	private final LibraryService libraryService;
	private final AnnouncementService announcementService;

	@Autowired
	public AdminAreaController(RepositoryService repoService, SearchService searchService,
			SettingsService settingsService, EmailService emailService, LibraryService libraryService,
			AnnouncementService announcementService) {
		this.repoService = repoService;
		this.searchService = searchService;
		this.settingsService = settingsService;
		this.emailService = emailService;
		this.libraryService = libraryService;
		this.announcementService = announcementService;
	}

	@GetMapping("testGladConfig")
	public void testGladConfig() {
		try {
			var config = settingsService.serverConfig;
			String gladUrl = config.get(ServerSetting.GLAD_URL);
			if (gladUrl == null || gladUrl.isEmpty())
				throw Response.error("No glad url specified");
			String gladHeaderField = config.get(ServerSetting.GLAD_API_KEY_HEADER);
			String gladHeaderValue = config.get(ServerSetting.GLAD_API_KEY);
			var result = get(gladUrl, gladHeaderField, gladHeaderValue);
			if (!result.contains("\"resultInfo\":") || !result.contains("\"data\":")
					|| !result.contains("\"aggregations\":"))
				throw Response.error("GLAD testcall returned unexpected content: " + result);
		} catch (Exception e) {
			throw Response.error("Could not reach GLAD service");
		}
	}

	@GetMapping("testSearchConfig")
	public void testSearchConfig() {
		SearchConfig config = settingsService.searchConfig;
		try {
			var client = config.getClient();
			String indexName = config.get(SearchSetting.INDEX_NAME);
			var exists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
			if (!exists)
				throw Response.error("Index " + indexName + " does not exist");
		} catch (UnknownHostException e) {
			String host = config.get(SearchSetting.HOST);
			throw Response.error("Could not connect to host " + host);
		} catch (Exception e) {
			throw Response.error(e.getMessage());
		}
	}

	@GetMapping("testMailConfig/{email}")
	public void testMailConfig(@PathVariable("email") String recipient) {
		var mail = new EmailJob();
		mail.subject = "Collaboration server test email";
		mail.recipient = recipient;
		emailService.send(mail);
	}

	@GetMapping("serverInfo")
	public Map<String, Object> getServerInfo() {
		var relevantSettings = Arrays.asList(new ServerSetting[] {
				ServerSetting.MAINTENANCE_MODE, ServerSetting.MAINTENANCE_MESSAGE, ServerSetting.ANNOUNCEMENT_MESSAGE,
				ServerSetting.LICENSE_AGREEMENT_TEXT, ServerSetting.HOME_TITLE, ServerSetting.HOME_TEXT,
				ServerSetting.MODEL_TYPES_ORDER, ServerSetting.MODEL_TYPES_HIDDEN
		});
		var info = settingsService.serverConfig.toMap(setting -> relevantSettings.contains(setting));
		// info.put("openWebServiceRequests",
		// RequestListener.openRequest.get());
		info.put("repositoriesOrder", repoService.getPublicRepositoryOrder());
		info.put("repositoriesHidden", repoService.getPublicHiddenRepositories());
		return info;
	}

	@PutMapping("clearIndex")
	public void clearIndex() {
		searchService.clearIndex();
	}

	@PutMapping("reindex")
	public void reindex() {
		searchService.clearIndex();
		try (var repositories = repoService.getAllAccessible()) {
			repositories.forEach(repo -> searchService.index(repo));
		}
	}

	@PutMapping("reindex/{group}/{repository}")
	public void reindex(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository) {
		try (var repo = repoService.get(group, repository)) {
			searchService.update(repo);
		}
	}

	@PutMapping("announce")
	public void announce(@RequestBody String message) {
		announcementService.announce(message);
	}

	@PutMapping("clearAnnouncement")
	public void clearAnnouncement() {
		announcementService.clear();
	}

	@GetMapping("settings")
	public Map<String, Object> getSettings() {
		return Arrays.asList(SettingType.values()).stream()
				.filter(type -> type.singleton)
				.collect(Collectors.toMap(
						type -> type.name(),
						type -> settingsService.getMap(type)));
	}

	@PutMapping("settings")
	public void setSetting(@RequestBody Map<String, String> data) {
		var type = SettingType.valueOf(data.get("type"));
		var key = type.getSettingKey(data.get("key"));
		var value = data.get("value");
		if (value != null && value.trim().isEmpty()) {
			value = null;
		}
		settingsService.set(key, value);
		if (key == ServerSetting.LIBRARY_PATH) {
			libraryService.resetLibraries();
		}
	}

	private String get(String gladBaseUrl, String headerField, String headerValue) throws Exception {
		var object = new URL(gladBaseUrl + "/search");
		var con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		con.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
		con.setRequestMethod("GET");
		if (!Strings.nullOrEmpty(headerField) && !Strings.nullOrEmpty(headerValue)) {
			con.addRequestProperty(headerField, headerValue);
		}
		var status = con.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK)
			return null;
		var s = con.getInputStream();
		if (s == null)
			return null;
		var sb = new StringBuilder();
		var br = new BufferedReader(new InputStreamReader(s, "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}

}
