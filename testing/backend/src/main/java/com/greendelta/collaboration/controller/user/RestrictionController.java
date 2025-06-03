package com.greendelta.collaboration.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;

// kept for legacy support
@RestController
@RequestMapping("ws/restrictions")
public class RestrictionController {

	@PostMapping
	public ResponseEntity<List<Map<String, Object>>> checkAgainstLibraries(
			@RequestParam String group,
			@RequestParam String name,
			@RequestBody List<String> refIds) {
		return Response.ok(new ArrayList<Map<String, Object>>());
	}

}
