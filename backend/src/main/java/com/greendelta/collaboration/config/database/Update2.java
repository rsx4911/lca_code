package com.greendelta.collaboration.config.database;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Update2 {

	private static final Logger log = LogManager.getLogger(Update2.class);
	private static final int UPDATE_TO = 3;
	private final Statement s;

	Update2(Statement s) {
		this.s = s;
	}

	int run() throws SQLException, IOException {
		log.info("Running update2");
		Updates.runScript(s, "update2.sql");
		return UPDATE_TO;
	}

}
