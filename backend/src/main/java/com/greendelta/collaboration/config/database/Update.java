package com.greendelta.collaboration.config.database;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Update {

	protected final Logger log = LogManager.getLogger(Update.class);
	protected final Statement s;
	private final int update;

	Update(Statement s, int update) {
		this.s = s;
		this.update = update;
	}

	int run() throws SQLException, IOException {
		log.info("Running update" + update);
		if (executeScript()) {
			Updates.executeScript(s, "update" + update + ".sql");
		}
		executeCode();
		return update + 1;
	}

	protected boolean executeScript() {
		return true;
	}

	protected void executeCode() throws SQLException, IOException {
		// subclasses may override
	}

}
