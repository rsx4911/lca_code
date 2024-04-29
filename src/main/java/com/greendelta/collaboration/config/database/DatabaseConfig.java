package com.greendelta.collaboration.config.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig implements HibernatePropertiesCustomizer {

	// tables present in all states of the schema
	private static final List<String> TABLES = Arrays.asList("comment", "job", "membership", "message", "review",
			"review_reference", "setting", "task_assignment", "team", "team_users", "user", "user_blocked_users");
	private final DataSource dataSource;

	public DatabaseConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		try (var con = dataSource.getConnection();
				var s = con.createStatement()) {
			if (!databaseInitialized(con)) {
				initializeDatabase(s);
			}
			Updates.checkAndRun(s);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	private boolean databaseInitialized(Connection con) throws SQLException {
		try (var rs = con.getMetaData().getTables(con.getCatalog(), con.getSchema(), null, new String[] { "TABLE" })) {
			var tables = new HashSet<String>();
			while (rs.next()) {
				var table = rs.getString("TABLE_NAME").toLowerCase();
				tables.add(table);
			}
			for (var table : TABLES) {
				if (!tables.contains(table)) {
					if (!tables.isEmpty())
						throw new IllegalStateException("Schema is not empty but contains not all required tables");
					return false;
				}
			}
			return true;
		}
	}

	private void initializeDatabase(Statement s) throws SQLException, IOException {
		try (var reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("schema.sql")))) {
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

}