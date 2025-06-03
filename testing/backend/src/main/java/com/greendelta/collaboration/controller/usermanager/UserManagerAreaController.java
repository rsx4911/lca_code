package com.greendelta.collaboration.controller.usermanager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.service.GroupService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.TeamService;
import com.greendelta.collaboration.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("ws/usermanager/area")
public class UserManagerAreaController {

	private final RepositoryService repoService;
	private final UserService userService;
	private final GroupService groupService;
	private final TeamService teamService;

	public UserManagerAreaController(RepositoryService repoService, UserService service, GroupService groupService,
			TeamService teamService) {
		this.repoService = repoService;
		this.userService = service;
		this.groupService = groupService;
		this.teamService = teamService;
	}

	@GetMapping("count")
	public Map<String, Object> getCounts(@Autowired HttpServletRequest request) {
		var result = new HashMap<String, Object>();
		var currentUser = userService.getCurrentUser();
		if (currentUser.isDataManager()) {
			result.put("repositories", repoService.getCount());
			result.put("groups", groupService.getAllAccessible().stream()
					.filter(Predicate.not(groupService::isUserNamespace))
					.count());
		}
		if (currentUser.isUserManager()) {
			result.put("users", userService.getCount());
			result.put("teams", teamService.getCount());
		}
		return result;
	}

}
