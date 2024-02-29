package com.greendelta.collaboration.io;

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
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Http;

import jakarta.servlet.http.HttpServletRequest;

public class ChangeLogWriter {

	private final static Logger log = LogManager.getLogger(ChangeLogWriter.class);

	public void generate(File file, HttpServletRequest request, Repository repo) {
		generate(file, zos -> {
			var data = renderCommits(request, repo);
			packResource(zos, "index.html", data);
			var commits = repo.commits.find().all();
			for (var commit : commits) {
				data = renderCommit(request, repo, commit.id);
				packResource(zos, commit.id + ".html", data);
				var diffs = repo.diffs.find().commit(commit).excludeCategories().withPreviousCommit();
				for (var diff : diffs) {
					if (diff.diffType != DiffType.MODIFIED && diff.diffType != DiffType.MOVED)
						continue;
					data = renderDiff(request, repo, diff);
					packResource(zos, commit.id + "-" + diff.refId + ".html", data);
				}
			}
		});
	}

	public void generate(File file, HttpServletRequest request, Repository repo, Commit commit) {
		generate(file, zos -> {
			var diffs = repo.diffs.find().commit(commit).excludeCategories().withPreviousCommit();
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
		try {
			var zos = new ZipOutputStream(new FileOutputStream(file));
			packResources(zos);
			renderer.accept(zos);
			zos.close();
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
				+ diff.newCommitId + "&compareToCommitId=" + diff.oldCommitId;
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
		var is = getClass().getResourceAsStream("/ssr/resources.zip");
		var zis = new ZipInputStream(is);
		ZipEntry entry = null;
		try {
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
		var bias = new ByteArrayInputStream(data.getBytes());
		packResource(zos, path, bias);
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
