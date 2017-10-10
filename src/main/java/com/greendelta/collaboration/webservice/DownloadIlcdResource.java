package com.greendelta.collaboration.webservice;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.FetchService;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SearchService;
import com.greendelta.collaboration.util.export.DatasetWriter;
import com.greendelta.collaboration.util.export.IlcdWriter;

@Path("public/download/ilcd")
public class DownloadIlcdResource extends DownloadResource {

	private final HistoryService historyService;
	private final FetchService fetchService;
	private final SearchService searchService;

	@Inject
	public DownloadIlcdResource(RepositoryService repoService, HistoryService historyService,
			FetchService fetchService, SearchService searchService) {
		super(repoService, historyService);
		this.fetchService = fetchService;
		this.historyService = historyService;
		this.searchService = searchService;
	}

	@GET
	@Path("prepare/{group}/{repository}/{type}/{refId}/{commitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepare(
			@PathParam("group") String group, 
			@PathParam("repository") String repository,
			@PathParam("type") ModelType type, 
			@PathParam("refId") String refId, 
			@PathParam("commitId") String commitId) {
		return super.prepare(group, repository, type, refId, commitId);
	}

	@PUT
	@Path("prepare/{group}/{repository}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response prepare(
			@PathParam("group") String group, 
			@PathParam("repository") String repository,
			List<FileReference> requested) {
		return super.prepare(group, repository, requested);
	}

	@GET
	@Path("{token}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Override
	public Response download(@PathParam("token") String token) {
		return super.download(token);
	}

	@Override
	protected DatasetWriter createWriter(Repository repo) throws IOException {
		return new IlcdWriter(fetchService, historyService, searchService, repo);
	}

}
