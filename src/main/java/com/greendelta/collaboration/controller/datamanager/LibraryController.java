package com.greendelta.collaboration.controller.datamanager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.openlca.git.util.Repositories;
import org.openlca.jsonld.PackageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.LibraryAccess;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.RepositoryService;

@RestController("datamanager-LibraryController")
@RequestMapping("ws/datamanager/libraries")
public class LibraryController {

	private final LibraryService service;
	private final RepositoryService repoService;

	@Autowired
	public LibraryController(LibraryService service, RepositoryService repoService) {
		this.service = service;
		this.repoService = repoService;
	}

	@GetMapping("missing")
	public Set<String> getMissing() {
		var libraries = service.getAllAccessible();
		return repoService.getAllAccessible().stream()
				.map(Repository::gitRepo)
				.map(Repositories::infoOf)
				.filter(Objects::nonNull)
				.map(PackageInfo::libraries)
				.flatMap(List::stream)
				.distinct()
				.filter(Predicate.not(libraries::contains))
				.collect(Collectors.toSet());
	}

	@PostMapping
	public String create(
			@RequestParam("file") MultipartFile file,
			@RequestParam("access") LibraryAccess access) {
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

	@PutMapping("{id}")
	public void update(@PathVariable("id") String id,
			@RequestParam("access") LibraryAccess access) {
		var library = service.get(id);
		if (library == null)
			throw Response.notFound("No library " + id + " found");
		service.update(id, access);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") String id) {
		if (service.get(id) == null)
			throw Response.notFound("No library " + id + " found");
		if (!service.delete(id))
			throw Response.error("Error deleting library " + id);
	}

}
