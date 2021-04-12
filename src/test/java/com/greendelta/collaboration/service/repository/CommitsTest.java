package com.greendelta.collaboration.service.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

public class CommitsTest {

	private static final String[] commitIds = {
			"63fda90b2d94a5ee0dae0708055965bb3d40d271",
			"52181ea4c0458a619ebe4e817fbbd07ff1627793",
			"bf2be1ecabe8fec2278b2c24f3b26f506d10deb3",
			"a877b304b3cc28cb2a70845862f35970d76630ef",
			"01fb4247461f632d4bdc02396894852a80882145"
	};
	private Commits commits;

	@Before
	public void before() throws IOException, GitAPIException {
		String path = "C:/Users/Sebastian/git/lca-collaboration/src/test/resources/com/greendelta/collaboration/service/repository/ref_data";
		File workDir = new File(path);
		commits = new Commits(workDir);
	}

	@Test
	public void testGet() {
		Commit commit = commits.get(commitIds[2]);
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[2], commit.id);
	}

	@Test
	public void testFindLastId() {
		String lastId = commits.find().id();
		Assert.assertNotNull(lastId);
		Assert.assertEquals(commitIds[commitIds.length - 1], lastId);
	}

	@Test
	public void testFindLast() {
		Commit commit = commits.find().last();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 1], commit.id);
	}

	@Test
	public void testFindAll() {
		List<Commit> all = commits.find().all();
		Assert.assertEquals(commitIds.length, all.size());
		for (int i = 0; i < commitIds.length; i++) {
			Commit commit = all.get(i);
			Assert.assertEquals(commitIds[i], commit.id);
		}
	}

	@Test
	public void testFindAfter() {
		List<Commit> all = commits.find().after(commitIds[1]).all();
		Assert.assertEquals(commitIds.length - 2, all.size());
		for (int i = 0; i < commitIds.length - 2; i++) {
			Commit commit = all.get(i);
			Assert.assertEquals(commitIds[i + 2], commit.id);
		}
	}

	@Test
	public void testFindFrom() {
		List<Commit> all = commits.find().from(commitIds[1]).all();
		Assert.assertEquals(commitIds.length - 1, all.size());
		for (int i = 0; i < commitIds.length - 1; i++) {
			Commit commit = all.get(i);
			Assert.assertEquals(commitIds[i + 1], commit.id);
		}
	}

	@Test
	public void testFindUntil() {
		List<Commit> all = commits.find().until(commitIds[2]).all();
		Assert.assertEquals(3, all.size());
		for (int i = 0; i < 3; i++) {
			Commit commit = all.get(i);
			Assert.assertEquals(commitIds[i], commit.id);
		}
	}

	@Test
	public void testFindModel() {
		List<Commit> all = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543").all();
		Assert.assertEquals(1, all.size());
		Assert.assertEquals(commitIds[commitIds.length - 2], all.get(0).id);
	}

	@Test
	public void testFindLastModel() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543").last();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 2], commit.id);
	}

	@Test
	public void testFindLastModelId() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543").last();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 2], commit.id);
	}

	@Test
	public void testFindLastModelBefore() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.before(commitIds[commitIds.length - 2]).last();
		Assert.assertNull(commit);
		commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.before(commitIds[commitIds.length - 1]).last();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 2], commit.id);
	}

	@Test
	public void testFindLastModelUntil() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.until(commitIds[commitIds.length - 3]).last();
		Assert.assertNull(commit);
		commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.until(commitIds[commitIds.length - 2]).last();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 2], commit.id);
	}

}
