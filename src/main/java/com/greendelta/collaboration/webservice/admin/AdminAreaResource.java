package com.greendelta.collaboration.webservice.admin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.client.Client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.platform.mail.EmailJob;
import com.greendelta.collaboration.platform.mail.EmailService;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.ReindexService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.SearchConfig;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("admin/area")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAreaResource {

	private final RepositoryService repoService;
	private final ReindexService reindexService;
	private final SearchService searchService;
	private final UserService userService;
	private final GroupService groupService;
	private final TeamService teamService;
	private final SettingsService settingsService;
	private final EmailService emailService;
	private final LibraryService libraryService;

	@Inject
	public AdminAreaResource(RepositoryService repoService, ReindexService reindexService, SearchService searchService,
			UserService service, GroupService groupService, TeamService teamService, SettingsService settingsService,
			EmailService emailService, LibraryService libraryService) {
		this.repoService = repoService;
		this.reindexService = reindexService;
		this.searchService = searchService;
		this.userService = service;
		this.groupService = groupService;
		this.teamService = teamService;
		this.settingsService = settingsService;
		this.emailService = emailService;
		this.libraryService = libraryService;
	}

	@GET
	@Path("count")
	public Response getCounts(@Context HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		result.put("repositories", repoService.getCount(true));
		result.put("users", userService.getCount());
		result.put("groups", groupService.getCount(true));
		result.put("teams", teamService.getCount());
		return Respond.ok(result);
	}

	@PUT
	@Path("reindex")
	public Response reindex() {
		searchService.clearIndex();
		List<Repository> repos = repoService.getAllAccessible();
		for (Repository repo : repos) {
			reindexService.reindex(repo);
		}
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("settings/{key}")
	public Response setSetting(@PathParam("key") Key key) {
		settingsService.set(key, null);
		if (key == Key.LIBRARY_PATH)
			libraryService.resetLibraries();
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("settings/{key}/{value}")
	public Response setSetting(@PathParam("key") Key key, @PathParam("value") String value) {
		settingsService.set(key, value);
		if (key == Key.LIBRARY_PATH)
			libraryService.resetLibraries();
		return Respond.ok(new HashMap<>());
	}

	@POST
	@Path("testMailConfig")
	public Response testMailConfig(String recipient) {
		EmailJob mail = new EmailJob();
		mail.setSubject("Collaboration server test email");
		mail.setRecipient(recipient);
		emailService.send(mail);
		return Respond.ok(new HashMap<>());
	}

	@GET
	@Path("testSearchConfig")
	public Response testSearchConfig() {
		SearchConfig config = settingsService.getSearchConfig();
		try {
			Client client = config.getClient();
			boolean exists = client.admin().indices().prepareExists(config.indexName).execute().actionGet().isExists();
			if (!exists)
				return Respond.error("Index " + config.indexName + " does not exist");
			return Respond.ok(new HashMap<>());
		} catch (UnknownHostException e) {
			return Respond.error("Could not connect to host " + config.host + " on cluster " + config.cluster);
		} catch (Exception e) {
			return Respond.error(e.getMessage());
		}
	}

	@GET
	@Path("testGladConfig")
	public Response testGladConfig() {
		try {
			String gladUrl = settingsService.get(Key.GLAD_URL);
			if (gladUrl == null || gladUrl.isEmpty())
				return Respond.error("No glad url specified");
			InputStream content = new URL(gladUrl + "/search").openStream();
			JsonObject element = new Gson().fromJson(new InputStreamReader(content), JsonObject.class);
			if (element.has("resultInfo") && element.has("data") && element.has("aggregations"))
				return Respond.ok(new HashMap<>());
			return Respond.error("GLAD testcall returned unexpected content: " + new Gson().toJson(element));
		} catch (Exception e) {
			return Respond.error("Could not reach GLAD service");
		}
	}
}
