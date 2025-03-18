package com.greendelta.collaboration.search;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.http.HttpHost;
import org.openlca.git.model.Commit;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.util.Strings;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.os.OsRestClient;
import com.mysql.cj.jdbc.Driver;

public class StandaloneReindexing {

	private static final String TAGS_QUERY = "SELECT value FROM setting "
			+ "WHERE type = 'REPOSITORY_SETTING'"
			+ "AND name = 'TAGS' AND owner = ?";

	private static final String RELEASES_QUERY = "SELECT commit_id, tags FROM release_info "
			+ "WHERE repository_path = ?";

	private static final String SETTINGS_QUERY = "SELECT name, value FROM setting "
			+ "WHERE owner IS NULL "
			+ "AND type IN ("
			+ "'SERVER_SETTING', "
			+ "'SEARCH_SETTING', "
			+ "'SEARCH_INDEX')"
			+ "AND name IN ("
			+ "'RELEASES_ENABLED', "
			+ "'USAGE_SEARCH_ENABLED', "
			+ "'HOST', "
			+ "'PORT', "
			+ "'SCHEMA', "
			+ "'PUBLIC', "
			+ "'PRIVATE', "
			+ "'PUBLIC_USAGE', "
			+ "'PRIVATE_USAGE', "
			+ "'REPOSITORY_PATH')";

	private StandaloneReindexing() {
	}

	public static void main(String[] args) throws IOException, SQLException {
		var in = parseInput(args);
		var driver = new Driver();
		DriverManager.registerDriver(driver);
		try (var connection = getConnection(in);
				var tagsStatement = connection.prepareStatement(TAGS_QUERY);
				var releasesStatement = connection.prepareStatement(RELEASES_QUERY)) {
			var settings = getSettings(connection);
			var client = getRestClient(settings);

			var repos = getRepos(settings.gitDir);
			try {
				var publicClient = createClient(client, settings.publicIndex);
				var privateClient = createClient(client, settings.privateIndex);
				var publicUsageClient = createClient(client, settings.publicUsageIndex);
				var privateUsageClient = createClient(client, settings.privateUsageIndex);

				var publicIndex = new Index(publicClient);
				var privateIndex = new Index(privateClient);
				var searchIndices = new Index(publicClient, privateClient);

				var publicUsageIndex = new UsageIndex(publicUsageClient);
				var privateUsageIndex = new UsageIndex(privateUsageClient);
				var usageIndices = new UsageIndex(publicUsageClient, privateUsageClient);

				System.out.println("Clearing indices");
				publicIndex.clear();
				privateIndex.clear();
				publicUsageIndex.clear();
				privateUsageIndex.clear();

				System.out.println(repos.size() + " repositories found in git");
				var count = 0;
				for (var repo : repos) {
					var head = repo.repo.commits.head();
					var tags = getTags(tagsStatement, repo);
					var release = getLatestRelease(publicClient, releasesStatement, repo);
					var m = repo.path + " (" + ++count + "/" + repos.size() + ")";
					if (release == null) {
						System.out.println(
								"[Private search] Indexing " + m);
						privateIndex.index(repo.path, repo.repo, tags, null, head);
					} else {
						if (head.equals(release.commit)) {
							System.out.println("[Public search, Private search] Indexing " + m);
							searchIndices.index(repo.path, repo.repo, release.tags, null, release.commit);
						} else {
							System.out.println("[Public search] Indexing " + m);
							publicIndex.index(repo.path, repo.repo, release.tags, null, release.commit);
							System.out.println("[Private search] Indexing " + m);
							privateIndex.index(repo.path, repo.repo, tags, null, head);
						}
					}
				}
				if (publicUsageClient != null || privateUsageClient != null) {
					count = 0;
					for (var repo : repos) {
						var head = repo.repo.commits.head();
						var release = getLatestRelease(publicUsageClient, releasesStatement, repo);
						var m = repo.path + " (" + ++count + "/" + repos.size() + ")";
						if (release == null) {
							System.out.println("[Private usage search] Indexing " + m);
							privateUsageIndex.index(repo.path, repo.repo, null, head);
						} else {
							if (head.equals(release.commit)) {
								System.out.println("[Public usage search, Private usage search] Indexing " + m);
								usageIndices.index(repo.path, repo.repo, null, release.commit);
							} else {
								System.out.println("[Public usage search] Indexing " + m);
								publicUsageIndex.index(repo.path, repo.repo, null, release.commit);
								System.out.println("[Private usage search] Indexing " + m);
								privateUsageIndex.index(repo.path, repo.repo, null, head);
							}
						}
					}
				}
			} finally {
				if (client != null) {
					client.close();
				}
				repos.forEach(r -> r.repo.close());
			}
		} finally {
			DriverManager.deregisterDriver(driver);
		}
	}

