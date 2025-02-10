package com.greendelta.collaboration.config.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.util.Strings;

class Updates {

	private static final Logger log = LogManager.getLogger(Updates.class);
	private static final int CURRENT_SCHEMA_VERSION = 5;
	private final Statement s;

	static void executeScript(Statement s, String script) throws IOException, SQLException {
		log.info("Executing sql script: " + script);
		try (var reader = new BufferedReader(new InputStreamReader(Updates.class.getResourceAsStream(script)))) {
			var next = "";
			for (var line : reader.lines().toList()) {
				next += line;
				while (next.contains(";")) {
					var semicolon = next.indexOf(";");
					var update = next.substring(0, semicolon + 1);
					next = semicolon == next.length() - 1 ? "" : next.substring(semicolon + 1);
					s.executeUpdate(update);
				}
			}
		}
	}

	static void checkAndRun(Statement s) throws SQLException, IOException {
		new Updates(s).run();
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
		log.info("Database updated successfully to schema version " + schemaVersion);
	}

	private int runUpdateFrom(int currentVersion) throws SQLException, IOException {
		return switch (currentVersion) {
			case 0, 1 -> new Update1(s).run();
			case 2 -> new Update(s, 2).run();
			case 3 -> new Update(s, 3).run();
			case 4 -> new Update(s, 4).run();
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
		var sql = existed
				? "UPDATE setting SET value = '" + schemaVersion + "' WHERE name = 'SCHEMA_VERSION'"
				: "INSERT INTO setting(id, name, value) VALUES (" + getNextSettingId() + ", 'SCHEMA_VERSION', '"
						+ schemaVersion + "')";
		log.info("  " + sql);
		s.executeUpdate(sql);
	}

	private long getNextSettingId() throws SQLException {
		try (var rs = s.executeQuery("SELECT max(id) FROM setting")) {
			if (!rs.next())
				return 1;
			var value = rs.getString(1);
			if (Strings.nullOrEmpty(value))
				return 1;
			return Long.parseLong(value) + 1;
		}
	}

	record UpdateResult(int schemaVersion, boolean reindex) {
	}

}
