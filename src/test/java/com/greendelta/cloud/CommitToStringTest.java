package com.greendelta.cloud;

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
		entry.setId("728c27550750206d44c18e0a524c65c8");
		entry.setMessage("This is a test commit");
		entry.setTimestamp(1431389292891l);
		entry.setUser("greve");
	}

	@Test
	public void testToString() {
		String s = entry.toString();
		Assert.assertEquals(expected, s);
	}

	@Test
	public void testParsing() {
		Commit parsed = Commit.parse(expected);
		Assert.assertEquals(entry.getId(), parsed.getId());
		Assert.assertEquals(entry.getMessage(), parsed.getMessage());
		Assert.assertEquals(entry.getTimestamp(), parsed.getTimestamp());
		Assert.assertEquals(entry.getUser(), parsed.getUser());
	}
}
