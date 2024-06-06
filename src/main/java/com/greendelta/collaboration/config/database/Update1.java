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

class Update1 {

	private static final int UPDATE_TO = 2;
	private final Statement s;

	Update1(Statement s) {
		this.s = s;
	}

	int run() throws SQLException, IOException {
		Updates.runScript(s, "update1.sql");
		createReleases();
		s.executeUpdate("UPDATE setting SET name = 'RELEASES_ENABLED' WHERE name = 'PUBLIC_REPOSITORIES_ENABLED'");
		return UPDATE_TO;
	}

	private void createReleases() throws SQLException, IOException {
		var gitDir = getGitDir();
		if (gitDir == null || !gitDir.exists())
			return;
		var repos = getPublicRepositories();
		var nextId = 1;
		for (var repositoryPath : repos) {
			insertRelease(gitDir, repositoryPath, nextId++);
		}
		s.executeUpdate("DELETE FROM setting WHERE name = 'PUBLIC_ACCESS' OR name = 'JSON_FILE_GENERATION'");
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
				"SELECT owner FROM setting WHERE name = 'PUBLIC_ACCESS'")) {
			while (rs.next()) {
				publicRepositories.add(rs.getString("owner").trim());
			}
		}
		return publicRepositories;
	}

	private void insertRelease(File gitDir, String repositoryPath, long id) throws SQLException, IOException {
		var commitId = getLatestCommitId(new File(gitDir, repositoryPath));
		if (Strings.nullOrEmpty(commitId))
			return;
		var properties = getProperties(repositoryPath);
		if (Strings.nullOrEmpty(properties.get("version"))) {
			properties.put("version", "Public");
		}
		if (Strings.nullOrEmpty(properties.get("label"))) {
			properties.put("label", repositoryPath.substring(repositoryPath.indexOf("/") + 1));
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
			sql += ", " + (value != null ? "'" + value + "'" : "null");
		}
		sql += ")";
		s.executeUpdate(sql);
		var cachedJsonFile = new File(gitDir, "cached-json.zip");
		if (cachedJsonFile.exists()) {
			var newFile = new File(gitDir, "cached-json-" + commitId + ".zip");
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
			if (head == null)
				return null;
			var commit = walk.parseCommit(head);
			if (commit == null)
				return null;
			return commit.getName();
		} catch (IOException e) {
			return null;
		}
	}

}
