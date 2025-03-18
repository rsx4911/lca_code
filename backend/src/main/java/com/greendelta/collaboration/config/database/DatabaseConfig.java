package com.greendelta.collaboration.config.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig implements HibernatePropertiesCustomizer {

	private static final Logger log = LogManager.getLogger(DatabaseConfig.class);

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
				log.info("Initializing database");
				Updates.executeScript(s, "schema.sql");
			}
			Updates.checkAndRun(s);
		} catch (SQLException | IOException e) {
			log.error("Error updating database", e);
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

}