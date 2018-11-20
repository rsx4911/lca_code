package com.greendelta.collaboration.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.AnnouncementService;
import com.greendelta.collaboration.service.AnnouncementService.Announcement;

public class AnnouncementResource {

	private final AnnouncementService service;

	@Inject
	public AnnouncementResource(AnnouncementService service) {
		this.service = service;
	}

	@GET
	public Response getAnnouncement() {
		Announcement announcement = service.getAnnouncement();
		if (announcement == null)
			return Respond.noContent();
		return Respond.ok(announcement);
	}

}
