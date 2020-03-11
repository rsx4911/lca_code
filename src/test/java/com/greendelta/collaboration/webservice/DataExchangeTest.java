package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.api.RepositoryClient;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.cloud.util.Datasets;
import org.openlca.cloud.util.WebRequests.WebRequestException;
import org.openlca.core.database.Daos;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.model.CategorizedEntity;
import org.openlca.core.model.ModelType;
import org.openlca.util.Dirs;
import org.zeroturnaround.zip.ZipUtil;

import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.webservice.setup.WebserviceTest;

public class DataExchangeTest extends WebserviceTest {

	private File emptyDbFolder;
	private File completeDbFolder;

	@Before
	public void setup() throws IOException {
		completeDbFolder = unpack("complete");
		emptyDbFolder = unpack("empty");
	}

	private File unpack(String name) throws IOException {
		File tmpFolder = Files.createTempDirectory("lcacs-data-exchange-test-").toFile();
		InputStream stream = getClass().getResourceAsStream(name + ".zolca");
		ZipUtil.unpack(stream, tmpFolder);
		return tmpFolder;
	}

	@Test
	public void testDataExchange() throws IOException, WebRequestException, InterruptedException {
		IDatabase database = new DerbyDatabase(completeDbFolder);
		Set<Dataset> data = getData(database);
		RepositoryClient client = getClient(database);
		client.commit("Initial commit", data, null);
		database.close();
		database = new DerbyDatabase(emptyDbFolder);
		client = getClient(database);
		Set<FileReference> toFetch = Collections.convertToSet(data, dataset -> dataset.asFileReference());
		client.fetch(toFetch, null, null);
		client.getConfig().remove();
		database.close();
		database = new DerbyDatabase(emptyDbFolder);
		DatabaseAssertion.on(database);
		database.close();
	}

	private Set<Dataset> getData(IDatabase database) {
		Set<Dataset> data = new HashSet<>();
		for (ModelType type : ModelType.values()) {
			if (!type.isCategorized())
				continue;
			for (CategorizedEntity entity : Daos.categorized(database, type).getAll()) {
				data.add(Datasets.toDataset(entity));
			}
		}
		return data;
	}

	@After
	public void cleanup() {
		if (completeDbFolder != null) {
			Dirs.delete(completeDbFolder.toPath());
		}
		if (emptyDbFolder != null) {
			Dirs.delete(emptyDbFolder.toPath());
		}
	}

}
