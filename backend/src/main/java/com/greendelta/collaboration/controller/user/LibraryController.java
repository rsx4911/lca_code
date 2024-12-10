package com.greendelta.collaboration.controller.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.lib.PersonIdent;
import org.openlca.core.library.LibraryDir;
import org.openlca.core.library.LibraryPackage;
import org.openlca.util.Dirs;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.io.LibraryReplacer;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.LibraryService.LibraryInfo;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.user.UserService;

@RestController
@RequestMapping("ws/libraries")
public class LibraryController {

	private static final Logger log = LogManager.getLogger(LibraryController.class);
	private final LibraryService service;
	private final RepositoryService repoService;
	private final UserService userService;

	public LibraryController(LibraryService service, RepositoryService repoService, UserService userService) {
		this.service = service;
		this.repoService = repoService;
		this.userService = userService;
	}

	@GetMapping
	public List<LibraryInfo> getAll() {
		return service.getAllAccessible().stream().map(lib -> service.getInfo(lib)).toList();
	}

	@GetMapping("missing")
	public List<HashMap<String, Object>> getMissing() {
		try (var repos = repoService.getAllAccessible()) {
			var libraries = service.getAllAccessible();
			var missing = repos.stream()
					.map(Repository::getLinkedLibraries)
					.flatMap(Set::stream)
					.distinct()
					.filter(Predicate.not(libraries::contains))
					.collect(Collectors.toSet());
			return missing.stream().map(lib -> {
				var linkedIn = repos.stream()
						.filter(repo -> repo.getLinkedLibraries().contains(lib))
						.map(Repository::path)
						.toList();
				var currentlyLinkedIn = repos.stream()
						.filter(repo -> repo.getLibraries().contains(lib))
						.map(Repository::path)
						.toList();
				var info = new HashMap<String, Object>();
				info.put("name", lib);
				info.put("linkedIn", linkedIn);
				info.put("currentlyLinkedIn", currentlyLinkedIn);
				return info;
			}).toList();
		}
	}

	@GetMapping("{name}")
	public ResponseEntity<Resource> get(@PathVariable String name) {
		var library = service.get(name);
		if (library == null)
			throw Response.notFound();
		return Response.ok(library);
	}

	@PostMapping
	public String create(
			@RequestParam MultipartFile file,
			@RequestParam LibraryAccess access) {
		try (var stream = file.getInputStream()) {
			var id = service.insert(stream, access);
			if (id == null)
				throw Response.badRequest("file", "Not a valid library file");
			return id;
		} catch (IOException e) {
			if ("existed".equals(e.getMessage()))
				throw Response.badRequest("file", "Library with the same name already existed");
			throw Response.error("Could not save file");
		}
	}

	@PutMapping("{name}/{access}")
	public void changeAccess(@PathVariable String name, @PathVariable LibraryAccess access) {
		if (service.get(name) == null)
			throw Response.notFound("No library " + name + " found");
		if (!service.update(name, access))
			throw Response.error("Error updating library " + name);
	}

	@PostMapping("replace/{toReplace}/{replaceWith}")
	public Set<String> replaceInRepositories(@PathVariable String toReplace, @PathVariable String replaceWith,
			@RequestBody ReplacementData data) {
		if (service.get(toReplace) == null)
			throw Response.notFound("No library " + toReplace + " found");
		if (service.get(replaceWith) == null)
			throw Response.notFound("No library " + replaceWith + " found");
		if (data.repositories.isEmpty())
			return new HashSet<>();
		var user = userService.getCurrentUser();
		var success = new HashSet<String>();
		File tmp = null;
		try {
			tmp = Files.createTempDirectory("cs-library-dir").toFile();
			var libFile = service.getLibraryFile(replaceWith);
			var libDir = LibraryDir.of(tmp);
			LibraryPackage.unzip(libFile, libDir);
			var replacement = libDir.getLibrary(replaceWith).get();
			for (var repoId : data.repositories) {
				try (var repo = repoService.get(repoId)) {
					LibraryReplacer.in(repo)
							.as(new PersonIdent(user.username, ""))
							.withMessage(data.message)
							.replace(toReplace, replacement)
							.run();
					success.add(repoId);
				} catch (IOException e) {
					log.error("Error replacing library " + toReplace + " with " + replaceWith + " in repo " + repoId);
				}
			}
		} catch (IOException e) {
			log.error("Error extracting library " + replaceWith);
		} finally {
			Dirs.delete(tmp);
		}
		return success;
	}

	@DeleteMapping("{name}")
	public void delete(@PathVariable String name) {
		if (service.get(name) == null)
			throw Response.notFound("No library " + name + " found");
		if (!service.delete(name))
			throw Response.error("Error deleting library " + name);
	}

	private record ReplacementData(Set<String> repositories, String message) {
	}

}
