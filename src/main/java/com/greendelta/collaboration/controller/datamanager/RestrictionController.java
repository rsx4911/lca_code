package com.greendelta.collaboration.controller.datamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.RestrictionSet;
import com.greendelta.collaboration.service.DeleteService;
import com.greendelta.collaboration.service.RestrictionService;
import com.greendelta.collaboration.util.Routes;

@RestController("datamanager-RestrictionController")
@RequestMapping("ws/datamanager/restrictions")
public class RestrictionController {

	private final RestrictionService service;
	private final DeleteService deleteService;

	@Autowired
	public RestrictionController(RestrictionService service, DeleteService deleteService) {
		this.service = service;
		this.deleteService = deleteService;
	}

	@GetMapping
	public List<Map<String, Object>> getRestrictions() {
		var restrictions = new ArrayList<Map<String, Object>>();
		for (var set : service.getAll()) {
			var map = new HashMap<String, Object>();
			map.put("name", set.name);
			map.put("count", set.getRefIds().size());
			restrictions.add(map);
		}
		return restrictions;
	}

	@GetMapping("{name}")
	public List<String> getRestrictionRefIds(@PathVariable("name") String name) {
		return service.getForName(name).getRefIds();
	}

	@PutMapping("{name}")
	public void putRestriction(@PathVariable("name") String name,
			@RequestBody List<String> refIds) {
		if (!Routes.isValid(name, ' '))
			throw Response.badRequest("name", "Only letters, numbers, underscore and space are allowed");
		var set = service.getForName(name);
		if (set != null) {
			set.setRefIds(refIds);
			service.update(set);
		} else {
			set = new RestrictionSet();
			set.name = name;
			set.setRefIds(refIds);
			service.insert(set);
		}
	}

	@DeleteMapping("{name}")
	public void removeRestriction(@PathVariable("name") String name) {
		deleteService.deleteRestriction(name);
	}

}
