package com.greendelta.collaboration.controller.user;

import java.util.Map;

import org.openlca.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Memberships;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.PermissionsService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.NotificationService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;

@RestController
@RequestMapping("ws/membership")
public class MembershipController {

	private final MembershipService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final TeamService teamService;
	private final PermissionsService permissions;
	private final NotificationService notificationService;

	public MembershipController(MembershipService service, RepositoryService repoService, UserService userService,
			TeamService teamService, PermissionsService permissions, NotificationService notificationService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.teamService = teamService;
		this.permissions = permissions;
		this.notificationService = notificationService;
	}

	@GetMapping("{group}")
	public SearchResult<Map<String, Object>> getAllForGroup(
			@PathVariable String group,
			@RequestParam(required = false) String filter) {
		return getAll(group, null, filter);
	}

	@GetMapping("{group}/{repo}")
	public SearchResult<Map<String, Object>> getAllForRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@RequestParam(required = false) String filter) {
		return getAll(group, repo, filter);
	}

	private SearchResult<Map<String, Object>> getAll(String group, String repo, String filter) {
		var path = getAuthorizedPath(group, repo);
		var memberships = service.getMemberships(path, filter);
		return SearchResults.listConvert(memberships, Memberships::map);
	}

	@PostMapping("{group}/user/{username}/{role}")
	public ResponseEntity<?> addUserRoleToGroup(
			@PathVariable String group,
			@PathVariable String username,
			@PathVariable Role role) {
		addUserRole(group, null, username, role);
		return Response.created();
	}

	@PostMapping("{group}/{repo}/user/{username}/{role}")
	public ResponseEntity<?> addUserRoleToRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@PathVariable String username,
			@PathVariable Role role) {
		addUserRole(group, repo, username, role);
		return Response.created();
	}

	private void addUserRole(String group, String repoName, String username, Role role) {
		var path = getAuthorizedPath(group, repoName);
		var user = userService.getForUsername(username);
		var added = service.addMembership(user, path, role);
		if (!added)
			throw Response.conflict("User " + username + " was already member of " + group + "/" + repoName);
		if (!Strings.nullOrEmpty(repoName) && !repoName.toLowerCase().equals("null")) {
			try (var repo = repoService.get(group, repoName)) {
				notificationService.memberAdded(repo, user).send();
			}
		} else {
			notificationService.memberAdded(group, user).send();
		}
	}

	@PostMapping("{group}/team/{teamname}/{role}")
	public ResponseEntity<?> addTeamRoleToGroup(
			@PathVariable String group,
			@PathVariable String teamname,
			@PathVariable Role role) {
		addTeamRole(group, null, teamname, role);
		return Response.created();
	}

	@PostMapping("{group}/{repo}/team/{teamname}/{role}")
	public ResponseEntity<?> addTeamRoleToRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@PathVariable String teamname,
			@PathVariable Role role) {
		addTeamRole(group, repo, teamname, role);
		return Response.created();
	}

	private void addTeamRole(String group, String repoName, String teamname, Role role) {
		var path = getAuthorizedPath(group, repoName);
		var team = teamService.getForTeamname(teamname);
		var added = service.addMemberships(team, path, role);
		if (!added)
			throw Response.conflict("Team " + teamname + " was already member of " + group + "/" + repoName);
		if (!Strings.nullOrEmpty(repoName) && !repoName.toLowerCase().equals("null")) {
			try (var repo = repoService.get(group, repoName)) {
				notificationService.memberAdded(repo, team).send();
			}
		} else {
			notificationService.memberAdded(group, team).send();
		}
	}

	@PutMapping("{group}/user/{username}/{role}")
	public void updateUserRoleInGroup(
			@PathVariable String group,
			@PathVariable String username,
			@PathVariable Role role) {
		updateUserRole(group, null, username, role);
	}

	@PutMapping("{group}/{repo}/user/{username}/{role}")
	public void updateUserRoleInRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@PathVariable String username,
			@PathVariable Role role) {
		updateUserRole(group, repo, username, role);
	}

	private void updateUserRole(String group, String repoName, String username, Role role) {
		var path = getAuthorizedPath(group, repoName);
		var user = userService.getForUsername(username);
		var updated = service.setRole(user, path, role);
		if (!updated)
			throw Response.notFound("User " + username + " is not a member of " + group + "/" + repoName);
		if (!Strings.nullOrEmpty(repoName) && !repoName.toLowerCase().equals("null")) {
			try (var repo = repoService.get(group, repoName)) {
				notificationService.roleChanged(repo, user).send();
			}
		} else {
			notificationService.roleChanged(group, user).send();
		}
	}

	@PutMapping("{group}/team/{teamname}/{role}")
	public void updateTeamRoleInGroup(
			@PathVariable String group,
			@PathVariable String teamname,
			@PathVariable Role role) {
		updateTeamRole(group, null, teamname, role);
	}

	@PutMapping("{group}/{repo}/team/{teamname}/{role}")
	public void updateTeamRoleInRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@PathVariable String teamname,
			@PathVariable Role role) {
		updateTeamRole(group, repo, teamname, role);
	}

	private void updateTeamRole(String group, String repoName, String teamname, Role role) {
		var path = getAuthorizedPath(group, repoName);
		var team = teamService.getForTeamname(teamname);
		var updated = service.setRole(team, path, role);
		if (!updated)
			throw Response.notFound("Team " + teamname + " is not a member of " + group + "/" + repoName);
		if (!Strings.nullOrEmpty(repoName) && !repoName.toLowerCase().equals("null")) {
			try (var repo = repoService.get(group, repoName)) {
				notificationService.roleChanged(repo, team).send();
			}
		} else {
			notificationService.roleChanged(group, team).send();
		}
	}

	@DeleteMapping("{group}/user/{username}")
	public void removeUserRoleFromGroup(
			@PathVariable String group,
			@PathVariable String username) {
		var path = getAuthorizedPath(group, null);
		var user = userService.getForUsername(username);
		var notification = notificationService.memberRemoved(group, user);
		var removed = service.removeMembership(user, path);
		if (!removed)
			throw Response.notFound("User " + username + " is not a member of " + group);
		notification.send();
	}

	@DeleteMapping("{group}/{repo}/user/{username}")
	public void removeUserRoleFromRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@PathVariable String username) {
		if (repo.equals("null")) {
			removeUserRoleFromGroup(group, username);
			return;
		}
		var path = getAuthorizedPath(group, repo);
		var user = userService.getForUsername(username);
		try (var repository = repoService.get(group, repo)) {
			var notification = notificationService.memberRemoved(repo, user);
			var removed = service.removeMembership(user, path);
			if (!removed)
				throw Response.notFound("User " + username + " is not a member of " + group + "/" + repo);
			notification.send();
		}
	}

	@DeleteMapping("{group}/team/{teamname}")
	public void removeTeamRoleFromGroup(
			@PathVariable String group,
			@PathVariable String teamname) {
		removeTeamRole(group, null, teamname);
	}

	@DeleteMapping("{group}/{repo}/team/{teamname}")
	public void removeTeamRoleFromRepository(
			@PathVariable String group,
			@PathVariable String repo,
			@PathVariable String teamname) {
		removeTeamRole(group, repo, teamname);
	}

	private void removeTeamRole(String group, String repoName, String teamname) {
		var path = getAuthorizedPath(group, repoName);
		var team = teamService.getForTeamname(teamname);
		try (var repo = repoService.get(group, repoName)) {
			var notification = !Strings.nullOrEmpty(repoName) && !repoName.toLowerCase().equals("null")
					? notificationService.memberRemoved(repo, team)
					: notificationService.memberRemoved(group, team);
			var removed = service.removeMemberships(team, path);
			if (!removed)
				throw Response.notFound("Team " + teamname + " is not a member of " + group + "/" + repoName);
			notification.send();
		}
	}

	private String getAuthorizedPath(String group, String repo) {
		var path = group;
		if (!Strings.nullOrEmpty(repo) && !repo.toLowerCase().equals("null")) {
			// implicitly checks access
			try (var repository = repoService.get(group, repo)) {
				return repository.path();
			}
		}
		if (userService.exists(group))
			throw Response.forbidden(group, Permission.EDIT_MEMBERS);
		if (!permissions.canRead(group))
			throw Response.forbidden(group, Permission.READ);
		return path;
	}

}
