package com.greendelta.cloud.service.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.model.data.DatasetDescriptor;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Version;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.platform.guice.GuicyTest;
import com.greendelta.cloud.webservice.SessionResource;

public class DatasetServiceTest extends GuicyTest {

	private final static String refId = "86567837-bb42-48f2-9c87-8b557e035646";
	private final static long lastChange = 1431518323232l;
	private final static String jsonData = "{\"id\": \"" + refId + "\", \"name\": \"test process\"}";
	private final static String repositoryName = "test-repo";
	private final static String repositoryId = USER + "/" + repositoryName;

	@Inject
	private RepositoryService repositoryService;

	@Inject
	private SessionResource sessionResource;

	@Inject
	private DatasetService datasetService;

	@Inject
	@Named("repository.path")
	private String repositoryPath;

	@Before
	public void setup() {
		Map<String, Object> formData = new HashMap<>();
		formData.put("username", USER);
		formData.put("password", PASS);
		sessionResource.login(formData);
		repositoryService.create(repositoryName);
	}

	@Test
	public void test() throws IOException {
		DatasetDescriptor descriptor = new DatasetDescriptor();
		descriptor.setType(ModelType.PROCESS);
		descriptor.setVersion(new Version(1, 1, 1).toString());
		descriptor.setRefId(refId);
		descriptor.setLastChange(lastChange);
		// put
		String commitId = UUID.randomUUID().toString();
		datasetService.put(repositoryId, commitId, descriptor, jsonData);
		File repository = new File(repositoryPath + "/" + repositoryId);
		File file = new File(repository, "process");
		Assert.assertEquals("Process directory does not exist: ", true, file.exists());
		Assert.assertEquals("Process directory is not a directory: ", true, file.isDirectory());
		file = new File(file, refId);
		Assert.assertEquals("Datafile directory does not exist: ", true, file.exists());
		Assert.assertEquals("Datafile directory is not a directory: ", true, file.isDirectory());
		file = new File(file, commitId + ".json");
		Assert.assertEquals("Datafile does not exist: ", true, file.exists());
		Assert.assertEquals("Datafile is a directory: ", false, file.isDirectory());
		String read = org.openlca.cloud.util.Strings.concat((Object[]) Strings.readLines(new FileInputStream(file)));
		Assert.assertEquals("Json data is not correct: ", jsonData, read);
		// get
		read = datasetService.get(repositoryId, ModelType.PROCESS, refId, commitId);
		Assert.assertEquals("Json data is not correct: ", jsonData, read);
	}

	@After
	public void cleanup() {
		repositoryService.delete(repositoryName);
		sessionResource.logout();
	}

}
