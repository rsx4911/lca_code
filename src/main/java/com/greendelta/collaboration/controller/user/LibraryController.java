package com.greendelta.collaboration.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.service.LibraryService.LibraryInfo;

@RestController
@RequestMapping("ws/libraries")
public class LibraryController {

	private final LibraryService service;

	@Autowired
	public LibraryController(LibraryService service) {
		this.service = service;
	}

	@GetMapping
	public List<LibraryInfo> getAll() {
		return service.getAllAccessible().stream().map(service::getInfo).toList();
	}

	@GetMapping("{id}")
	public ResponseEntity<StreamingResponseBody> get(@PathVariable("id") String id) {
		var library = service.get(id);
		if (library == null)
			throw Response.notFound();
		return Response.ok(library.getName(), library);
	}

}
