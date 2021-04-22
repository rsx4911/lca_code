package com.greendelta.collaboration.service.repository;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.service.repository.Commits.Commit;
import com.greendelta.collaboration.service.repository.References.CommitReference;

public class DatasetsTest {
	private static final String[] commitIds = {
			"aba49d04179faa1034eaf6d221a903ef64f3dbaf",
			"63f8eeaf7e65f7817e604460053fa1dcbfb28d35",
			"0adbf8bddac2cae5c81801fd836075fc612e37e4",
			"079cffbd7fc044a18ae3be0a748c29537594b951",
			"db3ba75f99df098aec28726447ad583fea3bd93b",
			"0c9395b3c2e28a26265d35a146f64369c82085fe"
	};
	private Commits commits;
	private References references;
	private Datasets datasets;

	@Before
	public void before() throws IOException, GitAPIException {
		String path = "C:/Users/Sebastian/git/lca-collaboration/src/test/resources/com/greendelta/collaboration/service/repository/ref_data";
		File workDir = new File(path);
		try (FileRepository repo = new FileRepository(workDir)) {
			commits = new Commits(repo);
			references = new References(repo);
			datasets = new Datasets(repo);
		}
	}

	@Test
	public void testGet() {
		Commit commit = commits.get(commitIds[5]);
		CommitReference ref = references.get(ModelType.FLOW, "00c3eaf0-7c2f-3f63-a756-37ffbd4f2b21", commit);
		String data = datasets.get(ref);
		Assert.assertNotNull(data);
	}

}
