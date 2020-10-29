package com.greendelta.collaboration.webservice.datamanager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.Dates;
import org.openlca.jsonld.Enums;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.glad.ProcessType;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.ReferenceCollector;
import com.greendelta.collaboration.webservice.ReferenceCollector.Reference;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.search.wrapper.SearchClient;

import joptsimple.internal.Strings;

@Path("datamanager/glad")
public class GladResource {

	private static final List<String> GLAD_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "processType", "supportedNomenclatures", "aggregationType", "multifunctionalModeling", "name",
			"categories", "location",
			"completeness", "technology", "copyrightHolder", "license", "contact", "description", "dataSetUrl",
			"format", "validFrom", "validFromYear", "validUntil", "validUntilYear", "copyrightProtected", "free",
			"dataprovider", "reviewers", "reviewType", "longitude", "latitude", "publiclyAccessible"));

	private final RepositoryService repoService;
	private final BrowseService browseService;
	private final SettingsService settingsService;
	private final HistoryService historyService;

	@Inject
	public GladResource(RepositoryService repoService, BrowseService browseService,
			SettingsService settingsService, HistoryService historyService) {
		this.repoService = repoService;
		this.browseService = browseService;
		this.settingsService = settingsService;
		this.historyService = historyService;
	}

	@PUT
	@Path("push/{group}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response pushToGlad(
			@PathParam("group") String group,
			@PathParam("name") String name,
			Input input) {
		String gladUrl = settingsService.get(Key.GLAD_URL);
		String gladHeaderField = settingsService.get(Key.GLAD_API_KEY_HEADER);
		String gladHeaderValue = settingsService.get(Key.GLAD_API_KEY);
		if (gladUrl == null || gladUrl.isEmpty())
			return Respond.status(Status.SERVICE_UNAVAILABLE, "No GLAD service url specified");
		Repository repo = repoService.get(group, name);
		if (repo == null)
			return Respond.notFound("No repository with id " + group + "/" + name + " found");
		String repoId = repo.toId();
		Map<String, String> dsToCommit = new HashMap<>();
		ReferenceCollector<String> collector = new ReferenceCollector<>(browseService, (ref) -> {
			String commitId = historyService.getLastCommit(repo, ModelType.PROCESS, ref.id).id;
			dsToCommit.put(ref.id, commitId);
			return IndexEntry.toIndexId(repoId, ref.type, ref.id, commitId);
		});
		Set<String> remaining = collector.getReferences(repo, input.references);
		if (remaining.isEmpty())
			return Respond.notFound("No data in repository " + group + "/" + name + " found");
		SearchClient client = settingsService.getSearchConfig().getSearchClient();
		Gson gson = new Gson();
		while (!remaining.isEmpty()) {
			Set<String> next = com.greendelta.collaboration.util.Collections.pop(remaining, 1000);
			List<Map<String, Object>> allData = client.get(next);
			for (Map<String, Object> data : allData) {
				putProcessData(repo, dsToCommit, data);
				data.put("format", "JSON_LD");
				data.put("dataprovider", input.dataprovider);
				String baseUrl = settingsService.get(Key.SERVER_URL);
				String refId = data.get("refId").toString();
				data.put("dataSetUrl", baseUrl + "/ws/public/browse/" + repoId + "/PROCESS/"
						+ refId + "?commitId=" + dsToCommit.get(refId));
				data.put("publiclyAccessible", repo.settings.publicAccess);
				data.put("free", repo.settings.publicAccess);
				for (String key : new ArrayList<>(data.keySet())) {
					if (!GLAD_FIELDS.contains(key)) {
						data.remove(key);
					}
				}
				try {
					send(gladUrl, gladHeaderField, gladHeaderValue, refId, gson.toJson(data));
				} catch (Exception e) {
					LogManager.getLogger(GladResource.class).error("Error pushing to GLAD", e);
					return Respond.error(e.getMessage());
				}
			}
		}
		return Respond.ok();
	}

	private void putProcessData(Repository repo, Map<String, String> dsToCommit, Map<String, Object> d) {
		String refId = d.get("refId").toString();
		ObjectMap data = ObjectMap.fromMap(repo.readData(ModelType.PROCESS, refId, dsToCommit.get(refId)));
		String reviewer = data.getString("processDocumentation.reviewer.name");
		d.put("processType", getProcessType(data.getString("processType")));
		d.put("validFrom", Dates.getTime(data.get("processDocumentation.validFrom")));
		d.put("validUntil", Dates.getTime(data.get("processDocumentation.validUntil")));
		d.put("technology", data.getString("processDocumentation.technologyDescription"));
		if (!Strings.isNullOrEmpty(reviewer)) {
			d.put("reviewers", new String[] { reviewer });
			d.put("reviewType", "UNKNOWN");
		}
		if (data.get("location.latitude") != null && data.get("location.longitude") != null) {
			d.put("latitude", data.getLong("location.latitude"));
			d.put("longitude", data.getLong("location.longitude"));
		}
		d.put("reviewed", !Strings.isNullOrEmpty(reviewer));
		d.put("copyrightProtected", data.getBoolean("processDocumentation.copyright"));
		d.put("copyrightHolder", data.getString("processDocumentation.dataSetOwner.name"));
		d.put("description", data.getString("description"));
		if (!Strings.isNullOrEmpty(data.getString("modellingApproach"))) {
			d.put("multifunctionalModeling", data.getString("modellingApproach"));
		}
	}

	private ProcessType getProcessType(String type) {
		if (type == null)
			return ProcessType.UNKNOWN;
		if (type.equals(Enums.getLabel(org.openlca.core.model.ProcessType.LCI_RESULT)))
			return ProcessType.FULLY_AGGREGATED;
		if (type.equals(Enums.getLabel(org.openlca.core.model.ProcessType.UNIT_PROCESS)))
			return ProcessType.UNIT;
		return ProcessType.UNKNOWN;
	}

	private void send(String gladBaseUrl, String headerField, String headerValue, String refId, String data)
			throws Exception {
		URL object = new URL(gladBaseUrl + "/search/index/" + refId);
		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("PUT");
		if (!Strings.isNullOrEmpty(headerField) && !Strings.isNullOrEmpty(headerValue)) {
			con.addRequestProperty(headerField, headerValue);
		}
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(data);
		wr.flush();
		int status = con.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK)
			return;
		InputStream s = con.getErrorStream();
		if (s == null)
			return;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(s, "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		throw new Exception(sb.toString());
	}

	private static class Input {

		public String dataprovider;
		public List<Reference> references;

	}

}
