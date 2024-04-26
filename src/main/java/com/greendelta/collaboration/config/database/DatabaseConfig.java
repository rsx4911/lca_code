package com.greendelta.collaboration.config.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig implements HibernatePropertiesCustomizer {

	private final DataSource dataSource;

	public DatabaseConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		try (var con = dataSource.getConnection()) {
			Updates.run(con);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
}