package com.greendelta.collaboration.webservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.StreamingOutput;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.FetchRequestData;
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

@Path("public/fetch")
public class FetchResource {

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
		Repository repo = repoService.get(group, name);
		commitId = getLastCommitId(repo, type, refId, commitId);
		if (commitId == null)
			return Respond.notFound(notFoundMessage(type, refId, null));
		String dataset = service.getDataset(repo, type, refId, commitId);
		if (dataset == null)
			return Respond.notFound(notFoundMessage(type, refId, commitId));
		return Respond.ok(dataset);
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

	@GET
	@Path("request/{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("lastCommitId") String lastCommitId,
			@QueryParam("sync") @DefaultValue("false") boolean sync) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = getCommits(repo, lastCommitId, sync);
		if (commits.isEmpty())
			return Respond.noContent();
		List<FetchRequestData> result = getData(commits, repo);
		if (result.isEmpty())
			return Respond.noContent();
		return Respond.ok(new ArrayList<>(result));
	}

	private List<FetchRequestData> getData(List<Commit> commits, Repository repo) {
		List<FetchRequestData> result = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();
		for (Commit commit : commits) {
			List<IndexEntry> descriptors = searchService.getAll(repo, commit);
			for (IndexEntry descriptor : descriptors) {
				if (alreadyAdded.contains(descriptor.refId))
					continue;
				result.add(descriptor.asFetchRequestData());
				alreadyAdded.add(descriptor.refId);
			}
		}
		return result;
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		Commit commit = historyService.getCommit(repo, commitId);
		if (commit == null)
			return Respond.notFound("Commit with id " + commitId + " not found");
		List<IndexEntry> datasets = searchService.getAll(repo, commit);
		if (datasets.isEmpty())
			return Respond.notFound("Commit with id " + commitId + " not found");
		List<FetchRequestData> resultData = new ArrayList<>();
		for (IndexEntry entry : datasets) {
			resultData.add(entry.asFetchRequestData());
		}
		return Respond.ok(resultData);
	}

	@POST
	@Path("{group}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("commitId") String commitId,
			@QueryParam("download") @DefaultValue("false") boolean download,
			List<FileReference> requested) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = getCommits(repo, commitId, download);
		if (commits.isEmpty())
			return Respond.noContent();
		if (requested.isEmpty() && download) {
			requested = null;
		}
		StreamingOutput data = service.prepareData(repo, commits, requested);
		return Respond.ok(data);
	}

	private List<Commit> getCommits(Repository repo, String commitId, boolean until) {
		List<Commit> commits = null;
		if (until) {
			commits = historyService.getCommitsUntil(repo, commitId);
		} else {
			commits = historyService.getCommitsAfter(repo, commitId);
		}
		Collections.reverse(commits);
		return commits;
	}

}
