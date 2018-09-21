package com.greendelta.collaboration.webservice.user;

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

import org.openlca.cloud.model.data.FileReference;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;

@Path("fetch") // support for openLCA 1.7 (url changed to public afterwards)
public class FetchResource {

	private com.greendelta.collaboration.webservice.FetchResource publicFetch;

	@Inject
	public FetchResource(com.greendelta.collaboration.webservice.FetchResource publicFetch) {
		this.publicFetch = publicFetch;
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
		return publicFetch.getData(group, name, type, refId, commitId);
	}

	@GET
	@Path("request/{group}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("lastCommitId") String lastCommitId,
			@QueryParam("sync") @DefaultValue("false") boolean sync) {
		return publicFetch.request(group, name, lastCommitId, sync);
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
		return publicFetch.fetch(group, name, commitId, download, requested);
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReferences(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		return publicFetch.getReferences(group, name, commitId);
	}

}
