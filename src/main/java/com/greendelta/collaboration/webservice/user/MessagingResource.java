package com.greendelta.collaboration.webservice.user;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.UserSettings;
import com.greendelta.collaboration.service.MessagingService;
import com.greendelta.collaboration.service.MessagingService.ConversationDescriptor;
import com.greendelta.collaboration.service.TeamService;
import com.greendelta.collaboration.service.UserService;
import com.greendelta.collaboration.util.Beans;
import com.greendelta.collaboration.util.ObjectMap;
import com.greendelta.collaboration.webservice.Respond;
import com.greendelta.collaboration.webservice.util.Conversations;
import com.greendelta.collaboration.webservice.util.Messages;

@Path("messaging")
@Produces(MediaType.APPLICATION_JSON)
public class MessagingResource {

	private final MessagingService service;
	private final UserService userService;
	private final TeamService teamService;

	@Inject
	public MessagingResource(MessagingService service, UserService userService, TeamService teamService) {
		this.service = service;
		this.userService = userService;
		this.teamService = teamService;
	}

	@GET
	public Response getConversations() {
		User user = userService.getCurrentUser();
		List<ConversationDescriptor> conversations = service.getConversations(user);
		return Respond.ok(Conversations.map(conversations, user));
	}

	@GET
	@Path("user/{username}")
	public Response getMessages(
			@PathParam("username") String username, 
			@QueryParam("before") long before) {
		User user = userService.getCurrentUser();
		User other = userService.getForUsername(username);
		if (user.settings.blockedUsers.contains(other))
			return Respond.noContent();
		Calendar cal = Calendar.getInstance();
		if (before > 0) {
			cal.setTimeInMillis(before);
		}
		List<Message> conversation = service.getMessages(user, other, 20, before > 0 ? cal.getTime() : null);
		if (conversation.isEmpty())
			return Respond.noContent();
		return Respond.ok(Messages.map(conversation, user));
	}

	@GET
	@Path("team/{teamname}")
	public Response getTeamMessages(
			@PathParam("teamname") String teamname, 
			@QueryParam("before") long before) {
		Team team = teamService.getForTeamname(teamname);
		Calendar cal = Calendar.getInstance();
		if (before > 0) {
			cal.setTimeInMillis(before);
		}
		User user = userService.getCurrentUser();
		List<Message> conversation = service.getMessages(user, team, 20, before > 0 ? cal.getTime() : null);
		if (conversation.isEmpty())
			return Respond.noContent();
		return Respond.ok(Messages.map(conversation, user));
	}

	@PUT
	@Path("settings")
	public Response updateSettings(UserSettings settings) {
		User currentUser = userService.getCurrentUser();
		Beans.populateProperties(settings, currentUser.settings,
				"messagingEnabled", "messagingRestricted", "showOnlineStatus", "showReadReceipt");
		currentUser = userService.update(currentUser);
		return Respond.ok(ObjectMap.fromObject(currentUser.settings));
	}

	@PUT
	@Path("block/{username}")
	public Response blockUser(@PathParam("username") String username) {
		User other = userService.getForUsername(username);
		if (other == null)
			return Respond.notFound();
		User currentUser = userService.getCurrentUser();
		currentUser.settings.blockedUsers.add(other);
		currentUser = userService.update(currentUser);
		return Respond.ok(new HashMap<>());
	}

	@PUT
	@Path("unblock/{username}")
	public Response unblockUser(@PathParam("username") String username) {
		User other = userService.getForUsername(username);
		if (other == null)
			return Respond.notFound();
		User currentUser = userService.getCurrentUser();
		currentUser.settings.blockedUsers.remove(other);
		currentUser = userService.update(currentUser);
		return Respond.ok(new HashMap<>());
	}

}
