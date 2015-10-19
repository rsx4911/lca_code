package com.greendelta.cloud.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.model.data.DatasetDescriptor;
import org.openlca.cloud.util.Directories;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Version;

import com.google.common.io.Files;

public class DatasetIndexerTest {

	private final static File DIRECTORY = Files.createTempDir();

	private DatasetIndexer indexer;

	@Before
	public void before() throws IOException {
		indexer = new DatasetIndexer(DIRECTORY);
	}

	@Test
	public void testIndexing() throws IOException {
		DatasetDescriptor expected = new DatasetDescriptor();
		expected.setRefId(UUID.randomUUID().toString());
		expected.setType(ModelType.ACTOR);
		expected.setLastChange(Calendar.getInstance().getTimeInMillis());
		expected.setVersion(new Version(1, 1, 1).toString());
		indexer.index(expected);
		DatasetDescriptor actual = indexer.get(ModelType.ACTOR,
				expected.getRefId());
		assertEquals(expected, actual);
		List<DatasetDescriptor> all = indexer.getAll();
		Assert.assertEquals(1, all.size());
		assertEquals(expected, all.get(0));
	}

	@Test
	public void testMassIndexing() throws IOException {
		int amount = 100000;
		List<DatasetDescriptor> descriptors = new ArrayList<>();
		for (int i = 0; i < amount; i++)
			descriptors.add(createDescriptors());
		long time = Calendar.getInstance().getTimeInMillis();
		indexer.index(descriptors);
		printSeconds("Indexing", time);
		time = Calendar.getInstance().getTimeInMillis();
		List<DatasetDescriptor> all = indexer.getAll();
		printSeconds("Retrieving all " + amount + " documents", time);
		Assert.assertEquals(amount, all.size());
		DatasetDescriptor expected = descriptors.get(amount / 2);
		time = Calendar.getInstance().getTimeInMillis();
		DatasetDescriptor actual = indexer.get(ModelType.ACTOR,
				expected.getRefId());
		printSeconds("Retrieving document", time);
		assertEquals(expected, actual);
	}

	private void printSeconds(String task, long since) {
		long milli = Calendar.getInstance().getTimeInMillis() - since;
		System.out.println(task + " took " + milli + " ms");
	}

	private void assertEquals(DatasetDescriptor expected,
			DatasetDescriptor actual) {
		Assert.assertEquals(expected.getRefId(), actual.getRefId());
		Assert.assertEquals(expected.getType(), actual.getType());
		Assert.assertEquals(expected.getLastChange(), actual.getLastChange());
		Assert.assertEquals(expected.getVersion(), actual.getVersion());
	}

	private DatasetDescriptor createDescriptors() {
		DatasetDescriptor descriptor = new DatasetDescriptor();
		descriptor.setRefId(UUID.randomUUID().toString());
		descriptor.setType(ModelType.ACTOR);
		descriptor.setLastChange(Calendar.getInstance().getTimeInMillis());
		descriptor.setVersion(new Version(1, 1, 1).toString());
		return descriptor;
	}

	@After
	public void after() {
		Directories.delete(DIRECTORY);
	}

}
