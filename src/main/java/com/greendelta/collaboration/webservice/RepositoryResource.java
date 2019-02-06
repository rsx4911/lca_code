package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.util.BinUtils;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.webservice.util.Repositories;

@Path("public/repository")
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryResource {

	private final RepositoryService service;
	private final GroupService groupService;
	private final HistoryService historyService;

	@Inject
	public RepositoryResource(RepositoryService service, GroupService groupService, HistoryService historyService) {
		this.service = service;
		this.groupService = groupService;
		this.historyService = historyService;
	}

	@GET
	@Path("{group}/{name}")
	public Response get(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		Map<String, Object> mappedRepo = Repositories.map(repo,
				groupService.isUserNamespace(group, repo.settings.publicAccess));
		return Respond.ok(mappedRepo);
	}

	@GET
	@Path("avatar/{group}/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		byte[] avatar = service.getAvatar(group, name);
		return Respond.ok(avatar, "avatar-repository.png");
	}

	@GET
	@Path("file/{group}/{name}/{type}/{refId}/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("filename") String filename,
			@QueryParam("commitId") String commitId) throws IOException {
		Repository repo = service.get(group, name);
		commitId = getLastCommitId(repo, type, refId, commitId);
		if (commitId == null)
			return Respond.notFound(notFoundMessage(type, refId, null));
		File binFile = service.getBinFile(repo, type, refId, commitId, filename);
		if (!binFile.exists())
			return Respond.notFound(notFoundMessage(type, refId, filename));
		return Respond.ok(BinUtils.gunzip(Files.readAllBytes(binFile.toPath())));
	}

	private String getLastCommitId(Repository repo, ModelType type, String refId, String commitId) {
		Commit commit = historyService.getLastCommit(repo, type, refId, commitId);
		if (commit == null)
			return null;
		return commit.id;
	}

	private String notFoundMessage(ModelType type, String refId, String commitId) {
		return notFoundMessage(type, refId, commitId, null);
	}

	private String notFoundMessage(ModelType type, String refId, String commitId, String filename) {
		String base = "";
		if (!Strings.isNullOrEmpty(filename))
			base = "Binary file " + filename + " of ";
		base += type.name() + " " + refId + " not found";
		if (commitId == null)
			return base;
		return base + " for commit id " + commitId;
	}
}
