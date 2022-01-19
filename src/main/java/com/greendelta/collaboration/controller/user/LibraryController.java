package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.LibraryRestriction;
import com.greendelta.collaboration.model.LibraryRestriction.RestrictionType;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/library")
public class LibraryController {

	private final LibraryService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final MembershipService membershipService;

	@Autowired
	public LibraryController(LibraryService service, RepositoryService repoService, UserService userService,
			MembershipService membershipService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.membershipService = membershipService;
	}

	@PostMapping
	public ResponseEntity<List<LibraryRestriction>> checkAgainstLibraries(
			@RequestParam("group") String group,
			@RequestParam("name") String name,
			@RequestBody List<String> refIds) {
		return check(group, name, refIds);
	}

	private ResponseEntity<List<LibraryRestriction>> check(String group, String name, List<String> refIds) {
		try (var repo = repoService.get(group, name)) {
			Map<String, Role> restrictedTo = repo.settings.get(RepositorySetting.LIBRARY_RESTRICTIONS);
			if (restrictedTo.isEmpty())
				return Response.noContent();
			var user = userService.getCurrentUser();
			var userRole = membershipService.getRole(user, repo.path());
			var restrictions = new ArrayList<LibraryRestriction>();
			refIds.forEach(refId -> {
				service.getLibraryNames(refId).forEach(library -> {
					var restrictedToRole = restrictedTo.get(library);
					if (restrictedToRole == null)
						return;
					if (userRole == null || !userRole.matches(restrictedToRole)) {
						restrictions.add(new LibraryRestriction(refId, library, RestrictionType.FORBIDDEN));
					} else {
						restrictions.add(new LibraryRestriction(refId, library, RestrictionType.WARNING));
					}
				});
			});
			if (restrictions.isEmpty())
				return Response.noContent();
			return Response.ok(restrictions);
		}
	}

}
