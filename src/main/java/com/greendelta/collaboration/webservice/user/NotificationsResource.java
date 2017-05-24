package com.greendelta.collaboration.webservice.user;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.greendelta.collaboration.model.Notification;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.webservice.Respond;

@Path("notifications")
public class NotificationsResource {

	private final UserService userService;

	@Inject
	public NotificationsResource(UserService userService) {
		this.userService = userService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnabled() {
		User currentUser = userService.getCurrentUser();
		List<Notification> enabled = new ArrayList<>();
		for (Notification n : Notification.values())
			if (currentUser.isEnabled(n))
				enabled.add(n);
		return Respond.ok(new Gson().toJson(enabled));
	}

	@PUT
	@Path("enable/{notifications}")
	public Response enable(@PathParam("notifications") String notifications) {
		User currentUser = userService.getCurrentUser();
		for (Notification notification : parse(notifications))
			currentUser.enable(notification);
		userService.update(currentUser);
		return Respond.ok();
	}

	@PUT
	@Path("disable/{notifications}")
	public Response disable(@PathParam("notifications") String notifications) {
		User currentUser = userService.getCurrentUser();
		for (Notification notification : parse(notifications))
			currentUser.disable(notification);
		userService.update(currentUser);
		return Respond.ok();
	}

	private Notification[] parse(String value) {
		String[] values = value.split(",");
		Notification[] parsed = new Notification[values.length];
		for (int i = 0; i < values.length; i++)
			parsed[i] = Notification.valueOf(values[i]);
		return parsed;
	}

}
