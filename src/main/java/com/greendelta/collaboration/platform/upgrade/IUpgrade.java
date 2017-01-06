package com.greendelta.collaboration.platform.upgrade;

import java.sql.SQLException;

interface IUpgrade {

	int fromVersion();

	void run(DbUtil dbUtil) throws SQLException;

}
