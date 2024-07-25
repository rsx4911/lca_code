package com.greendelta.collaboration.controller.admin;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.opensearch.client.RequestOptions;
import org.opensearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.SearchIndex;
import com.greendelta.collaboration.model.settings.SearchSetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.settings.SettingType;
import com.greendelta.collaboration.service.AnnouncementService;
import com.greendelta.collaboration.service.EmailService;
import com.greendelta.collaboration.service.EmailService.EmailJob;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.Repository.RepositoryPath;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.IndexService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Maps;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("ws/admin/area")
public class AdminAreaController {

	private final RepositoryService repoService;
	private final GroupService groupService;
	private final UserService userService;
	private final TeamService teamService;
	private final IndexService indexService;
	private final SettingsService settings;
	private final EmailService emailService;
	private final AnnouncementService announcementService;

	public AdminAreaController(RepositoryService repoService, GroupService groupService, UserService userService,
			TeamService teamService, IndexService indexingService, SettingsService settings,
			EmailService emailService, AnnouncementService announcementService) {
		this.repoService = repoService;
		this.groupService = groupService;
		this.userService = userService;
		this.teamService = teamService;
		this.indexService = indexingService;
		this.settings = settings;
		this.emailService = emailService;
		this.announcementService = announcementService;
	}

	@GetMapping("count")
	public Map<String, Object> getCounts(@Autowired HttpServletRequest request) {
		var result = new HashMap<String, Object>();
		result.put("repositories", repoService.getCount());
		result.put("groups", groupService.getAllAccessible().stream()
				.filter(Predicate.not(groupService::isUserNamespace))
				.count());
		result.put("users", userService.getCount());
		result.put("teams", teamService.getCount());
		return result;
	}

	@GetMapping("testSearchConfig")
	public void testSearchConfig() {
		if (!settings.searchConfig.isSearchAvailable())
			throw Response.unavailable("Search feature not enabled or search cluster unavailable");
		var config = settings.searchConfig;
		try {
			var client = config.getClient();
			String indexName = config.indices.get(SearchIndex.PRIVATE);
			var exists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
			if (!exists)
				throw Response.error("Index " + indexName + " does not exist");
		} catch (UnknownHostException | ConnectException e) {
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
		var info = settings.serverConfig.toMap(setting -> relevantSettings.contains(setting));
		info.put("repositoriesOrder", repoService.getRepositoryOrder());
		info.put("repositoriesHidden", repoService.getHiddenRepositories());
		info.put("indexingStatus", Maps.of(indexService.getIndexingStatus()));
		return info;
	}

	@PutMapping("clearIndex")
	public void clearIndex() {
		if (!settings.searchConfig.isSearchAvailable())
			throw Response.unavailable("Search feature not enabled or search cluster unavailable");
		indexService.clearIndexAsync();
	}

	@PutMapping("reindex")
	public void reindex() {
		if (!settings.searchConfig.isSearchAvailable())
			throw Response.unavailable("Search feature not enabled or search cluster unavailable");
		try (var repos = repoService.getAllAccessible()) {
			var paths = repos.stream()
					.map(Repository::path)
					.map(RepositoryPath::of)
					.collect(Collectors.toList());
			indexService.reindexAllAsync(paths);
		}
	}

	@PutMapping("reindex/{group}/{repository}")
	public void reindex(
			@PathVariable("group") String group,
			@PathVariable("repository") String repository) {
		if (!settings.searchConfig.isSearchAvailable())
			throw Response.unavailable("Search feature not enabled or search cluster unavailable");
		indexService.reindexAsync(RepositoryPath.of(group, repository));
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
						type -> settings.getMap(type)));
	}

	@PutMapping("settings")
	public void setSetting(@RequestBody Map<String, String> data) {
		var type = SettingType.valueOf(data.get("type"));
		var key = type.getSettingKey(data.get("key"));
		var value = data.get("value");
		if (value != null && value.trim().isEmpty()) {
			value = null;
		}
		settings.set(key, value);
	}

}
