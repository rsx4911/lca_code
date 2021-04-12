package com.greendelta.collaboration.webservice;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.repository.Datasets.Binary;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.BrowseService.BrowseParameter;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.util.Client;
import com.greendelta.collaboration.webservice.util.Repositories;

@Path("public/repository")
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryResource {

	private final RepositoryService service;
	private final GroupService groupService;
	private final BrowseService browseService;

	@Inject
	public RepositoryResource(RepositoryService service, GroupService groupService, BrowseService browseService) {
		this.service = service;
		this.groupService = groupService;
		this.browseService = browseService;
	}

	@GET
	public Response getPublic() {
		List<Repository> repositories = service.getAllAccessible();
		return Respond.ok(Client.map(repositories, repo -> {
			ObjectMap map = Repositories.map(repo);
			map.put("datasets", browseService.getCount(new BrowseParameter(repo)));
			return map;
		}));
	}

	@GET
	@Path("{group}/{name}")
	public Response get(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		boolean publicAccess = repo.settings.is(RepositorySetting.PUBLIC_ACCESS);
		ObjectMap mappedRepo = Repositories.map(repo, groupService.isUserNamespace(group, publicAccess));
		Commit lastCommit = repo.commits.find().last();
		if (lastCommit != null) {
			mappedRepo.put("settings.lastChange", lastCommit.timestamp);
		}
		return Respond.ok(mappedRepo);
	}

	@GET
	@Path("avatar/{group}/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAvatar(
			@PathParam("group") String group,
			@PathParam("name") String name) {
		Repository repo = service.get(group, name);
		return Respond.ok(repo.settings.get(RepositorySetting.AVATAR), "avatar-repository.png");
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
		// TODO test git implementation
		Repository repo = service.get(group, name);
		commitId = repo.commits.find().model(type, refId).until(commitId).id();
		if (commitId == null)
			return Respond.notFound(notFoundMessage(type, refId, null));
		Binary binary = repo.datasets.getBinary(type, refId, commitId, filename);
		if (binary == null)
			return Respond.notFound(notFoundMessage(type, refId, filename));
		return Respond.ok(binary.data);
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
