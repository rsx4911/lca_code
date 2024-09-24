package com.greendelta.collaboration.controller.datamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.jsonld.Enums;
import org.openlca.util.Strings;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.search.ModellingApproach;
import com.greendelta.collaboration.service.ReleaseService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.ServerConfig;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Maps;

@RestController
@RequestMapping("ws/datamanager/glad")
public class GladController {

	private static final Logger log = LogManager.getLogger(GladController.class);
	private static final List<String> GLAD_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "processType", "supportedNomenclatures", "aggregationType", "multifunctionalModeling", "name",
			"categories", "location", "completeness", "technology", "copyrightHolder", "license", "contact",
			"description", "dataSetUrl", "format", "validFrom", "validFromYear", "validUntil", "validUntilYear",
			"copyrightProtected", "free", "dataprovider", "reviewers", "reviewType", "longitude", "latitude",
			"publiclyAccessible"));
	private final RepositoryService repoService;
	private final ReleaseService releaseService;
	private final SettingsService settings;

	public GladController(RepositoryService repoService, ReleaseService releaseService, SettingsService settings) {
		this.repoService = repoService;
		this.releaseService = releaseService;
		this.settings = settings;
	}

	@GetMapping("list")
	public Set<String> list() {
		var api = new GladApi(settings.serverConfig);
		return api.listEntries().stream()
				.map(Entry::repositoryPath)
				.collect(Collectors.toSet());
	}

	@PutMapping("push/{group}/{name}/{commitId}")
	public void push(
			@PathVariable String group,
			@PathVariable String name,
			@PathVariable String commitId,
			@RequestBody Set<String> paths) {
		var repo = repoService.get(group, name);
		if (repo == null)
			throw Response.notFound("No repository with id " + group + "/" + name + " found");
		new Thread(() -> {
			try {
				var api = new GladApi(settings.serverConfig);
				var isReleased = releaseService.isReleased(repo.path(), commitId);
				var existing = api.listEntries(repo.path()).stream()
						.map(Entry::refId)
						.collect(Collectors.toSet());
				paths.stream().forEach(path -> {
					repo.references.find().path(path).commit(commitId).iterate(ref -> {
						existing.remove(ref.refId);
						if (isReleased) {
							api.putReleased(repo, ref, repo.path());
						} else {
							api.putUnreleased(repo, ref, repo.path());
						}
					});
				});
				// delete remaining old entries
				existing.forEach(api::delete);
			} finally {
				repo.close();
			}
		}).start();
	}

	@DeleteMapping("clear")
	public void deleteAllData() {
		new Thread(() -> {
			var api = new GladApi(settings.serverConfig);
			api.listEntries().stream()
					.map(Entry::refId)
					.forEach(api::delete);
		}).start();
	}

	@DeleteMapping("clear/{group}/{name}")
	public void deleteData(
			@PathVariable String group,
			@PathVariable String name) {
		new Thread(() -> {
			var api = new GladApi(settings.serverConfig);
			api.listEntries(group + "/" + name).stream()
					.map(Entry::refId)
					.forEach(api::delete);
		}).start();
	}

	@GetMapping("testConfig")
	public void testConfig() {
		try {
			new GladApi(settings.serverConfig).test();
		} catch (Exception e) {
			throw Response.error("Could not reach GLAD service");
		}
	}

	private static class GladApi {

		private final String baseUrl;
		private final String apiKey;
		private final String dataprovider;
		private final String serverUrl;

		public GladApi(ServerConfig config) {
			this(config.get(ServerSetting.GLAD_URL),
					config.get(ServerSetting.GLAD_API_KEY),
					config.get(ServerSetting.GLAD_DATAPROVIDER),
					config.getServerUrl());
		}

		public GladApi(String baseUrl, String apiKey, String dataprovider, String serverUrl) {
			this.baseUrl = baseUrl;
			this.apiKey = apiKey;
			this.dataprovider = dataprovider;
			this.serverUrl = serverUrl;
			if (Strings.nullOrEmpty(baseUrl))
				throw Response.unavailable("No GLAD service url specified");
			if (Strings.nullOrEmpty(apiKey))
				throw Response.unavailable("No GLAD service api-key specified");
			if (Strings.nullOrEmpty(dataprovider))
				throw Response.unavailable("No GLAD service dataprovider name specified");
			if (Strings.nullOrEmpty(serverUrl))
				throw Response.unavailable("No Collaboration Server url specified");
		}

		public void test() {
			var result = send("GET", "search", null);
			if (result == null
					|| !result.contains("\"resultInfo\":")
					|| !result.contains("\"data\":")
					|| !result.contains("\"aggregations\":"))
				throw Response.error("GLAD testcall returned unexpected content: " + result);
		}

		public List<Entry> listEntries(String path) {
			return listEntries().stream()
					.filter(e -> e.repositoryPath().equals(path))
					.collect(Collectors.toList());
		}

		public List<Entry> listEntries() {
			var entries = new ArrayList<Entry>();
			Map<String, Object> resultInfo = null;
			var page = 0;
			do {
				var content = send("GET",
						"search?pageSize=1000&page=" + page++ + "&dataprovider="
								+ URLEncoder.encode(dataprovider, Charset.forName("utf-8")),
						null);
				var results = Maps.of(content);
				List<Map<String, Object>> data = Maps.getArray(results, "data");
				resultInfo = Maps.getObject(results, "resultInfo");
				for (var result : data) {
					var url = Maps.getString(result, "dataSetUrl");
					var startRepo = serverUrl.length() + 1;
					var endRepo = url.indexOf("/dataset/");
					var repo = url.substring(startRepo, endRepo);
					var startQuery = url.indexOf("?commitId=");
					var processId = url.substring(endRepo + 17, startQuery);
					var commitId = url.substring(startQuery + 10);
					entries.add(new Entry(repo, processId, commitId));
				}
			} while (Maps.getInteger(resultInfo, "currentPage") < Maps.getInteger(resultInfo, "pageCount") - 1);
			return entries;
		}

		public void putReleased(OlcaRepository repo, Reference ref, String path) {
			put(repo, ref, path, true);
		}

		public void putUnreleased(OlcaRepository repo, Reference ref, String path) {
			put(repo, ref, path, false);
		}

		public void put(OlcaRepository repo, Reference ref, String path, boolean isReleased) {
			var json = repo.datasets.get(ref);
			var data = Maps.of(json);
			data.put("catgeories", ref.category.split("/"));
			data.put("contact", Maps.getString(data, "processDocumentation.dataSetOwner.name"));
			var reviewer = Maps.getString(data, "processDocumentation.reviewer.name");
			data.put("processType", getProcessType(data));
			var validFrom = Dates.getTime(Maps.get(data, "processDocumentation.validFrom"));
			data.put("validFrom", validFrom);
			data.put("validFromYear", getYear(validFrom));
			var validUntil = Dates.getTime(Maps.get(data, "processDocumentation.validUntil"));
			data.put("validUntil", validUntil);
			data.put("validUntilYear", getYear(validUntil));
			data.put("technology", Maps.getString(data, "processDocumentation.technologyDescription"));
			if (!Strings.nullOrEmpty(reviewer)) {
				data.put("reviewers", new String[] { reviewer });
				data.put("reviewType", "UNKNOWN");
			}
			if (Maps.get(data, "location.latitude") != null && Maps.get(data, "location.longitude") != null) {
				data.put("latitude", Maps.getLong(data, "location.latitude"));
				data.put("longitude", Maps.getLong(data, "location.longitude"));
			}
			data.put("location", Maps.getString(data, "location.name"));
			data.put("reviewed", !Strings.nullOrEmpty(reviewer));
			data.put("copyrightProtected", Maps.getBoolean(data, "processDocumentation.copyright"));
			data.put("copyrightHolder", Maps.getString(data, "processDocumentation.dataSetOwner.name"));
			if (!Strings.nullOrEmpty(Maps.getString(data, "defaultAllocationMethod"))) {
				data.put("multifunctionalModeling", ModellingApproach.fromDefaultAllocationMethod(data));
			}
			data.put("format", "JSON_LD");
			data.put("dataprovider", dataprovider);
			data.put("dataSetUrl", serverUrl +
					"/" + path
					+ "/dataset/PROCESS/"
					+ ref.refId
					+ "?commitId=" + ref.commitId);
			data.put("publiclyAccessible", isReleased);
			data.put("free", isReleased);
			new HashSet<>(data.keySet()).stream()
					.filter(Predicate.not(GLAD_FIELDS::contains))
					.forEach(data::remove);
			send("PUT", "search/index/" + ref.refId, data);
		}

		private String getProcessType(Map<String, Object> map) {
			var value = Maps.getString(map, "processType");
			if (value == null)
				return "UNKNOWN";
			if (value.equals(Enums.getLabel(org.openlca.core.model.ProcessType.LCI_RESULT)))
				return "FULLY_AGGREGATED";
			if (value.equals(Enums.getLabel(org.openlca.core.model.ProcessType.UNIT_PROCESS)))
				return "UNIT";
			return "UNKNOWN";
		}

		private Integer getYear(long time) {
			if (time == 0l)
				return null;
			var cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			return cal.get(Calendar.YEAR);
		}

		public void delete(String refId) {
			send("DELETE", "search/index/" + refId, null);
		}

		private String send(String type, String path, Map<String, Object> data) {
			try {
				var object = URI.create(baseUrl + "/" + path).toURL();
				var con = (HttpURLConnection) object.openConnection();
				con.addRequestProperty("api-key", apiKey);
				con.setRequestMethod(type);
				if ("GET".equals(type)) {
					con.setDoInput(true);
					con.setRequestProperty("Accept", "application/json");
				} else if ("PUT".equals(type)) {
					con.setDoOutput(true);
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json");
					var wr = new OutputStreamWriter(con.getOutputStream());
					wr.write(new ObjectMapper().writeValueAsString(data));
					wr.flush();
				}
				int status = con.getResponseCode();
				if (status >= 200 && status < 300) {
					if (!"GET".equals(type))
						return null;
					return readStream(con.getInputStream());
				}
				var error = readStream(con.getErrorStream());
				if (error == null)
					return null;
				throw new IOException(error);
			} catch (IOException e) {
				log.error("Error pushing to GLAD", e);
				throw Response.error(e.getMessage());
			}
		}

		private String readStream(InputStream s) throws IOException {
			if (s == null)
				return null;
			var sb = new StringBuilder();
			var br = new BufferedReader(new InputStreamReader(s, StandardCharsets.UTF_8));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			return sb.toString();
		}

	}

	private record Entry(String repositoryPath, String refId, String commitId) {
	}
}
