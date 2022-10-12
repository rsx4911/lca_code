package com.greendelta.collaboration.controller.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.LibraryService.LibraryInfo;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;

@RestController
@RequestMapping("ws/libraries")
public class LibraryController {

	private final LibraryService service;
	private final RepositoryService repoService;

	@Autowired
	public LibraryController(LibraryService service, RepositoryService repoService) {
		this.service = service;
		this.repoService = repoService;
	}

	@GetMapping
	public List<LibraryInfo> getAll() {
		return service.getAllAccessible().stream().map(lib -> service.getInfo(lib, true)).toList();
	}

	@GetMapping("teams")
	public List<LibraryInfo> getAllForTeams() {
		return service.getAccessibleForTeams().stream().map(lib -> service.getInfo(lib, false)).toList();
	}

	@GetMapping("missing")
	public List<HashMap<String, Object>> getMissing() {
		try (var repos = repoService.getAllAccessible()) {
			var libraries = service.getAllAccessible();
			var missing = repos.stream()
					.map(Repository::linkedLibraries)
					.flatMap(List::stream)
					.distinct()
					.filter(Predicate.not(libraries::contains))
					.collect(Collectors.toSet());
			return missing.stream().map(lib -> {
				var linkedIn = repos.stream()
						.filter(repo -> repo.linkedLibraries().contains(lib))
						.map(Repository::path)
						.toList();
				var info = new HashMap<String, Object>();
				info.put("name", lib);
				info.put("linkedIn", linkedIn);
				return info;
			}).toList();
		}
	}

	@GetMapping("{name}")
	public ResponseEntity<StreamingResponseBody> get(@PathVariable("name") String name) {
		var library = service.get(name);
		if (library == null)
			throw Response.notFound();
		return Response.ok(library.getName(), library);
	}

	@PostMapping
	public String create(
			@RequestParam("file") MultipartFile file,
			@RequestParam("access") String access) {
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

	@DeleteMapping("{name}/{access}")
	public void delete(@PathVariable("name") String name, @PathVariable("access") String access) {
		if (service.get(name) == null)
			throw Response.notFound("No library " + name + " found");
		if (!service.delete(name, access))
			throw Response.error("Error deleting library " + name);
	}

}
