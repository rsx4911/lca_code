package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openlca.cloud.model.LibraryRestriction;
import org.openlca.cloud.model.RestrictionType;
import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Role;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.RepositorySetting;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.repository.Repository;
import com.greendelta.collaboration.service.repository.RepositoryService;
import com.greendelta.collaboration.service.user.MembershipService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("library")
public class LibraryResource {

	private final LibraryService service;
	private final RepositoryService repoService;
	private final UserService userService;
	private final MembershipService membershipService;

	@Inject
	public LibraryResource(LibraryService service, RepositoryService repoService, UserService userService,
			MembershipService membershipService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
		this.membershipService = membershipService;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	// openLCA versions up to 1.9.0 only checked if the data sets are part of a
	// library, to support older versions of both, openLCA and collaboration
	// server, the required parameters for the new check are optional (query
	// parameters)
	public Response checkAgainstLibraries(
			@QueryParam("group") String group,
			@QueryParam("name") String name,
			List<String> refIds) {
		if (Strings.nullOrEmpty(group) || Strings.nullOrEmpty(name))
			return legacyCheck(refIds);
		return check(group, name, refIds);
	}

	private Response check(String group, String name, List<String> refIds) {
		Repository repo = repoService.get(group, name);
		Map<String, Role> restrictedTo = repo.settings.get(RepositorySetting.LIBRARY_RESTRICTIONS);
		if (restrictedTo.isEmpty())
			return Respond.noContent();
		User user = userService.getCurrentUser();
		Role userRole = membershipService.getRole(user, repo.toId());
		List<LibraryRestriction> restrictions = new ArrayList<>();
		for (String refId : refIds) {
			Set<String> libraries = service.getLibraryNames(refId);
			for (String library : libraries) {
				Role restrictedToRole = restrictedTo.get(library);
				if (restrictedToRole == null)
					continue;
				if (userRole == null || !userRole.matches(restrictedToRole)) {
					restrictions.add(new LibraryRestriction(refId, library, RestrictionType.FORBIDDEN));
				} else {
					restrictions.add(new LibraryRestriction(refId, library, RestrictionType.WARNING));
				}
			}
		}
		if (restrictions.isEmpty())
			return Respond.noContent();
		return Respond.ok(restrictions);
	}

	@Deprecated
	private Response legacyCheck(List<String> refIds) {
		Map<String, String> refIdToLibrary = new HashMap<>();
		for (String refId : refIds) {
			Set<String> libraries = service.getLibraryNames(refId);
			if (libraries.isEmpty())
				continue;
			refIdToLibrary.put(refId, libraries.iterator().next());
		}
		if (refIdToLibrary.isEmpty())
			return Respond.noContent();
		return Respond.ok(refIdToLibrary);
	}

}
