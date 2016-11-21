package com.greendelta.cloud.webservice;

import java.util.Calendar;
import java.util.Collections;
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
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;
import com.greendelta.cloud.service.MessageService;
import com.greendelta.cloud.service.MessageService.ConversationDescriptor;
import com.greendelta.cloud.service.TeamService;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.mapper.ConversationMapper;
import com.greendelta.cloud.webservice.mapper.MessageMapper;

@Path("messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

	private final MessageService service;
	private final UserService userService;
	private final TeamService teamService;

	@Inject
	public MessageResource(MessageService service, UserService userService, TeamService teamService) {
		this.service = service;
		this.userService = userService;
		this.teamService = teamService;
	}

	@GET
	public Response getConversations() {
		User user = userService.getCurrentUser();
		List<ConversationDescriptor> conversations = service.getConversations(user);
		return Respond.ok(new ConversationMapper().map(conversations, user));
	}

	@GET
	@Path("user/{username}")
	public Response getMessages(@PathParam("username") String username, @QueryParam("before") long before) {
		User user = userService.getCurrentUser();
		User other = userService.getForUsername(username);
		Calendar cal = Calendar.getInstance();
		if (before > 0) {
			cal.setTimeInMillis(before);
		}
		List<Message> conversation = service.getMessages(user, other, 20, before > 0 ? cal.getTime() : null);
		if (conversation.isEmpty())
			return Respond.noContent();
		return Respond.ok(new MessageMapper().map(conversation));
	}

	@GET
	@Path("team/{teamname}")
	public Response getTeamMessages(@PathParam("teamname") String teamname, @QueryParam("before") long before) {
		Team team = teamService.getForTeamname(teamname);
		Calendar cal = Calendar.getInstance();
		if (before > 0) {
			cal.setTimeInMillis(before);
		}
		User user = userService.getCurrentUser();
		List<Message> conversation = service.getMessages(user, team, 20, before > 0 ? cal.getTime() : null);
		if (conversation.isEmpty())
			return Respond.noContent();
		return Respond.ok(new MessageMapper().map(conversation));
	}

	@PUT
	@Path("markAsRead/user/{username}")
	public Response markAsRead(@PathParam("username") String username) {
		User user = userService.getCurrentUser();
		User other = userService.getForUsername(username);
		service.markAsRead(user, other);
		return Respond.ok(Collections.emptyMap());
	}

	@PUT
	@Path("markAsRead/team/{teamname}")
	public Response markAsReadTeam(@PathParam("teamname") String teamname) {
		User user = userService.getCurrentUser();
		Team team = teamService.getForTeamname(teamname);
		service.markAsRead(user, team);
		return Respond.ok(Collections.emptyMap());
	}

}
