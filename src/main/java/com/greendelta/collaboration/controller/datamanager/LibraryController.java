package com.greendelta.collaboration.controller.datamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.LibraryService;
import com.greendelta.collaboration.util.Routes;

@RestController("datamanager-LibraryController")
@RequestMapping("ws/datamanager/library")
public class LibraryController {

	private final LibraryService service;
	private final DeleteService deleteService;

	@Autowired
	public LibraryController(LibraryService service, DeleteService deleteService) {
		this.service = service;
		this.deleteService = deleteService;
	}

	@GetMapping
	public List<Map<String, Object>> getLibraries() {
		var libraries = new ArrayList<Map<String, Object>>();
		for (var name : service.getLibraryNames()) {
			var map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("count", service.getRefIds(name).size());
			libraries.add(map);
		}
		return libraries;
	}

	@GetMapping("{name}")
	public Set<String> getLibraryRefIds(@PathVariable("name") String name) {
		return service.getRefIds(name);
	}

	@PutMapping("{name}")
	public void putLibrary(@PathVariable("name") String name,
			@RequestBody List<String> refIds) {
		if (!Routes.isValid(name, ' '))
			throw Response.badRequest("name", "Only letters, numbers, underscore and space are allowed");
		service.putLibrary(name, refIds);
	}

	@DeleteMapping("{name}")
	public void removeLibrary(@PathVariable("name") String name) {
		deleteService.deleteLibrary(name);
	}

}
