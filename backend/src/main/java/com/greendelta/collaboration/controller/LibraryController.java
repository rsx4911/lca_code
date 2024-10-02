package com.greendelta.collaboration.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.LibraryService;

@RestController("publicLibraryController")
@RequestMapping("ws/public/libraries")
public class LibraryController {

	private final LibraryService service;

	public LibraryController(LibraryService service) {
		this.service = service;
	}

	@GetMapping("{name}")
	public ResponseEntity<Resource> get(@PathVariable String name) {
		var library = service.get(name);
		if (library == null)
			throw Response.notFound();
		return Response.ok(library);
	}

}
