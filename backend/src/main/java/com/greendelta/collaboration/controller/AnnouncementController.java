package com.greendelta.collaboration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.service.AnnouncementService;
import com.greendelta.collaboration.service.AnnouncementService.Announcement;

@RestController
@RequestMapping("ws/public/announcements")
public class AnnouncementController {

	private final AnnouncementService service;

	public AnnouncementController(AnnouncementService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<Announcement> getAnnouncement() {
		var announcement = service.getAnnouncement();
		if (announcement == null)
			return Response.noContent();
		return Response.ok(announcement);
	}

}