	private static Input parseInput(String[] args) {
		if (args.length % 2 != 0)
			throw new IllegalArgumentException("Invalid arguments");
		var map = new HashMap<String, Object>();
		for (var i = 0; i < args.length; i += 2) {
			var key = args[i];
			if (!key.startsWith("--"))
				throw new IllegalArgumentException("Invalid arguments: " + key);
			var value = args[i + 1];
			map.put(key.substring(2), value);
		}
		var sqlHost = get(map, "sqlHost", Maps::getString, "localhost");
		var sqlPort = get(map, "sqlPort", Maps::getInteger, 3306);
		var sqlUser = get(map, "sqlUser", Maps::getString, "collaboration-server");
		var sqlPass = get(map, "sqlPass", Maps::getString, "collaboration-server");
		var sqlDb = get(map, "sqlDb", Maps::getString, "collaboration-server");
		return new Input(sqlHost, sqlPort, sqlUser, sqlPass, sqlDb);
	}

	private static RestHighLevelClient getRestClient(Settings in) throws IOException {
		var client = new RestHighLevelClient(RestClient.builder(
				new HttpHost(in.osHost, in.osPport, in.osSchema),
				new HttpHost(in.osHost, in.osPport + 1, in.osSchema)));
		if (!client.ping(RequestOptions.DEFAULT))
			throw new IOException("Could not ping search cluster");
		return client;
	}

	private static SearchClient createClient(RestHighLevelClient restClient, String index) {
		if (index == null)
			return null;
		return new OsRestClient(restClient, index);
	}

	private static List<Repo> getRepos(File gitDir) throws IOException {
		var repos = new ArrayList<Repo>();
		for (var group : gitDir.listFiles()) {
			if (!group.isDirectory())
				continue;
			for (var repoDir : group.listFiles()) {
				if (!repoDir.isDirectory())
					continue;
				if (!new File(repoDir, "HEAD").exists())
					continue;
				var repo = new OlcaRepository(repoDir);
				if (repo.commits.head() == null)
					continue;
				repos.add(new Repo(group.getName() + "/" + repoDir.getName(), repo));
			}
		}
		return repos;
	}

	private static Connection getConnection(Input in) throws SQLException {
		var url = "jdbc:mysql://" + in.sqlHost + ":" + in.sqlPort + "/" + in.sqlDb;
		return DriverManager.getConnection(url, in.sqlUser, in.sqlPass);

	}

	private static Release getLatestRelease(SearchClient publicClient, PreparedStatement statement, Repo repo)
			throws IOException, SQLException {
		if (publicClient == null)
			return null;
		statement.setString(1, repo.path);
		var releaseTags = new HashMap<String, String>();
		try (var result = statement.executeQuery()) {
			while (result.next()) {
				releaseTags.put(result.getString("commit_id"), result.getString("tags"));
			}
		}
		var releases = repo.repo.commits.find().all().stream()
				.filter(commit -> releaseTags.keySet().contains(commit.id))
				.toList();
		if (releases.isEmpty())
			return null;
		var commit = releases.get(releases.size() - 1);
		var tags = releaseTags.get(commit.id);
		return new Release(commit, parseTags(tags));
	}

	private static List<String> getTags(PreparedStatement statement, Repo repo) throws IOException, SQLException {
		statement.setString(1, repo.path);
		try (var result = statement.executeQuery()) {
			if (!result.next())
				return null;
			var tags = result.getString(1);
			if (Strings.nullOrEmpty(tags))
				return null;
			return parseTags(tags);
		}
	}

	private static List<String> parseTags(String tags) throws IOException {
		if (Strings.nullOrEmpty(tags))
			return null;
		return new ObjectMapper().readValue(tags, new TypeReference<List<String>>() {
		});
	}

