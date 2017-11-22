package com.greendelta.collaboration.platform.upgrade.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

class DbUtil implements Closeable {

	private final Connection connection;
	private final Statement statement;

	DbUtil(String dbPath) throws IOException {
		try {
			connection = DriverManager.getConnection("jdbc:derby:" + dbPath);
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	void update(String sql) throws SQLException {
		statement.executeUpdate(sql);
	}

	@SuppressWarnings("unchecked")
	<T> T queryField(String sql) throws SQLException {
		try (ResultSet rs = statement.executeQuery(sql)) {
			if (!rs.next())
				return null;
			return (T) rs.getObject(1);
		}
	}

	void query(String sql, Consumer<ResultSet> consumer) throws SQLException {
		try (ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				consumer.accept(rs);
			}
		}
	}

	@Override
	public void close() throws IOException {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	boolean addColumn(String table, String column, String columnDefinition) throws SQLException {
		if (columnExists(table, column))
			return false;
		update("ALTER TABLE " + table + " ADD COLUMN " + column + " " + columnDefinition);
		return true;
	}

	boolean dropColumn(String table, String column) throws SQLException {
		if (!columnExists(table, column))
			return false;
		update("ALTER TABLE " + table + " DROP COLUMN " + column);
		return true;
	}

	boolean createTable(String table, String... columnDefinitions) throws SQLException {
		if (tableExists(table))
			return false;
		if (columnDefinitions == null)
			return false;
		String sql = "CREATE TABLE " + table + "(";
		for (int i = 0; i < columnDefinitions.length; i++) {
			if (i != 0) {
				sql += ", ";
			}
			sql += columnDefinitions[i];
		}
		sql += ")";
		update(sql);
		return true;
	}

	private boolean columnExists(String table, String column) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		try (ResultSet rs = metaData.getColumns(null, null, "%", "%")) {
			while (rs.next()) {
				String tbl = rs.getString(3);
				String col = rs.getString(4);
				if (tbl.equalsIgnoreCase(table) && col.equalsIgnoreCase(column))
					return true;
			}
			return false;
		}
	}

	private boolean tableExists(String table) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		try (ResultSet rs = metaData.getTables(null, null, "%", null)) {
			while (rs.next()) {
				String tbl = rs.getString(3);
				if (table.equalsIgnoreCase(tbl))
					return true;
			}
			return false;
		}
	}

}
