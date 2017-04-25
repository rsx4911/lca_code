package com.greendelta.collaboration.platform.upgrade.database;

import java.sql.SQLException;

public class Upgrade3 implements IUpgrade {

	@Override
	public int fromVersion() {
		return 3;
	}

	@Override
	public void run(DbUtil dbUtil) throws SQLException {
		dbUtil.createTable("task_assignments",
				"id BIGINT NOT NULL",
				"comment VARCHAR(4000)",
				"start_date TIMESTAMP NOT NULL",
				"end_date TIMESTAMP",
				"iteration BIGINT NOT NULL DEFAULT 1",
				"canceled BOOLEAN NOT NULL DEFAULT false",
				"f_assigned_to BIGINT NOT NULL",
				"f_ended_by BIGINT",
				"f_task BIGINT");
		dbUtil.createTable("reviews",
				"id BIGINT NOT NULL",
				"name VARCHAR(255)",
				"repository_path VARCHAR(255) NOT NULL",
				"comment VARCHAR(4000)",
				"start_date TIMESTAMP NOT NULL",
				"end_date TIMESTAMP",
				"state VARCHAR(255)",
				"f_initiator BIGINT NOT NULL");
		dbUtil.createTable("review_references",
				"id BIGINT NOT NULL",
				"ds_type VARCHAR(255) NOT NULL",
				"ds_ref_id VARCHAR(36) NOT NULL",
				"ds_commit_id VARCHAR(36) NOT NULL",
				"ds_name VARCHAR(255) NOT NULL",
				"f_reviewer BIGINT",
				"f_review BIGINT");
		// Remove NOT NULL constraint
		dbUtil.update("ALTER TABLE messages ADD COLUMN f_team2 BIGINT");
		dbUtil.update("UPDATE messages SET f_team2 = f_team");
		dbUtil.update("ALTER TABLE messages DROP COLUMN f_team");
		dbUtil.update("RENAME COLUMN messages.f_team2 TO f_team");
	}
}
