package com.greendelta.collaboration.webservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.openlca.cloud.api.data.FetchWriter;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.cloud.model.data.FetchRequestData;
import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;

@Path("fetch")
public class FetchResource {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final FetchService service;
	private final RepositoryService repoService;
	private final HistoryService historyService;

	@Inject
	public FetchResource(FetchService service, RepositoryService repoService,
			HistoryService historyService) {
		this.service = service;
		this.repoService = repoService;
		this.historyService = historyService;
	}

	@GET
	@Path("file/{group}/{name}/{type}/{refId}/{commitId}/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("group") String group,
			@PathParam("name") String name, @PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId, @PathParam("filename") String filename) throws IOException {
		Repository repo = repoService.get(group, name);
		commitId = getLastCommitId(repo, type, refId, commitId);
		if (commitId == null)
			return Respond.notFound(notFoundMessage(type, refId, null));
		File binDir = service.getBinDir(repo, type, refId, commitId);
		if (!binDir.exists())
			return Respond.notFound(notFoundMessage(type, refId, filename));
		File file = new File(binDir, filename);
		if (!file.exists())
			return Respond.notFound(notFoundMessage(type, refId, filename));
		return Respond.ok(Files.readAllBytes(file.toPath()));
	}

	@GET
	@Path("data/{group}/{name}/{type}/{refId}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		commitId = getLastCommitId(repo, type, refId, commitId);
		if (commitId == null) {
			String message = notFoundMessage(type, refId, null);
			return Respond.notFound(message);
		}
		String dataset = service.getDataset(repo, type, refId, commitId);
		if (dataset == null) {
			String message = notFoundMessage(type, refId, commitId);
			return Respond.notFound(message);
		}
		return Respond.ok(dataset);
	}

	private String getLastCommitId(Repository repo, ModelType type, String refId, String commitId) {
		if ("null".equals(commitId))
			commitId = null;
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
	@Path("request/{group}/{name}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@PathParam("group") String group, @PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommitsAfter(repo, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		List<FetchRequestData> result = getData(commits, repo);
		if (result.size() == 0)
			return Respond.noContent();
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
				FetchRequestData value = service.toRequestData(repo, commit.id, descriptor);
				result.add(value);
				alreadyAdded.add(toKey(descriptor));
			}
		}
		return result;
	}

	@POST
	@Path("{group}/{name}/{lastCommitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetch(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("lastCommitId") String lastCommitId,
			List<String> requested) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId.equals("null"))
			lastCommitId = null;
		List<Commit> commits = historyService.getCommitsAfter(repo, lastCommitId);
		if (commits.size() == 0)
			return Respond.noContent();
		StreamingOutput data = prepareFetch(requested, commits, repo);
		if (data == null)
			return Respond.noContent();
		return Respond.ok(data);
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		List<Dataset> datasets = historyService.getReferences(repo, commitId);
		if (datasets.size() == 0) {
			// if size is 0, commit was not found (no commit without files)
			String message = "Commit with id " + commitId + " not found";
			return Respond.notFound(message);
		}
		List<FetchRequestData> resultData = new ArrayList<>();
		for (Dataset dataset : datasets) {
			FetchRequestData value = service.toRequestData(repo, commitId,
					dataset);
			resultData.add(value);
		}
		return Respond.ok(resultData);
	}

	private StreamingOutput prepareFetch(List<String> requested, List<Commit> commits, Repository repo) {
		FetchWriter writer = new FetchWriter(null);
		Collections.reverse(commits);
		try {
			Set<String> alreadyPut = new HashSet<>();
			for (Commit commit : commits) {
				for (Dataset dataset : historyService.getReferences(repo, commit.id)) {
					if (requested != null && !requested.contains(dataset.refId))
						continue;
					if (alreadyPut.contains(toKey(dataset)))
						continue;
					put(writer, repo, dataset, commit.id);
					alreadyPut.add(toKey(dataset));
				}
			}
			writer.setCommitId(commits.get(0).id);
			writer.close();
			return toStream(writer.getFile());
		} catch (IOException e) {
			log.error("Error closing fetch writer", e);
			return null;
		}
	}

	private void put(FetchWriter writer, Repository repo, Dataset dataset, String commitId) throws IOException {
		String data = service.getDataset(repo, dataset.type, dataset.refId, commitId);
		File binDir = service.getBinDir(repo, dataset.type, dataset.refId, commitId);
		writer.put(dataset, data, binDir);
	}

	private StreamingOutput toStream(File file) {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				Files.copy(file.toPath(), output);
			}

		};
	}

	private String toKey(FileReference reference) {
		return reference.type.name() + "_" + reference.refId;
	}

}
