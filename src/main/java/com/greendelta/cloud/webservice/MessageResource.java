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
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.model.chat.Message;
import com.greendelta.cloud.service.MessageService;
import com.greendelta.cloud.service.MessageService.ConversationDescriptor;
import com.greendelta.cloud.service.UserService;
import com.greendelta.cloud.webservice.mapper.ConversationMapper;
import com.greendelta.cloud.webservice.mapper.MessageMapper;

@Path("messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

	private final MessageService service;
	private final UserService userService;

	@Inject
	public MessageResource(MessageService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@GET
	public Response getConversations() {
		User user = userService.getCurrentUser();
		List<ConversationDescriptor> conversations = service.getConversations(user);
		return Respond.ok(new ConversationMapper().map(conversations));
	}

	@GET
	@Path("{username}")
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

	@PUT
	@Path("markAsRead/{username}")
	public Response markAsRead(@PathParam("username") String username) {
		User other = userService.getForUsername(username);
		service.markAsRead(other);
		return Respond.ok(Collections.emptyMap());
	}

}
