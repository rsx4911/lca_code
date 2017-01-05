package com.greendelta.collaboration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlca.cloud.model.data.Commit;

public class CommitToStringTest {

	private Commit entry;
	private String expected = "728c27550750206d44c18e0a524c65c8 1431389292891 greve This is a test commit";

	@Before
	public void setup() {
		entry = new Commit();
		entry.id = "728c27550750206d44c18e0a524c65c8";
		entry.message = "This is a test commit";
		entry.timestamp = 1431389292891l;
		entry.user = "greve";
	}

	@Test
	public void testToString() {
		String s = entry.toString();
		Assert.assertEquals(expected, s);
	}

	@Test
	public void testParsing() {
		Commit parsed = Commit.parse(expected);
		Assert.assertEquals(entry.id, parsed.id);
		Assert.assertEquals(entry.message, parsed.message);
		Assert.assertEquals(entry.timestamp, parsed.timestamp);
		Assert.assertEquals(entry.user, parsed.user);
	}
}
