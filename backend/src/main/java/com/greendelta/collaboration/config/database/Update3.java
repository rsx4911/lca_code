package com.greendelta.collaboration.config.database;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Update3 {

	private static final Logger log = LogManager.getLogger(Update2.class);
	private static final int UPDATE_TO = 4;
	private final Statement s;

	Update3(Statement s) {
		this.s = s;
	}

	int run() throws SQLException, IOException {
		log.info("Running update3");
		Updates.runScript(s, "update3.sql");
		return UPDATE_TO;
	}
	
}
