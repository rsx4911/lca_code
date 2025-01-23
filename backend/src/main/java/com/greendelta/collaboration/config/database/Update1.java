package com.greendelta.collaboration.config.database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.openlca.util.Dirs;
import org.openlca.util.Strings;

class Update1 extends Update {

	Update1(Statement s) {
		super(s, 1);
	}

	@Override
	protected void executeCode() throws SQLException, IOException {
		createReleases();
		updateSettings();
	}

	private void createReleases() throws SQLException, IOException {
		log.info("Creating release info data");
		var gitDir = getGitDir();
		if (gitDir == null || !gitDir.exists()) {
			log.warn("Could not find git directory " + (gitDir == null ? "null" : gitDir.getAbsolutePath()));
			return;
		}
		var repos = getPublicRepositories();
		var nextId = 1;
		for (var repositoryPath : repos) {
			insertRelease(gitDir, repositoryPath, nextId++);
		}
	}

	private File getGitDir() throws SQLException {
		try (var rs = s.executeQuery(
				"SELECT value FROM setting WHERE name = 'REPOSITORY_PATH'")) {
			if (!rs.next())
				return null;
			return new File(rs.getString("value"));
		}
	}

	private Set<String> getPublicRepositories() throws SQLException {
		var publicRepositories = new HashSet<String>();
		try (var rs = s.executeQuery(
				"SELECT owner FROM setting WHERE name = 'PUBLIC_ACCESS' AND lower(value) = 'true'")) {
			while (rs.next()) {
				publicRepositories.add(rs.getString("owner").trim());
			}
		}
		return publicRepositories;
	}

	private void insertRelease(File gitDir, String repositoryPath, long id) throws SQLException, IOException {
		log.info("Inserting release info of " + repositoryPath);
		var commitId = getLatestCommitId(new File(gitDir, repositoryPath));
		if (Strings.nullOrEmpty(commitId))
			return;
		var properties = getProperties(repositoryPath);
		if (Strings.nullOrEmpty(properties.get("VERSION"))) {
			properties.put("VERSION", "Public");
		}
		if (Strings.nullOrEmpty(properties.get("LABEL"))) {
			properties.put("LABEL", repositoryPath.substring(repositoryPath.indexOf("/") + 1));
		}
		var fields = Arrays.asList("label", "version", "description", "source_info", "contact_info", "project_info",
				"project_funding", "appropriate_use", "dq_assessment", "citation", "type_of_data");
		var sql = "INSERT INTO release_info(id, repository_path, commit_id";
		for (var field : fields) {
			sql += ", " + field;
		}
		sql += ") VALUES (" + id + ", '" + repositoryPath + "', '" + commitId + "'";
		for (var field : fields) {
			var value = properties.get(field.toUpperCase());
			sql += ", " + (value != null ? "'" + value.replace("'", "''") + "'" : "null");
		}
		sql += ")";
		update(sql);
		var cachedJsonFile = new File(gitDir, "cached-json.zip");
		if (cachedJsonFile.exists()) {
			var newFile = new File(gitDir, "cached-json-" + commitId + ".zip");
			log.info("Moving cached json file "
					+ "from " + cachedJsonFile.getAbsolutePath()
					+ " to " + newFile.getAbsolutePath());
			Dirs.move(cachedJsonFile.toPath(), newFile.toPath());
		}
	}

	private Map<String, String> getProperties(String repositoryPath) throws SQLException {
		var properties = new HashMap<String, String>();
		try (var rs = s.executeQuery("SELECT name, value FROM setting WHERE owner = '" + repositoryPath + "'")) {
			while (rs.next()) {
				properties.put(rs.getString("name"), rs.getString("value"));
			}
		}
		return properties;
	}

	private String getLatestCommitId(File gitDir) throws IOException {
		var repo = new FileRepository(gitDir);
		try (var walk = new RevWalk(repo)) {
			var head = repo.resolve("refs/heads/main");
			if (head == null) {
				log.warn("Could not find refs/heads/main in " + gitDir.getAbsolutePath());
				return null;
			}
			var commit = walk.parseCommit(head);
			if (commit == null) {
				log.warn("Could not find head commit of " + gitDir.getAbsolutePath());
				return null;
			}
			return commit.getName();
		} catch (IOException e) {
			log.error("Error trying to access latest commit id of " + gitDir.getAbsolutePath(), e);
			return null;
		}
	}

	private void updateSettings() throws SQLException {
		log.info("Updating settings table");
		update("DELETE FROM setting WHERE name = 'PUBLIC_ACCESS' OR name = 'JSON_FILE_GENERATION' OR name = 'SEARCH_COMMIT_ID'");
		update("UPDATE setting SET name = 'RELEASES_ENABLED' WHERE name = 'PUBLIC_REPOSITORY_ENABLED'");
		update("UPDATE setting SET name = 'PRIVATE', type = 'SEARCH_INDEX' WHERE name = 'INDEX_NAME'");
		update("UPDATE setting SET name = 'IO_DATA', type = 'SEARCH_INDEX' WHERE name = 'IO_DATA_INDEX_NAME'");
		insertPublicIndexSetting();
	}

	private void insertPublicIndexSetting() throws SQLException {
		try (var rs = s
				.executeQuery("SELECT value FROM setting WHERE name = 'RELEASES_ENABLED' AND lower(value) = 'true'")) {
			if (!rs.next())
				return;
		}
		try (var rs = s.executeQuery("SELECT value FROM setting WHERE name = 'SEARCH_ENABLED'")) {
			if (!rs.next())
				return;
		}
		String indexName = null;
		try (var rs = s.executeQuery("SELECT value FROM setting WHERE name = 'PRIVATE'")) {
			if (!rs.next())
				return;
			indexName = rs.getString("value");
		}
		long id = 1;
		try (var rs = s.executeQuery("SELECT id FROM setting ORDER BY id DESC")) {
			if (rs.next()) {
				id = rs.getLong("id") + 1;
			}
		}
		update("INSERT INTO setting(id, type, name, value)"
				+ " VALUES (" + id + ", 'SEARCH_INDEX', 'PUBLIC', '" + indexName + "-public" + "')");
	}

	private void update(String sql) throws SQLException {
		log.info("  " + sql);
		s.executeUpdate(sql);
	}

}
