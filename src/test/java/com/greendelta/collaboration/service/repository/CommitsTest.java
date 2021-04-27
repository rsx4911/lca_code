package com.greendelta.collaboration.service.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.Commits;
import org.openlca.core.model.ModelType;

public class CommitsTest {

	private static final String[] commitIds = {
			"aba49d04179faa1034eaf6d221a903ef64f3dbaf",
			"63f8eeaf7e65f7817e604460053fa1dcbfb28d35",
			"0adbf8bddac2cae5c81801fd836075fc612e37e4",
			"079cffbd7fc044a18ae3be0a748c29537594b951",
			"db3ba75f99df098aec28726447ad583fea3bd93b",
			"0c9395b3c2e28a26265d35a146f64369c82085fe"
	};
	private Commits commits;

	@Before
	public void before() throws IOException, GitAPIException {
		String path = "C:/Users/Sebastian/git/lca-collaboration/src/test/resources/com/greendelta/collaboration/service/repository/ref_data";
		File workDir = new File(path);
		try (FileRepository repo = new FileRepository(workDir)) {
			commits = new Commits(repo);
		}
	}

	@Test
	public void testGet() {
		Commit commit = commits.get(commitIds[2]);
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[2], commit.id);
	}

	@Test
	public void testFindLastId() {
		String lastId = commits.find().latestId();
		Assert.assertNotNull(lastId);
		Assert.assertEquals(commitIds[commitIds.length - 1], lastId);
	}

	@Test
	public void testFindLast() {
		Commit commit = commits.find().latest();
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
		Assert.assertEquals(commitIds[commitIds.length - 3], all.get(0).id);
	}

	@Test
	public void testFindLastModel() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543").latest();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 3], commit.id);
	}

	@Test
	public void testFindLastModelId() {
		String commitId = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543").latestId();
		Assert.assertNotNull(commitId);
		Assert.assertEquals(commitIds[commitIds.length - 3], commitId);
	}

	@Test
	public void testFindLastModelBefore() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.before(commitIds[commitIds.length - 3]).latest();
		Assert.assertNull(commit);
		commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.before(commitIds[commitIds.length - 2]).latest();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 3], commit.id);
	}

	@Test
	public void testFindLastModelUntil() {
		Commit commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.until(commitIds[commitIds.length - 4]).latest();
		Assert.assertNull(commit);
		commit = commits.find().model(ModelType.LOCATION, "af92823f-638d-36d7-8406-451a58f61543")
				.until(commitIds[commitIds.length - 3]).latest();
		Assert.assertNotNull(commit);
		Assert.assertEquals(commitIds[commitIds.length - 3], commit.id);
	}

}
