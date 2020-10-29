package com.greendelta.collaboration.service;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexAction;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.search.SearchService.IndexIterator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ChangeLogService {

	private final static Logger log = LogManager.getLogger(ChangeLogService.class);
	private final HistoryService historyService;
	private final SearchService searchService;

	@Inject
	public ChangeLogService(HistoryService historyService, SearchService searchService) {
		this.historyService = historyService;
		this.searchService = searchService;
	}

	public File generate(HttpServletRequest request, Repository repo, String commitId) {
		try {
			File tmpDir = Files.createTempDirectory("lca-collaboration-changelog").toFile();
			File file = new File(tmpDir, "temp.zip");
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
			packResources(zos);
			IndexIterator iterator = searchService.getAll(repo, commitId);
			String data = renderCommit(request, repo, commitId);
			ByteArrayInputStream bias = new ByteArrayInputStream(data.getBytes());
			packResource(zos, "index.html", bias);
			while (iterator.hasNext()) {
				IndexEntry entry = iterator.next();
				if (entry.action == IndexAction.UPDATE) {
					Commit previous = historyService.getLastCommitBefore(repo, entry.type, entry.refId, commitId);
					data = renderDataset(request, repo, entry, previous);
					bias = new ByteArrayInputStream(data.getBytes());
					packResource(zos, entry.refId + ".html", bias);
				}
			}
			zos.close();
			return file;
		} catch (IOException e) {
			log.error("Error during changelog creation", e);
			return null;
		}
	}

	private String renderCommit(HttpServletRequest request, Repository repo, String commitId) throws IOException {
		String route = "/" + repo.toId() + "/commit/" + commitId;
		return renderSsr(request, route);
	}

	private String renderDataset(HttpServletRequest request, Repository repo, IndexEntry entry, Commit previousCommit)
			throws IOException {
		String route = "/" + repo.toId() + "/dataset/" + entry.type.name() + "/" + entry.refId + "?commitId="
				+ entry.commitId + "&compareToCommitId=" + previousCommit.id;
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
			packResource(zos, entry.getName(), zis);
		}
	}

	private void packResource(ZipOutputStream zos, String path, InputStream is) throws IOException {
		zos.putNextEntry(new ZipEntry(path));
		ByteStreams.copy(is, zos);
		zos.closeEntry();
	}

}
