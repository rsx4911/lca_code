package com.greendelta.collaboration.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.util.Http;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ChangeLogService {

	private final static Logger log = LogManager.getLogger(ChangeLogService.class);
	private final HistoryService historyService;

	public ChangeLogService(HistoryService historyService) {
		this.historyService = historyService;
	}

	public void generate(File file, HttpServletRequest request, Repository repo) {
		generate(file, zos -> {
			var data = renderCommits(request, repo);
			packResource(zos, "index.html", data);
			var commits = historyService.getAccessibleCommits(repo);
			for (var i = 0; i < commits.size(); i++) {
				var commit = commits.get(i);
				var previousCommit = i != 0
						? commits.get(i - 1)
						: null;
				data = renderCommit(request, repo, commit.id);
				packResource(zos, commit.id + ".html", data);
				var diffs = repo.diffs.find().commit(previousCommit).excludeCategories().with(commit);
				for (var diff : diffs) {
					if (diff.diffType != DiffType.MODIFIED && diff.diffType != DiffType.MOVED)
						continue;
					data = renderDiff(request, repo, diff);
					packResource(zos, commit.id + "-" + diff.refId + ".html", data);
				}
			}
		});
	}

	public void generate(File file, HttpServletRequest request, Repository repo, String commitId) {
		generate(file, zos -> {
			var commit = historyService.getAccessibleCommit(repo, commitId);
			if (commit == null)
				throw Response.notFound("Could not find commit with id " + commitId);
			var previousCommit = historyService.getLatestAccessibleCommit(repo, options -> options.before(commit.id));
			var diffs = repo.diffs.find().commit(previousCommit).excludeCategories().with(commit);
			var data = renderCommit(request, repo, commit.id);
			packResource(zos, "index.html", data);
			for (var diff : diffs) {
				if (diff.diffType != DiffType.MODIFIED && diff.diffType != DiffType.MOVED)
					continue;
				data = renderDiff(request, repo, diff);
				packResource(zos, commit.id + "-" + diff.refId + ".html", data);
			}
		});
	}

	private void generate(File file, Consumer<ZipOutputStream> renderer) {
		try (var zos = new ZipOutputStream(new FileOutputStream(file))) {
			packResources(zos);
			renderer.accept(zos);
		} catch (IOException e) {
			log.error("Error during changelog creation", e);
		}
	}

	private String renderCommits(HttpServletRequest request, Repository repo) {
		var route = "/" + repo.path() + "/commits";
		return renderSsr(request, route);
	}

	private String renderCommit(HttpServletRequest request, Repository repo, String commitId) {
		var route = "/" + repo.path() + "/commit/" + commitId;
		return renderSsr(request, route);
	}

	private String renderDiff(HttpServletRequest request, Repository repo, Diff diff) {
		var route = "/" + repo.path() + "/dataset/" + diff.type.name() + "/" + diff.refId + "?commitId="
				+ diff.newRef.commitId + "&compareToCommitId=" + diff.oldRef.commitId;
		return renderSsr(request, route);
	}

	private String renderSsr(HttpServletRequest request, String route) {
		var scheme = request.getScheme();
		try {
			var client = HttpClientBuilder.create().build();
			var sessionId = request.getSession().getId();
			route += route.contains("?") ? "&" : "?";
			route += "sessionid=" + sessionId;
			route += "&scheme=" + scheme;
			route += "&standalone=true";
			var response = Http.execute(client, new HttpGet("http://localhost:3000" + route));
			return Http.getString(response);
		} catch (IOException e) {
			throw Response.error(e.getMessage());
		}
	}

	private void packResources(ZipOutputStream zos) {
		try (var is = getClass().getResourceAsStream("/ssr/resources.zip");
				var zis = new ZipInputStream(is)) {
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				var name = entry.getName();
				if (name.contains("styles") && name.endsWith(".css")) {
					name = name.substring(0, name.lastIndexOf("styles")) + "styles.css";
				}
				packResource(zos, name, zis);
			}
		} catch (IOException e) {
			throw Response.error(e.getMessage());
		}
	}

	private void packResource(ZipOutputStream zos, String path, String data) {
		try (var bias = new ByteArrayInputStream(data.getBytes())) {
			packResource(zos, path, bias);
		} catch (IOException e) {
			throw Response.error(e.getMessage());
		}
	}

	private void packResource(ZipOutputStream zos, String path, InputStream is) {
		try {
			zos.putNextEntry(new ZipEntry(path));
			is.transferTo(zos);
			zos.closeEntry();
		} catch (IOException e) {
			throw Response.error(e.getMessage());
		}
	}

}
