package com.greendelta.collaboration.webservice.user;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.error.UnauthorizedAccessException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.AccessService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.NotificationService.NotificationJob;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Memberships;
import com.greendelta.search.wrapper.SearchResult;

@Path("membership")
@Produces(MediaType.APPLICATION_JSON)
public class MembershipResource {

	private final MembershipService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final TeamService teamService;
	private final AccessService accessService;
	private final NotificationService notificationService;

	@Inject
	public MembershipResource(MembershipService service, RepositoryService repoService, UserService userService,
			TeamService teamService, AccessService accessService, NotificationService notificationService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.teamService = teamService;
		this.accessService = accessService;
		this.notificationService = notificationService;
	}

	@GET
	@Path("{group}")
	public Response getAllForGroup(
			@PathParam("group") String group,
			@QueryParam("filter") @DefaultValue("") String filter) {
		return getAll(group, null, filter);
	}

	@GET
	@Path("{group}/{repo}")
	public Response getAllForRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@QueryParam("filter") @DefaultValue("") String filter) {
		return getAll(group, repo, filter);
	}

	private Response getAll(String group, String repo, String filter) {
		String path = getAuthorizedPath(group, repo);
		SearchResult<Membership> memberships = service.getMemberships(path, filter);
		return Respond.ok(SearchResults.lconvert(memberships, Memberships::map));
	}

	@POST
	@Path("{group}/user/{username}/{role}")
	public Response addUserRoleToGroup(
			@PathParam("group") String group,
			@PathParam("username") String username,
			@PathParam("role") Role role) {
		return addUserRole(group, null, username, role);
	}

	@POST
	@Path("{group}/{repo}/user/{username}/{role}")
	public Response addUserRoleToRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("username") String username,
			@PathParam("role") Role role) {
		return addUserRole(group, repo, username, role);
	}

	private Response addUserRole(String group, String repo, String username, Role role) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean added = service.addMembership(user, path, role);
		if (!added)
			return Respond.conflict("User " + username + " was already member of " + group + "/" + repo);
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notificationService.memberAdded(repoService.get(group, repo), user).send();
		else
			notificationService.memberAdded(group, user).send();
		return Respond.created(new HashMap<>());
	}

	@POST
	@Path("{group}/team/{teamname}/{role}")
	public Response addTeamRoleToGroup(
			@PathParam("group") String group,
			@PathParam("teamname") String teamname,
			@PathParam("role") Role role) {
		return addTeamRole(group, null, teamname, role);
	}

	@POST
	@Path("{group}/{repo}/team/{teamname}/{role}")
	public Response addTeamRoleToRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("teamname") String teamname,
			@PathParam("role") Role role) {
		return addTeamRole(group, repo, teamname, role);
	}

	private Response addTeamRole(String group, String repo, String teamname, Role role) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean added = service.addMemberships(team, path, role);
		if (!added)
			return Respond.conflict("Team " + teamname + " was already member of " + group + "/" + repo);
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notificationService.memberAdded(repoService.get(group, repo), team).send();
		else
			notificationService.memberAdded(group, team).send();
		return Respond.created(new HashMap<>());
	}

	@PUT
	@Path("{group}/user/{username}/{role}")
	public Response updateUserRoleInGroup(
			@PathParam("group") String group,
			@PathParam("username") String username,
			@PathParam("role") Role role) {
		return updateUserRole(group, null, username, role);
	}

	@PUT
	@Path("{group}/{repo}/user/{username}/{role}")
	public Response updateUserRoleInRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("username") String username,
			@PathParam("role") Role role) {
		return updateUserRole(group, repo, username, role);
	}

	private Response updateUserRole(String group, String repo, String username, Role role) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		boolean updated = service.setRole(user, path, role);
		if (!updated)
			return Respond.notFound("User " + username + " is not a member of " + group + "/" + repo);
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notificationService.roleChanged(repoService.get(group, repo), user).send();
		else
			notificationService.roleChanged(group, user).send();
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("{group}/team/{teamname}/{role}")
	public Response updateTeamRoleInGroup(
			@PathParam("group") String group,
			@PathParam("teamname") String teamname,
			@PathParam("role") Role role) {
		return updateTeamRole(group, null, teamname, role);
	}

	@PUT
	@Path("{group}/{repo}/team/{teamname}/{role}")
	public Response updateTeamRoleInRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("teamname") String teamname,
			@PathParam("role") Role role) {
		return updateTeamRole(group, repo, teamname, role);
	}

	private Response updateTeamRole(String group, String repo, String teamname, Role role) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		boolean updated = service.setRole(team, path, role);
		if (!updated)
			return Respond.notFound("Team " + teamname + " is not a member of " + group + "/" + repo);
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notificationService.roleChanged(repoService.get(group, repo), team).send();
		else
			notificationService.roleChanged(group, team).send();
		return Respond.ok(new HashMap<>());
	}

	@DELETE
	@Path("{group}/user/{username}")
	public Response removeUserRoleFromGroup(
			@PathParam("group") String group,
			@PathParam("username") String username) {
		return removeUserRole(group, null, username);
	}

	@DELETE
	@Path("{group}/{repo}/user/{username}")
	public Response removeUserRoleFromRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("username") String username) {
		return removeUserRole(group, repo, username);
	}

	private Response removeUserRole(String group, String repo, String username) {
		String path = getAuthorizedPath(group, repo);
		User user = userService.getForUsername(username);
		NotificationJob notification = null;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notification = notificationService.memberRemoved(repoService.get(group, repo), user);
		else
			notification = notificationService.memberRemoved(group, user);
		boolean removed = service.removeMembership(user, path);
		if (!removed)
			return Respond.notFound("User " + username + " is not a member of " + group + "/" + repo);
		notification.send();
		return Respond.ok(new HashMap<>());
	}

	@DELETE
	@Path("{group}/team/{teamname}")
	public Response removeTeamRoleFromGroup(
			@PathParam("group") String group,
			@PathParam("teamname") String teamname) {
		return removeTeamRole(group, null, teamname);
	}

	@DELETE
	@Path("{group}/{repo}/team/{teamname}")
	public Response removeTeamRoleFromRepository(
			@PathParam("group") String group,
			@PathParam("repo") String repo,
			@PathParam("teamname") String teamname) {
		return removeTeamRole(group, repo, teamname);
	}

	private Response removeTeamRole(String group, String repo, String teamname) {
		String path = getAuthorizedPath(group, repo);
		Team team = teamService.getForTeamname(teamname);
		NotificationJob notification = null;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null"))
			notification = notificationService.memberRemoved(repoService.get(group, repo), team);
		else
			notification = notificationService.memberRemoved(group, team);
		boolean removed = service.removeMemberships(team, path);
		if (!removed)
			return Respond.notFound("Team " + teamname + " is not a member of " + group + "/" + repo);
		notification.send();
		return Respond.ok(new HashMap<>());
	}

	private String getAuthorizedPath(String group, String repo) {
		if (userService.exists(group))
			throw new UnauthorizedAccessException(group, "EDIT_MEMBERS");
		String path = group;
		if (!Strings.isNullOrEmpty(repo) && !repo.toLowerCase().equals("null")) {
			// implicitly checks access
			Repository repository = repoService.get(group, repo);
			return repository.toId();
		}
		if (!accessService.canRead(group)) {
			throw new UnauthorizedAccessException(group, "READ");
		}
		return path;
	}

}
