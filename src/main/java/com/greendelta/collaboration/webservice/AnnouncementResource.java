package com.greendelta.collaboration.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.AnnouncementService;
import com.greendelta.collaboration.service.AnnouncementService.Announcement;

@Path("public/announcements")
@Produces(MediaType.APPLICATION_JSON)
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
