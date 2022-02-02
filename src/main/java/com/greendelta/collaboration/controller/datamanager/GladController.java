package com.greendelta.collaboration.controller.datamanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openlca.core.model.AllocationMethod;
import org.openlca.git.model.Reference;
import org.openlca.jsonld.Enums;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greendelta.collaboration.controller.util.FrontendReference;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.glad.ModellingApproach;
import com.greendelta.collaboration.model.glad.ProcessType;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Maps;

@RestController
@RequestMapping("ws/datamanager/glad")
public class GladController {

	private static final List<String> GLAD_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "processType", "supportedNomenclatures", "aggregationType", "multifunctionalModeling", "name",
			"categories", "location", "completeness", "technology", "copyrightHolder", "license", "contact",
			"description", "dataSetUrl", "format", "validFrom", "validFromYear", "validUntil", "validUntilYear",
			"copyrightProtected", "free", "dataprovider", "reviewers", "reviewType", "longitude", "latitude",
			"publiclyAccessible"));

	private final RepositoryService repoService;
	private final SettingsService settingsService;

	@Autowired
	public GladController(RepositoryService repoService, SettingsService settingsService) {
		this.repoService = repoService;
		this.settingsService = settingsService;
	}

	@PutMapping("push/{group}/{name}")
	public void pushToGlad(
			@PathVariable("group") String group,
			@PathVariable("name") String name,
			@RequestBody Input input) {
		var config = settingsService.serverConfig;
		String gladUrl = config.get(ServerSetting.GLAD_URL);
		String gladHeaderField = config.get(ServerSetting.GLAD_API_KEY_HEADER);
		String gladHeaderValue = config.get(ServerSetting.GLAD_API_KEY);
		if (gladUrl == null || gladUrl.isEmpty())
			throw Response.unavailable("No GLAD service url specified");
		try (var repo = repoService.get(group, name)) {
			if (repo == null)
				throw Response.notFound("No repository with id " + group + "/" + name + " found");
			var refs = FrontendReference.collect(repo, input.references);
			if (refs.isEmpty())
				throw Response.notFound("No data in repository " + group + "/" + name + " found");
			refs.forEach(ref -> {
				var data = loadProcessData(repo, ref);
				data.put("format", "JSON_LD");
				data.put("dataprovider", input.dataprovider);
				String baseUrl = config.get(ServerSetting.SERVER_URL);
				data.put("dataSetUrl", baseUrl + "/ws/public/browse/" + repo.path() + "/PROCESS/"
						+ ref.refId + "?commitId=" + ref.commitId);
				data.put("publiclyAccessible", repo.settings.is(RepositorySetting.PUBLIC_ACCESS));
				data.put("free", repo.settings.is(RepositorySetting.PUBLIC_ACCESS));
				for (var key : new ArrayList<>(data.keySet())) {
					if (!GLAD_FIELDS.contains(key)) {
						data.remove(key);
					}
				}
				try {
					var dataString = new ObjectMapper().writeValueAsString(data);
					send(gladUrl, gladHeaderField, gladHeaderValue, ref.refId, dataString);
				} catch (Exception e) {
					LogManager.getLogger(GladController.class).error("Error pushing to GLAD", e);
					throw Response.error(e.getMessage());
				}
			});
		}
	}

	private Map<String, Object> loadProcessData(Repository repo, Reference ref) {
		var json = repo.datasets().get(ref);
		var data = Maps.of(json);
		data.put("catgeories", ref.category.split("/"));
		data.put("contact", Maps.getString(data, "processDocumentation.dataSetOwner.name"));
		var reviewer = Maps.getString(data, "processDocumentation.reviewer.name");
		data.put("processType", getProcessType(Maps.getString(data, "processType")));
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
			data.put("multifunctionalModeling", getModellingApproach(Maps.getString(data, "defaultAllocationMethod")));
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
		var cal = Calendar.getInstance();
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
		var object = new URL(gladBaseUrl + "/search/index/" + refId);
		var con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		con.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
		con.setRequestMethod("PUT");
		if (!Strings.nullOrEmpty(headerField) && !Strings.nullOrEmpty(headerValue)) {
			con.addRequestProperty(headerField, headerValue);
		}
		var wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(data);
		wr.flush();
		int status = con.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK)
			return;
		var s = con.getErrorStream();
		if (s == null)
			return;
		var sb = new StringBuilder();
		var br = new BufferedReader(new InputStreamReader(s, "utf-8"));
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
