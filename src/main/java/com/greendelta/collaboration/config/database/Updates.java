package com.greendelta.collaboration.config.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class Updates {

	private static final int CURRENT_SCHEMA_VERSION = 2;
	private final Statement s;

	static void run(Connection con) throws SQLException, IOException {
		try (var s = con.createStatement()) {
			new Updates(s).run();
		}
	}

	private Updates(Statement s) {
		this.s = s;
	}

	private void run() throws SQLException, IOException {
		var schemaVersion = getSchemaVersion();
		if (schemaVersion == CURRENT_SCHEMA_VERSION)
			return;
		var existed = schemaVersion != 0;
		while (schemaVersion < CURRENT_SCHEMA_VERSION) {
			schemaVersion = runUpdateFrom(schemaVersion);
		}
		setSchemaVersion(schemaVersion, existed);
	}

	private int runUpdateFrom(int currentVersion) throws SQLException, IOException {
		return switch (currentVersion) {
			case 0, 1 -> new Update1(s).run();
			default -> throw new IllegalArgumentException("Unknown schema version: " + currentVersion);
		};
	}

	private int getSchemaVersion() throws SQLException {
		try (var rs = s.executeQuery("SELECT value FROM setting WHERE name = 'SCHEMA_VERSION'")) {
			if (!rs.next())
				return 0;
			return Integer.parseInt(rs.getString("value"));
		}
	}

	private void setSchemaVersion(int schemaVersion, boolean existed) throws SQLException {
		if (existed) {
			s.executeUpdate("UPDATE setting SET value = '" + schemaVersion + "' WHERE name = 'SCHEMA_VERSION'");
		} else {
			s.executeUpdate("INSERT INTO setting(id, name, value) VALUES (" + getNextSettingId()
					+ ", 'SCHEMA_VERSION', '" + schemaVersion + "')");
		}
	}

	private long getNextSettingId() throws SQLException {
		try (var rs = s.executeQuery("SELECT max(id) FROM setting")) {
			if (!rs.next())
				return 1;
			return Long.parseLong(rs.getString(1)) + 1;
		}
	}

}