	private static Settings getSettings(Connection connection) throws SQLException {
		var map = new HashMap<String, Object>();
		try (var statement = connection.createStatement();
				var result = statement.executeQuery(SETTINGS_QUERY)) {
			while (result.next()) {
				map.put(result.getString("name"), result.getString("value"));
			}
		}
		var osHost = get(map, "HOST", Maps::getString, "localhost");
		var osPort = get(map, "PORT", Maps::getInteger, 9200);
		var osSchema = get(map, "SCHEMA", Maps::getString, "http");
		var publicIndex = getIf(map, "PUBLIC", Maps::getString, "collaboration-server-public",
				"RELEASES_ENABLED");
		var privateIndex = Maps.getString(map, "PRIVATE");
		var publicUsageIndex = getIf(map, "PUBLIC_USAGE", Maps::getString, "collaboration-server-usage-public",
				"USAGE_SEARCH_ENABLED", "RELEASES_ENABLED");
		var privateUsageIndex = getIf(map, "PRIVATE_USAGE", Maps::getString, "collaboration-server-usage",
				"USAGE_SEARCH_ENABLED");
		if (Strings.nullOrEmpty(publicIndex) && Strings.nullOrEmpty(privateIndex)
				&& Strings.nullOrEmpty(publicUsageIndex) && Strings.nullOrEmpty(privateUsageIndex))
			throw new IllegalArgumentException("Missing argument of: public, private or usage)");
		var gitPath = get(map, "REPOSITORY_PATH", Maps::getString);
		var gitDir = new File(gitPath);
		if (!gitDir.exists() || !gitDir.isDirectory() || gitDir.listFiles() == null
				|| gitDir.listFiles().length == 0)
			throw new IllegalArgumentException("Missing or invalid argument: git");
		return new Settings(osHost, osPort, osSchema, publicIndex, privateIndex, publicUsageIndex, privateUsageIndex,
				gitDir);
	}

	private static <T> T get(Map<String, Object> args, String param, BiFunction<Map<String, Object>, String, T> get) {
		var value = get.apply(args, param);
		if (value != null)
			return value;
		throw new IllegalArgumentException("Missing argument: " + param);
	}

	private static <T> T getIf(Map<String, Object> args, String param, BiFunction<Map<String, Object>, String, T> get,
			T defaultValue, String... ifParams) {
		for (var ifParam : ifParams)
			if (!Maps.getBoolean(args, ifParam))
				return null;
		var value = get.apply(args, param);
		if (value != null)
			return value;
		if (defaultValue == null)
			return null;
		System.out.println("No value for parameter " + param + " provided, using default value: " + defaultValue);
		return defaultValue;
	}

	private static <T> T get(Map<String, Object> args, String param, BiFunction<Map<String, Object>, String, T> get,
			T defaultValue) {
		var value = get.apply(args, param);
		if (value != null && !Objects.equal(value, 0))
			return value;
		if (defaultValue == null)
			return null;
		System.out.println("No value for parameter " + param + " provided, using default value: " + defaultValue);
		return defaultValue;
	}

	public SearchResult<Map<String, Object>> query(SearchClient client, String path, String refId, String field,
			int page, int pageSize,
			String filter) {
		var query = new SearchQueryBuilder();
		query.page(page);
		query.pageSize(pageSize);
		query.filter("path", SearchFilterValue.term(path));
		query.fields("type", "refId", "name", "processType", "flowType");
		if (Strings.nullOrEmpty(field)) {
			field = "others";
		}
		query.filter(field, SearchFilterValue.term(refId));
		if (!Strings.nullOrEmpty(filter)) {
			query.filter("name", SearchFilterValue.wildcard("*" + filter + "*"));
		}
		return client.search(query.build());
	}

	private record Input(String sqlHost, int sqlPort, String sqlUser, String sqlPass, String sqlDb) {
	}

	private record Settings(String osHost, int osPport, String osSchema, String publicIndex, String privateIndex,
			String publicUsageIndex, String privateUsageIndex, File gitDir) {
	}

	private record Repo(String path, OlcaRepository repo) {
	}

	private record Release(Commit commit, List<String> tags) {
	}

}
