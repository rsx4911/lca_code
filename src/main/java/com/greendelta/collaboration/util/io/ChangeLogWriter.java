package com.greendelta.collaboration.util.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.DiffReference;
import org.openlca.cloud.api.git.DiffType;

import com.google.common.io.ByteStreams;
import com.greendelta.collaboration.service.Repository;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ChangeLogWriter {

	private final static Logger log = LogManager.getLogger(ChangeLogWriter.class);

	public File generate(HttpServletRequest request, Repository repo) {
		return generate(zos -> {
			String data = renderCommits(request, repo);
			packResource(zos, "index.html", data);
			List<Commit> commits = repo.commits.find().all();
			Commit previous = null;
			for (Commit commit : commits) {
				data = renderCommit(request, repo, commit.id);
				packResource(zos, commit.id + ".html", data);
				List<DiffReference> diffs = repo.references.diff().between(previous, commit).all();
				for (DiffReference diff : diffs) {
					if (diff.type != DiffType.MODIFIED)
						continue;
					data = renderDiff(request, repo, diff);
					packResource(zos, diff.ref().refId + ".html", data);
				}
				previous = commit;
			}
		});
	}

	public File generate(HttpServletRequest request, Repository repo, Commit commit) {
		return generate(zos -> {
			List<DiffReference> diffs = repo.references.diff().withPrevious(commit).all();
			String data = renderCommit(request, repo, commit.id);
			packResource(zos, "index.html", data);
			for (DiffReference diff : diffs) {
				if (diff.type != DiffType.MODIFIED)
					continue;
				data = renderDiff(request, repo, diff);
				packResource(zos, diff.ref().refId + ".html", data);
			}
		});
	}

	private File generate(Renderer renderer) {
		try {
			File tmpDir = Files.createTempDirectory("lca-collaboration-changelog").toFile();
			File file = new File(tmpDir, "temp.zip");
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
			packResources(zos);
			renderer.render(zos);
			zos.close();
			return file;
		} catch (IOException e) {
			log.error("Error during changelog creation", e);
			return null;
		}
	}

	private String renderCommits(HttpServletRequest request, Repository repo) throws IOException {
		String route = "/" + repo.toId() + "/commits";
		return renderSsr(request, route);
	}

	private String renderCommit(HttpServletRequest request, Repository repo, String commitId) throws IOException {
		String route = "/" + repo.toId() + "/commit/" + commitId;
		return renderSsr(request, route);
	}

	private String renderDiff(HttpServletRequest request, Repository repo, DiffReference diff)
			throws IOException {
		String route = "/" + repo.toId() + "/dataset/" + diff.right.type.name() + "/" + diff.right.refId + "?commitId="
				+ diff.right.commitId + "&compareToCommitId=" + diff.left.commitId;
		return renderSsr(request, route);
	}

	private String renderSsr(HttpServletRequest request, String route) throws IOException {
		String scheme = request.getScheme();
		try {
			Client client = Client.create();
			String sessionId = request.getSession().getId();
			route += route.contains("?") ? "&" : "?";
			route += "sessionid=" + sessionId;
			route += "&scheme=" + scheme;
			route += "&standalone=true";
			WebResource webResource = client.resource("http://localhost:3000" + route);
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200)
				throw new IOException("Request failed with status " + response.getStatus() + " for: " + route);
			return response.getEntity(String.class);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private void packResources(ZipOutputStream zos) throws IOException {
		InputStream is = getClass().getResourceAsStream("/ssr/resources.zip");
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry = null;
		while ((entry = zis.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.contains("styles") && name.endsWith(".css")) {
				name = name.substring(0, name.lastIndexOf("styles")) + "styles.css";
			}
			packResource(zos, name, zis);
		}
	}

	private void packResource(ZipOutputStream zos, String path, String data) throws IOException {
		ByteArrayInputStream bias = new ByteArrayInputStream(data.getBytes());
		packResource(zos, path, bias);
	}

	private void packResource(ZipOutputStream zos, String path, InputStream is) throws IOException {
		zos.putNextEntry(new ZipEntry(path));
		ByteStreams.copy(is, zos);
		zos.closeEntry();
	}

	private interface Renderer {

		void render(ZipOutputStream zos) throws IOException;

	}

}
