package com.greendelta.cloud.service.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Version;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.data.Commit;
import com.greendelta.cloud.model.data.CommitData;
import com.greendelta.cloud.model.data.CommitDescriptor;
import com.greendelta.cloud.model.data.DatasetIdentifier;
import com.greendelta.cloud.platform.guice.GuicyTest;
import com.greendelta.cloud.webservice.SessionResource;

public class CommitServiceTest extends GuicyTest {

	private final static String refId = "86567837-bb42-48f2-9c87-8b557e035646";
	private final static long lastChange = 1431518323232l;
	private final static String jsonData = "{\"id\": \"" + refId + "\", \"name\": \"test process\"}";
	private final static String jsonData2 = "{\"id\": \"" + refId + "\", \"name\": \"test process changed\"}";
	private final static String message = "This is a test commit";
	private final static String message2 = "This is a second test commit";
	private final static String repositoryUser = "greve";
	private final static String repositoryName = "test-repo";
	private final static String repositoryId = repositoryUser + "/" + repositoryName;

	@Inject
	private CommitService commitService;

	@Inject
	private SessionResource sessionResource;

	@Inject
	private RepositoryService repositoryService;

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
	public void commitTest() throws IOException {
		CommitData data = new CommitData();
		DatasetIdentifier id1 = new DatasetIdentifier();
		id1.setType(ModelType.PROCESS);
		id1.setVersion(new Version(1, 1, 1).toString());
		id1.setRefId(refId);
		id1.setLastChange(lastChange);
		data.setIdentifier(id1);
		data.setJson(jsonData);
		pushAndAssert(data, message);
		assertCommitHistory(data, 1);
		CommitData data2 = new CommitData();

		DatasetIdentifier id2 = new DatasetIdentifier();
		id2 = new DatasetIdentifier();
		id2.setType(ModelType.PROCESS);
		id2.setVersion(new Version(1, 1, 2).toString());
		id2.setRefId(refId);
		id2.setLastChange(lastChange + 1);
		data2.setIdentifier(id2);
		data2.setJson(jsonData2);
		pushAndAssert(data2, message2);
		assertCommitHistory(data, 2);
	}

	private void pushAndAssert(CommitData data, String message) throws IOException {
		Commit commit = new Commit();
		commit.setMessage(message);
		commit.getData().add(data);
		String commitId = commitService.push(repositoryId, commit);
		File file = new File(repositoryPath + "/" + repositoryId + "/process/" + data.getIdentifier().getRefId() + "/"
				+ commitId + ".json");
		Assert.assertEquals("Datafile does not exist: ", true, file.exists());
		Assert.assertEquals("Datafile is a directory: ", false, file.isDirectory());
		String read = com.greendelta.cloud.util.Strings.concat((Object[]) Strings.readLines(new FileInputStream(file)));
		Assert.assertEquals("Json data is not correct: ", data.getJson(), read);
		File commitFile = new File(repositoryPath + "/" + repositoryId + "/history.json");
		Assert.assertEquals("Commitfile does not exist: ", true, commitFile.exists());
		Assert.assertEquals("Commitfile is a directory: ", false, commitFile.isDirectory());
		String[] readCommit = Strings.readLines(new FileInputStream(commitFile));
		String actual = readCommit[readCommit.length - 1].substring(0, commitId.length());
		Assert.assertEquals("Commit entry is not correct: ", commitId, actual);
	}

	private void assertCommitHistory(CommitData data, int length) {
		List<CommitDescriptor> history = commitService.getCommitHistory(repositoryId);
		Assert.assertNotNull(history);
		Assert.assertEquals(length, history.size());
		CommitDescriptor commit = history.get(length - 1);
		CommitDescriptor latestCommit = commitService.getLatestCommit(repositoryId);
		Assert.assertEquals(commit, latestCommit);
	}

	@After
	public void cleanup() {
		repositoryService.delete(repositoryName);
		sessionResource.logout();
	}

}
