package com.greendelta.collaboration.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Diff;
import org.openlca.git.model.DiffType;
import org.openlca.git.util.Diffs;
import org.springframework.http.HttpStatus;

import com.greendelta.collaboration.error.WebRequestException;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Http;

public class ChangeLogWriter {

	private final static Logger log = LogManager.getLogger(ChangeLogWriter.class);

	public File generate(HttpServletRequest request, Repository repo) throws WebRequestException {
		return generate(zos -> {
			var data = renderCommits(request, repo);
			packResource(zos, "index.html", data);
			var commits = repo.commits().find().all();
			for (var commit : commits) {
				data = renderCommit(request, repo, commit.id);
				packResource(zos, commit.id + ".html", data);
				var diffs = Diffs.of(repo.gitRepo(), commit).withPreviousCommit();
				for (var diff : diffs) {
					if (diff.diffType != DiffType.MODIFIED)
						continue;
					data = renderDiff(request, repo, diff);
					packResource(zos, diff.refId + ".html", data);
				}
			}
		});
	}

	public File generate(HttpServletRequest request, Repository repo, Commit commit) throws WebRequestException {
		return generate(zos -> {
			var diffs = Diffs.of(repo.gitRepo(), commit).withPreviousCommit();
			var data = renderCommit(request, repo, commit.id);
			packResource(zos, "index.html", data);
			for (var diff : diffs) {
				if (diff.diffType != DiffType.MODIFIED)
					continue;
				data = renderDiff(request, repo, diff);
				packResource(zos, diff.refId + ".html", data);
			}
		});
	}

	private File generate(Renderer renderer) throws WebRequestException {
		try {
			var tmpDir = Files.createTempDirectory("lca-collaboration-changelog").toFile();
			var file = new File(tmpDir, "temp.zip");
			var zos = new ZipOutputStream(new FileOutputStream(file));
			packResources(zos);
			renderer.render(zos);
			zos.close();
			return file;
		} catch (IOException e) {
			log.error("Error during changelog creation", e);
			return null;
		}
	}

	private String renderCommits(HttpServletRequest request, Repository repo) throws WebRequestException {
		var route = "/" + repo.path() + "/commits";
		return renderSsr(request, route);
	}

	private String renderCommit(HttpServletRequest request, Repository repo, String commitId)
			throws WebRequestException {
		var route = "/" + repo.path() + "/commit/" + commitId;
		return renderSsr(request, route);
	}

	private String renderDiff(HttpServletRequest request, Repository repo, Diff diff)
			throws WebRequestException {
		var route = "/" + repo.path() + "/dataset/" + diff.type.name() + "/" + diff.refId + "?commitId="
				+ diff.newCommitId + "&compareToCommitId=" + diff.oldCommitId;
		return renderSsr(request, route);
	}

	private String renderSsr(HttpServletRequest request, String route) throws WebRequestException {
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
		} catch (Exception e) {
			throw new WebRequestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private void packResources(ZipOutputStream zos) throws WebRequestException {
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
			throw new WebRequestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private void packResource(ZipOutputStream zos, String path, String data) throws WebRequestException {
		var bias = new ByteArrayInputStream(data.getBytes());
		packResource(zos, path, bias);
	}

	private void packResource(ZipOutputStream zos, String path, InputStream is) throws WebRequestException {
		try {
			zos.putNextEntry(new ZipEntry(path));
			is.transferTo(zos);
			zos.closeEntry();
		} catch (IOException e) {
			throw new WebRequestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private interface Renderer {

		void render(ZipOutputStream zos) throws WebRequestException;

	}

}
