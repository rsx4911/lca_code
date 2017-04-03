package com.greendelta.collaboration.platform.upgrade.database;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Upgrades {

	private static final Logger log = LoggerFactory.getLogger(Upgrades.class);
	private static final IUpgrade[] upgrades = { new Upgrade1() , new Upgrade2()};

	public static void run(String dbPath) {
		try (DbUtil dbUtil = new DbUtil(dbPath)) {
			int version = getVersion(dbUtil);
			for (IUpgrade upgrade : upgrades) {
				if (upgrade.fromVersion() != version)
					continue;
				try {
					upgrade.run(dbUtil);
					version++;
				} catch (SQLException e) {
					log.error("Error running " + upgrade.getClass().getSimpleName(), e);
				}
			}
			setVersion(dbUtil, version);
		} catch (IOException e) {
			log.error("Error upgrading database", e);
		}
	}

	private static int getVersion(DbUtil dbUtil) {
		try {
			return dbUtil.queryField("SELECT version FROM version");
		} catch (SQLException e) {
			log.error("Error checking for db version", e);
			return 0;
		}
	}

	private static void setVersion(DbUtil dbUtil, int version) {
		try {
			dbUtil.update("UPDATE version SET version = " + version);
		} catch (SQLException e) {
			log.error("Error updating db version", e);
		}
	}

}
