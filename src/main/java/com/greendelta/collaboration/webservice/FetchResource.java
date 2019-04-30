package com.greendelta.collaboration.webservice;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.search.SearchService;
import com.greendelta.collaboration.util.Collections;

@Path("public/fetch")
public class FetchResource {

	private static final Logger log = LogManager.getLogger(FetchResource.class);
	private final FetchService service;
	private final RepositoryService repoService;
	private final HistoryService historyService;
	private final SearchService searchService;

	@Inject
	public FetchResource(FetchService service, RepositoryService repoService, HistoryService historyService,
			SearchService searchService) {
		this.service = service;
		this.repoService = repoService;
		this.historyService = historyService;
		this.searchService = searchService;
	}

	@GET
	@Path("data/{group}/{name}/{type}/{refId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@QueryParam("commitId") String commitId) {
		log.debug("Fetching {} {} from repository {}/{} (commit id: {})", type, refId, group, name, commitId);
		Repository repo = repoService.get(group, name);
		Commit commit = historyService.getLastCommit(repo, type, refId, commitId);
		if (commit == null)
			return Respond.notFound(notFoundMessage(type, refId, null));
		String dataset = service.getDataset(repo, type, refId, commit.id);
		if (dataset == null)
			return Respond.notFound(notFoundMessage(type, refId, commit.id));
		return Respond.ok(dataset);
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

	@GET
	@Path("request/{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("lastCommitId") String lastCommitId,
			@QueryParam("sync") @DefaultValue("false") boolean sync) {
		log.debug("Requesting fetch for repository {}/{} (last commit id: {}, sync: {})", group, name, lastCommitId, sync);
		Repository repo = repoService.get(group, name);
		Commit commit = historyService.getCommit(repo, lastCommitId);
		List<IndexEntry> data = sync
				? searchService.getMostRecentUntil(repo, commit != null ? commit.id : null)
				: searchService.getMostRecentAfter(repo, commit);
		if (data.isEmpty())
			return Respond.noContent();
		return Respond.ok(Collections.convertToList(data, entry -> entry.asFetchRequestData()));
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		log.debug("Requesting reeferences for repository {}/{} (commit id: {})", group, name, commitId);
		Repository repo = repoService.get(group, name);
		Commit commit = historyService.getCommit(repo, commitId);
		if (commit == null)
			return Respond.notFound("Commit with id " + commitId + " not found");
		List<IndexEntry> datasets = searchService.getAll(repo, commit.id);
		if (datasets.isEmpty())
			return Respond.notFound("Commit with id " + commitId + " not found");
		return Respond.ok(Collections.convertToList(datasets, entry -> entry.asFetchRequestData()));
	}

	@POST
	@Path("{group}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response fetch(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("commitId") String commitId,
			@QueryParam("download") @DefaultValue("false") boolean download,
			List<FileReference> requested) {
		log.info("Fetching data for repository {}/{} (commit id: {}, download: {})", group, name, commitId, download);
		Repository repo = repoService.get(group, name);
		Commit commit = historyService.getCommit(repo, commitId);
		if (commit == null && historyService.getCommits(repo).isEmpty())
			return Respond.noContent();
		if (!download)
			return Respond.ok(service.prepareDataForFetch(repo, requested, commit));
		if (commit == null)
			return Respond.noContent();
		return Respond.ok(service.prepareDataForDownload(repo, requested, commit));
	}

}
