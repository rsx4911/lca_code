package com.greendelta.collaboration.platform.upgrade.database;

import java.sql.SQLException;

public class Upgrade4 implements IUpgrade {

	@Override
	public int fromVersion() {
		return 4;
	}

	@Override
	public void run(DbUtil dbUtil) throws SQLException {
		dbUtil.addColumn("comments", "approved", "BOOLEAN NOT NULL DEFAULT false");
		dbUtil.update("UPDATE comments SET approved = (f_approved_by IS NOT NULL)");
		dbUtil.dropColumn("comments", "f_approved_by");
	}
}
