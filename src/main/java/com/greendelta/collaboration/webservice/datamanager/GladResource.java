package com.greendelta.collaboration.webservice.datamanager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.openlca.cloud.api.git.Reference;
import org.openlca.core.model.AllocationMethod;
import org.openlca.jsonld.Dates;
import org.openlca.jsonld.Enums;
import org.openlca.util.Strings;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.model.glad.ProcessType;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.ServerConfig;
import com.greendelta.collaboration.util.GsonTypes;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.FrontendReference;

@Path("datamanager/glad")
public class GladResource {

	private static final List<String> GLAD_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "processType", "supportedNomenclatures", "aggregationType", "multifunctionalModeling", "name",
			"categories", "location", "completeness", "technology", "copyrightHolder", "license", "contact",
			"description", "dataSetUrl", "format", "validFrom", "validFromYear", "validUntil", "validUntilYear",
			"copyrightProtected", "free", "dataprovider", "reviewers", "reviewType", "longitude", "latitude",
			"publiclyAccessible"));

	private final RepositoryService repoService;
	private final SettingsService settingsService;

	@Inject
	public GladResource(RepositoryService repoService, SettingsService settingsService) {
		this.repoService = repoService;
		this.settingsService = settingsService;
	}

	@PUT
	@Path("push/{group}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response pushToGlad(
			@PathParam("group") String group,
			@PathParam("name") String name,
			Input input) {
		ServerConfig config = settingsService.serverConfig;
		String gladUrl = config.get(ServerSetting.GLAD_URL);
		String gladHeaderField = config.get(ServerSetting.GLAD_API_KEY_HEADER);
		String gladHeaderValue = config.get(ServerSetting.GLAD_API_KEY);
		if (gladUrl == null || gladUrl.isEmpty())
			return Respond.status(Status.SERVICE_UNAVAILABLE, "No GLAD service url specified");
		Repository repo = repoService.get(group, name);
		if (repo == null)
			return Respond.notFound("No repository with id " + group + "/" + name + " found");
		List<Reference> refs = FrontendReference.collect(repo, input.references);
		if (refs.isEmpty())
			return Respond.notFound("No data in repository " + group + "/" + name + " found");
		Gson gson = new Gson();
		for (Reference ref : refs) {
			Map<String, Object> data = loadProcessData(repo, ref);
			data.put("format", "JSON_LD");
			data.put("dataprovider", input.dataprovider);
			String baseUrl = config.get(ServerSetting.SERVER_URL);
			data.put("dataSetUrl", baseUrl + "/ws/public/browse/" + repo.toId() + "/PROCESS/"
					+ ref.refId + "?commitId=" + ref.commitId);
			data.put("publiclyAccessible", repo.settings.is(RepositorySetting.PUBLIC_ACCESS));
			data.put("free", repo.settings.is(RepositorySetting.PUBLIC_ACCESS));
			for (String key : new ArrayList<>(data.keySet())) {
				if (!GLAD_FIELDS.contains(key)) {
					data.remove(key);
				}
			}
			try {
				send(gladUrl, gladHeaderField, gladHeaderValue, ref.refId, gson.toJson(data));
			} catch (Exception e) {
				LogManager.getLogger(GladResource.class).error("Error pushing to GLAD", e);
				return Respond.error(e.getMessage());
			}
		}
		return Respond.ok();

	}

	private Map<String, Object> loadProcessData(Repository repo, Reference ref) {
		String json = repo.datasets.get(ref.objectId);
		ObjectMap data = ObjectMap.fromMap(new Gson().fromJson(json, GsonTypes.OBJECT_MAP));
		data.put("catgeories", ref.category.split("/"));
		data.put("contact", data.getString("processDocumentation.dataSetOwner.name"));
		String reviewer = data.getString("processDocumentation.reviewer.name");
		data.put("processType", getProcessType(data.getString("processType")));
		long validFrom = Dates.getTime(data.get("processDocumentation.validFrom"));
		data.put("validFrom", validFrom);
		data.put("validFromYear", getYear(validFrom));
		long validUntil = Dates.getTime(data.get("processDocumentation.validUntil"));
		data.put("validUntil", validUntil);
		data.put("validUntilYear", getYear(validUntil));
		data.put("technology", data.getString("processDocumentation.technologyDescription"));
		if (!Strings.nullOrEmpty(reviewer)) {
			data.put("reviewers", new String[] { reviewer });
			data.put("reviewType", "UNKNOWN");
		}
		if (data.get("location.latitude") != null && data.get("location.latitude") != null) {
			data.put("latitude", data.getLong("location.latitude"));
			data.put("longitude", data.getLong("location.longitude"));
		}
		data.put("location", data.getString("location.name"));
		data.put("reviewed", !Strings.nullOrEmpty(reviewer));
		data.put("copyrightProtected", data.getBoolean("processDocumentation.copyright"));
		data.put("copyrightHolder", data.getString("processDocumentation.dataSetOwner.name"));
		if (!Strings.nullOrEmpty(data.getString("defaultAllocationMethod"))) {
			data.put("multifunctionalModeling", getModellingApproach(data.getString("defaultAllocationMethod")));
		}
		return data;
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

	private static Integer getYear(long time) {
		if (time == 0l)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.YEAR);
	}

	private static ModellingApproach getModellingApproach(String value) {
		if (value == null)
			return ModellingApproach.UNKNOWN;
		if (value.equals(Enums.getLabel(AllocationMethod.PHYSICAL)))
			return ModellingApproach.PHYSICAL;
		if (value.equals(Enums.getLabel(AllocationMethod.ECONOMIC)))
			return ModellingApproach.ECONOMIC;
		if (value.equals(Enums.getLabel(AllocationMethod.CAUSAL)))
			return ModellingApproach.CAUSAL;
		if (value.equals(Enums.getLabel(AllocationMethod.NONE)))
			return ModellingApproach.NONE;
		return ModellingApproach.UNKNOWN;
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
		if (!Strings.nullOrEmpty(headerField) && !Strings.nullOrEmpty(headerValue)) {
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
		public List<FrontendReference> references;

	}

}
