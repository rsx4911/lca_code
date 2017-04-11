package com.greendelta.collaboration.platform.upgrade.database;

import java.sql.SQLException;

class Upgrade2 implements IUpgrade {

	@Override
	public int fromVersion() {
		return 2;
	}

	@Override
	public void run(DbUtil dbUtil) throws SQLException {
		dbUtil.createTable("comments",
				"id BIGINT NOT NULL",
				"repository_path VARCHAR(255) NOT NULL",
				"ds_type VARCHAR(255) NOT NULL",
				"ds_ref_id VARCHAR(36) NOT NULL",
				"ds_commit_id VARCHAR(36) NOT NULL",
				"ds_path VARCHAR(4000) NOT NULL",
				"f_user BIGINT",
				"date TIMESTAMP NOT NULL",
				"text VARCHAR(4000)",
				"restricted_to_role VARCHAR(255)",
				"f_reply_to BIGINT");
	}

}
