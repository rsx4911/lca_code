package com.greendelta.collaboration.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Avatar;
import com.greendelta.collaboration.controller.util.Module;
import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.controller.util.Teams;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.SearchResults;

@RestController
@RequestMapping("ws/team")
public class TeamController {

	private final TeamService service;
	private final UserService userService;

	@Autowired
	public TeamController(TeamService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<?> getAll(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "module", required = false) Module module) {
		var result = service.getVisible(page, pageSize, filter);
		if (module == null)
			return Response.ok(SearchResults.convert(result, Teams::mapForOthers));
		return Response.ok(result.data.stream().map(Teams::mapForOthers).toList());
	}

	@GetMapping("avatar/{teamname}")
	public byte[] getAvatar(@PathVariable("teamname") String teamname) {
		var team = service.getForTeamname(teamname);
		if (team == null)
			return Avatar.get("avatar-team.png");
		return Avatar.get(team.avatar, "avatar-team.png");
	}

	@PutMapping("avatar/{teamname}")
	public byte[] setAvatar(
			@PathVariable("teamname") String teamname,
			@RequestParam(name = "file", required = false) byte[] file) {
		var team = authorizedGetTeam(teamname);
		if (team == null)
			throw Response.notFound();
		team.avatar = file;
		team = service.update(team);
		return getAvatar(teamname);
	}

	private Team authorizedGetTeam(String teamname) {
		var user = userService.getCurrentUser();
		if (!user.isUserManager())
			throw Response.unauthorized("Not authorized to manage teams");
		return service.getForTeamname(teamname);
	}

}
