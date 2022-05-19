package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.Restriction;
import com.greendelta.collaboration.model.Restriction.RestrictionType;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.RestrictionService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/restrictions")
public class RestrictionController {

	private final RestrictionService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final MembershipService membershipService;

	@Autowired
	public RestrictionController(RestrictionService service, RepositoryService repoService, UserService userService,
			MembershipService membershipService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.membershipService = membershipService;
	}

	@PostMapping
	public ResponseEntity<List<Restriction>> checkAgainstLibraries(
			@RequestParam("group") String group,
			@RequestParam("name") String name,
			@RequestBody List<String> refIds) {
		return check(group, name, refIds);
	}

	private ResponseEntity<List<Restriction>> check(String group, String name, List<String> refIds) {
		try (var repo = repoService.get(group, name)) {
			Map<String, Role> restrictedTo = repo.settings.get(RepositorySetting.RESTRICTIONS);
			if (restrictedTo.isEmpty())
				return Response.noContent();
			var user = userService.getCurrentUser();
			var userRole = membershipService.getRole(user, repo.path());
			var entries = new ArrayList<Restriction>();
			var restrictions = service.getAll().stream()
					.collect(Collectors.toMap(lib -> lib.name, lib -> lib.getRefIds()));
			refIds.forEach(refId -> {
				restrictions.keySet().forEach(restriction -> {
					if (!restrictions.get(restriction).contains(refId))
						return;
					var restrictedToRole = restrictedTo.get(restriction);
					if (restrictedToRole == null)
						return;
					if (userRole == null || !userRole.matches(restrictedToRole)) {
						entries.add(new Restriction(refId, restriction, RestrictionType.FORBIDDEN));
					} else {
						entries.add(new Restriction(refId, restriction, RestrictionType.WARNING));
					}
				});
			});
			if (entries.isEmpty())
				return Response.noContent();
			return Response.ok(entries);
		}
	}

}
