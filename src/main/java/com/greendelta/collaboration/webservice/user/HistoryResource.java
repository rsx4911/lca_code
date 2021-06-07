package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.api.git.Commit;
import org.openlca.cloud.api.git.DiffReference;
import org.openlca.cloud.api.git.DiffType;
import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.MetaData;
import com.greendelta.search.wrapper.SearchResult;

@Path("history")
@Produces(MediaType.APPLICATION_JSON)
public class HistoryResource {

	private final RepositoryService repoService;
	private final UserService userService;
	private final AccessService accessService;
	private final SettingsService settingsService;

	@Inject
	public HistoryResource(RepositoryService repoService, UserService userService, AccessService accessService,
			SettingsService settingsService) {
		this.repoService = repoService;
		this.userService = userService;
		this.accessService = accessService;
		this.settingsService = settingsService;
	}

	@GET
	@Path("{group}/{name}/{type}/{refId}")
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = repo.commits.find().model(type, refId).all();
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("{group}/{name}")
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("path") String path,
			@QueryParam("lastCommitId") String lastCommitId) {
		Repository repo = repoService.get(group, name);
		if (lastCommitId != null && !lastCommitId.isEmpty()) {
			Commit commit = repo.commits.get(lastCommitId);
			if (commit == null)
				return Respond.notFound("Commit " + lastCommitId + " not found");
		}
		List<Commit> commits = repo.commits.find().after(lastCommitId).path(path).all();
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		return Respond.ok(putUserName(commits));
	}

	@GET
	@Path("search/{group}/{name}")
	public Response getCommitHistory(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@QueryParam("filter") String filter,
			@QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		Repository repo = repoService.get(group, name);
		List<Commit> commits = repo.commits.find().all();
		if (commits.size() == 0)
			return Respond.noContent();
		java.util.Collections.reverse(commits);
		SearchResult<Commit> result = SearchResults.pagedAndFiltered(page, pageSize, filter, commits, (c) -> c.message);
		SearchResult<ObjectMap> converted = SearchResults.convert(result, c -> ObjectMap.fromObject(c));
		return Respond.ok(putAdditionalInfo(converted, repo, commits));
	}

	private Map<String, Object> putAdditionalInfo(SearchResult<ObjectMap> result, Repository repo,
			List<Commit> commits) {
		Map<String, Integer> groupCount = new HashMap<>();
		result = SearchResults.convert(result, this::putUserName);
		for (ObjectMap commitData : result.data) {
			int count = 0;
			for (Commit c : commits) {
				if (!isSameDay(commitData.getLong("timestamp"), c.timestamp))
					continue;
				count++;
			}
			String commitId = commitData.getString("id");
			groupCount.put(commitId, count);
			Commit commit = repo.commits.get(commitId);
			List<DiffReference> diffs = repo.references.diff().withPrevious(commit.id).all();
			commitData.put("additions", DiffReference.filter(diffs, DiffType.ADDED).size());
			commitData.put("deletions", DiffReference.filter(diffs, DiffType.DELETED).size());
			commitData.put("updates", DiffReference.filter(diffs, DiffType.MODIFIED).size());
		}
		ObjectMap map = ObjectMap.fromObject(result);
		map.put("resultInfo.groupCount", groupCount);
		return map;
	}

	private boolean isSameDay(long d1, long d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(d2);
		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
			return false;
		return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

	@GET
	@Path("commit/{group}/{name}/{commitId}")
	public Response getCommit(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		Commit commit = repo.commits.get(commitId);
		if (commit == null)
			return Respond.notFound();
		ObjectMap map = putUserName(commit);
		List<DiffReference> diffs = repo.references.diff().withPrevious(commit.id).all();
		map.put("additions", DiffReference.filter(diffs, DiffType.ADDED).size());
		map.put("deletions", DiffReference.filter(diffs, DiffType.DELETED).size());
		map.put("updates", DiffReference.filter(diffs, DiffType.MODIFIED).size());
		map.put("canCreateChangeLog", accessService.canCreateChangeLog(repo.toId()));
		return Respond.ok(map);
	}

	@GET
	@Path("references/{group}/{name}/{commitId}")
	public Response getReferences(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("commitId") String commitId,
			@QueryParam("type") ModelType type,
			@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("filter") @DefaultValue("") String filter) {
		Repository repo = repoService.get(group, name);
		Commit commit = repo.commits.get(commitId);
		if (commit == null)
			return Respond.notFound();
		List<DiffReference> refs = repo.references.diff().type(type).withPrevious(commit.id).all();
		List<ObjectMap> mapped = Collections.convertToList(refs, r -> MetaData.toDatasetInfo(r, repo));
		List<String> typesOrder = settingsService.get(ServerSetting.MODEL_TYPES_ORDER);
		MetaData.sortByTypeAndName(mapped, typesOrder);
		return Respond.ok(SearchResults.pagedAndFiltered(page, pageSize, filter, mapped));
	}

	private List<ObjectMap> putUserName(List<Commit> commits) {
		List<ObjectMap> mapped = new ArrayList<>();
		for (Commit commit : commits) {
			mapped.add(putUserName(commit));
		}
		return mapped;
	}

	private ObjectMap putUserName(Commit commit) {
		return putUserName(ObjectMap.fromObject(commit));
	}

	private ObjectMap putUserName(ObjectMap map) {
		User user = userService.getForUsername(map.getString("user"));
		map.put("userDisplayName", user != null ? user.name : map.getString("user"));
		return map;
	}

	@GET
	@Path("previousCommitId/{group}/{name}/{type}/{refId}/{commitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPreviousCommitId(
			@PathParam("group") String group,
			@PathParam("name") String name,
			@PathParam("type") ModelType type,
			@PathParam("refId") String refId,
			@PathParam("commitId") String commitId) {
		Repository repo = repoService.get(group, name);
		Commit lastCommit = repo.commits.find().model(type, refId).before(commitId).latest();
		if (lastCommit == null || lastCommit.id.equals(commitId))
			return Respond.notFound("No previous commit found for " + type.name() + " " + refId);
		return Respond.ok(lastCommit.id);
	}

}
