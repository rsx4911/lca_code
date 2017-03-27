package com.greendelta.collaboration.platform.upgrade;

import java.sql.SQLException;

class Upgrade1 implements IUpgrade {

	@Override
	public int fromVersion() {
		return 0;
	}

	@Override
	public void run(DbUtil dbUtil) throws SQLException {
		dbUtil.addColumn("users", "two_factor_secret", "VARCHAR(255)");
		dbUtil.addColumn("users", "messaging_enabled", "BOOLEAN NOT NULL DEFAULT true");
		dbUtil.addColumn("users", "messaging_restricted", "BOOLEAN NOT NULL DEFAULT false");
		dbUtil.addColumn("users", "show_online_status", "BOOLEAN NOT NULL DEFAULT true");
		dbUtil.addColumn("users", "show_read_receipt", "BOOLEAN NOT NULL DEFAULT true");
		dbUtil.createTable("blocked_users",
				"f_user BIGINT NOT NULL",
				"f_blocked BIGINT NOT NULL");
		dbUtil.createTable("messages",
				"id BIGINT NOT NULL",
				"f_from_user BIGINT NOT NULL",
				"f_to_user BIGINT NOT NULL",
				"f_team BIGINT NOT NULL",
				"date TIMESTAMP NOT NULL",
				"text VARCHAR(4000) NOT NULL",
				"read_date TIMESTAMP",
				"show_read_receipt BOOLEAN NOT NULL DEFAULT false");
		if (!dbUtil.createTable("version", "version INTEGER NOT NULL DEFAULT 1"))
			return;
		dbUtil.update("INSERT INTO version VALUES (1)");
	}
}
