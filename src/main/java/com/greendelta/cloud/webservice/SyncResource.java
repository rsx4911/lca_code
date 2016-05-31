package com.greendelta.cloud.webservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.cloud.model.data.FileReference;

import com.google.inject.Inject;
import com.greendelta.cloud.service.FetchService;
import com.greendelta.cloud.service.HistoryService;
import com.greendelta.cloud.service.Repository;
import com.greendelta.cloud.service.RepositoryService;

@Path("sync")
public class SyncResource {

	private final FetchService fetchService;
	private final HistoryService historyService;
	private final RepositoryService repoService;

	@Inject
	public SyncResource(FetchService fetchService, HistoryService historyService, RepositoryService repoService) {
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.repoService = repoService;
	}

	@GET
	@Path("{group}/{name}/{untilCommitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("untilCommitId") String untilCommitId) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = null;
		if (untilCommitId.equals("null"))
			commits = historyService.getCommits(repo);
		else
			commits = historyService.getCommitsUntil(repo, untilCommitId);
		if (commits.isEmpty())
			return Respond.noContent();
		List<FetchRequestData> result = getData(commits, repo);
		return Respond.ok(new ArrayList<>(result));
	}

	private List<FetchRequestData> getData(List<Commit> commits, Repository repo) {
		List<FetchRequestData> result = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();
		Collections.reverse(commits);
		for (Commit commit : commits) {
			List<Dataset> descriptors = historyService.getReferences(repo, commit.id);
			for (Dataset descriptor : descriptors) {
				if (alreadyAdded.contains(toKey(descriptor)))
					continue;
				FetchRequestData value = fetchService.toRequestData(repo, commit.id, descriptor);
				result.add(value);
				alreadyAdded.add(toKey(descriptor));
			}
		}
		return result;
	}

	private String toKey(FileReference reference) {
		return reference.type.name() + "_" + reference.refId;
	}
}
