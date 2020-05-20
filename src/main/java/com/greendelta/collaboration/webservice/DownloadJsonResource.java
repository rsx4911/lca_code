package com.greendelta.collaboration.webservice;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.common.Strings;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.search.BrowseService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.io.DatasetWriter;
import com.greendelta.collaboration.util.io.JsonWriter;
import com.greendelta.collaboration.webservice.ReferenceCollector.Reference;

@Path("public/download/json")
public class DownloadJsonResource extends DownloadResource {

	private final FetchService fetchService;
	private final SearchService searchService;
	private final HistoryService historyService;
	private final RepositoryService repoService;

	@Inject
	public DownloadJsonResource(RepositoryService repoService, HistoryService historyService, FetchService fetchService,
			SearchService searchService, BrowseService browseService, UserService userService) {
		super(repoService, historyService, searchService, browseService, userService);
		this.fetchService = fetchService;
		this.searchService = searchService;
		this.historyService = historyService;
		this.repoService = repoService;
	}

	@GET
	@Path("{token}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Override
	public Response download(@PathParam("token") String token) {
		if (token.startsWith("repository_")) {
			Repository repo = repoService.get(token.substring(11).replace("@", "/"));
			if (repo.getCachedJsonFile().exists())
				return Respond.ok(repo.toFilename(), repo.getCachedJsonFile());
		}
		return super.download(token);
	}

	@GET
	@Path("prepare/{group}/{repository}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepareByPath(
			@PathParam("group") String group,
			@PathParam("repository") String repository,
			@QueryParam("commitId") String commitId,
			@QueryParam("path") String path) {
		if (isCompleteCurrentRepo(group, repository, commitId, path))
			return Respond.ok("repository_" + group + "@" + repository);
		return super.prepare(group, repository, commitId, path);
	}

	private boolean isCompleteCurrentRepo(String group, String repository, String commitId, String path) {
		Repository repo = repoService.get(group, repository);
		if (!repo.getCachedJsonFile().exists())
			return false; // is not cached
		if (!Strings.isNullOrEmpty(path))
			return false; // is not complete repo
		if (commitId != null && !historyService.getLastCommit(repo).id.equals(commitId))
			return false; // is not current state (last commit)
		return true;
	}

	@GET
	@Path("prepare/{group}/{repository}/{type}/{refId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepareDataset(
			@PathParam("group") String group,
			@PathParam("repository") String repository,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@QueryParam("commitId") String commitId) {
		return super.prepare(group, repository, type, refId, commitId);
	}

	@POST
	@Path("prepare/{group}/{repository}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepareSelection(
			@PathParam("group") String group,
			@PathParam("repository") String repository,
			@QueryParam("commitId") String commitId,
			List<Reference> references) {
		return super.prepare(group, repository, commitId, collectRefs(group, repository, references));
	}

	@PUT
	@Path("prepare/{group}/{repository}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepareRequested(
			@PathParam("group") String group,
			@PathParam("repository") String repository,
			@QueryParam("commitId") String commitId,
			List<FileReference> requested) {
		return super.prepare(group, repository, commitId, requested.iterator());
	}

	@Override
	protected DatasetWriter createWriter(Repository repo, String commitId) throws IOException {
		return new JsonWriter(fetchService, searchService, repo, commitId);
	}

}
