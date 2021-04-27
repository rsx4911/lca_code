package com.greendelta.collaboration.service.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.api.git.Reference;
import org.openlca.cloud.api.git.References;
import org.openlca.core.model.ModelType;

public class ReferencesTest {

	private static final String[] commitIds = {
			"aba49d04179faa1034eaf6d221a903ef64f3dbaf",
			"63f8eeaf7e65f7817e604460053fa1dcbfb28d35",
			"0adbf8bddac2cae5c81801fd836075fc612e37e4",
			"079cffbd7fc044a18ae3be0a748c29537594b951",
			"db3ba75f99df098aec28726447ad583fea3bd93b",
			"0c9395b3c2e28a26265d35a146f64369c82085fe"
	};
	private References references;

	@Before
	public void before() throws IOException, GitAPIException {
		String path = "C:/Users/Sebastian/git/lca-collaboration/src/test/resources/com/greendelta/collaboration/service/repository/ref_data";
		File workDir = new File(path);
		try (FileRepository repo = new FileRepository(workDir)) {
			references = new References(repo);
		}
	}

	@Test
	public void testGet() {
		Reference ref = references.get(ModelType.UNIT_GROUP, "da299c4d-1741-4da8-9fbd-5ccfb5e1d688",
				commitIds[0]);
		Assert.assertNotNull(ref);
		Assert.assertEquals(ModelType.UNIT_GROUP, ref.type);
		Assert.assertEquals("da299c4d-1741-4da8-9fbd-5ccfb5e1d688", ref.refId);
		Assert.assertEquals(commitIds[0], ref.commitId);
	}

	@Test
	public void testFindAll1() {
		List<Reference> refs = references.find().commit(commitIds[0]).changed();
		Assert.assertEquals(27, refs.size());
		Reference ref = refs.get(0);
		Assert.assertEquals(ModelType.UNIT_GROUP, ref.type);
		Assert.assertEquals("da299c4d-1741-4da8-9fbd-5ccfb5e1d688", ref.refId);
		Assert.assertEquals(commitIds[0], ref.commitId);
	}

	@Test
	public void testFindAll2() {
		List<Reference> refs = references.find().commit(commitIds[1]).all();
		Assert.assertEquals(60, refs.size());
		Reference ref = refs.get(0);
		Assert.assertEquals(ModelType.FLOW_PROPERTY, ref.type);
		Assert.assertEquals("fdfecf14-ff8a-4e17-b2b2-f938c4b5cc27", ref.refId);
		Assert.assertEquals(commitIds[1], ref.commitId);
		ref = refs.get(33);
		Assert.assertEquals(ModelType.UNIT_GROUP, ref.type);
		Assert.assertEquals("da299c4d-1741-4da8-9fbd-5ccfb5e1d688", ref.refId);
		Assert.assertEquals(commitIds[1], ref.commitId);
	}

	@Test
	public void testFindTypeChanged() {
		List<Reference> refs = references.find().type(ModelType.FLOW_PROPERTY).commit(commitIds[1]).changed();
		Assert.assertEquals(33, refs.size());
		Reference ref = refs.get(0);
		Assert.assertEquals(ModelType.FLOW_PROPERTY, ref.type);
		Assert.assertEquals("fdfecf14-ff8a-4e17-b2b2-f938c4b5cc27", ref.refId);
		Assert.assertEquals(commitIds[1], ref.commitId);
	}

	@Test
	public void testFindPathChanged() {
		List<Reference> refs = references.find().path("FLOW_PROPERTY/Economic flow properties")
				.commit(commitIds[1]).changed();
		Assert.assertEquals(1, refs.size());
		Reference ref = refs.get(0);
		Assert.assertEquals(ModelType.FLOW_PROPERTY, ref.type);
		Assert.assertEquals("fdfecf14-ff8a-4e17-b2b2-f938c4b5cc27", ref.refId);
		Assert.assertEquals(commitIds[1], ref.commitId);
	}

}
