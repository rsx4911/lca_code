package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Notification;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.platform.Imprint;
import com.greendelta.collaboration.platform.mail.EmailJob;
import com.greendelta.collaboration.platform.mail.EmailService;
import com.greendelta.collaboration.service.Repository;

public class NotificationService {

	private final UserService userService;
	private final MembershipService membershipService;
	private final EmailService emailService;
	private final Imprint imprint;
	private final String baseUrl;

	@Inject
	public NotificationService(UserService userService, MembershipService membershipService, EmailService emailService,
			Imprint imprint, @Named("base.url") String baseUrl) {
		this.userService = userService;
		this.membershipService = membershipService;
		this.emailService = emailService;
		this.imprint = imprint;
		this.baseUrl = baseUrl;
	}

	public NotificationJob groupCreated(String group) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String subject = "A new group was created";
		String message = "A new group <a href=\"" + url + "\">" + group + "</a> was created by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.GROUP_CREATED, true)));
		return new NotificationJob(emails);
	}

	public NotificationJob groupDeleted(String group) {
		User currentUser = userService.getCurrentUser();
		String subject = "A group was deleted";
		String message = "The group " + group + " was deleted by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.GROUP_DELETED, group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.GROUP_DELETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryCreated(Repository repo) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/" + repo.toId();
		String subject = "A new repository was created";
		String message = "A new repository <a href=\"" + url + "\">" + repo.toId()
				+ "</a> was created by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_CREATED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.REPOSITORY_CREATED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryMoved(Repository oldRepo, Repository newRepo) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/" + newRepo.toId();
		String subject = "A repository was moved";
		String message = "The repository " + oldRepo.toId() + " was moved to a href=\"" + url + "\">" + newRepo.toId()
				+ "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_MOVED, newRepo.toId())));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.REPOSITORY_MOVED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryDeleted(Repository repo) {
		User currentUser = userService.getCurrentUser();
		String subject = "A repository was deleted";
		String message = "The repository " + repo.toId() + " was deleted by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_DELETED, repo.toId())));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_DELETED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.REPOSITORY_DELETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob dataCommitted(Repository repo, Commit commit) {
		User currentUser = userService.getCurrentUser();
		String repoUrl = baseUrl + "/" + repo.toId();
		String commitUrl = baseUrl + "/" + repo.toId() + "/commit/" + commit.id;
		String subject = "Data was committed";
		String message = "Data was committed to <a href=\"" + repoUrl + "\">" + repo.toId()
				+ "</a> by the user "
				+ currentUser.name + " with message <a href=\"" + commitUrl + "\">" + commit.message + "</a>";
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.DATA_COMMITTED, repo.toId())));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.DATA_COMMITTED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.DATA_COMMITTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob fieldCommented(Comment comment) {
		User currentUser = userService.getCurrentUser();
		String repoUrl = baseUrl + "/" + comment.repositoryPath;
		String commentUrl = repoUrl + "/comments";
		String datasetUrl = repoUrl + "/dataset/" + comment.field.modelType.name() + "/" + comment.field.refId + "/"
				+ comment.field.commitId;
		String subject = "A field was commented";
		String message = "The user " + currentUser.name + " added a comment '<a href=\"" + commentUrl + "\">"
				+ comment.text + "</a>' in <a href=\"" + repoUrl
				+ "\">" + comment.repositoryPath
				+ "</a> on <a href=\"" + datasetUrl + "\">dataset</a>";
		String group = comment.repositoryPath.substring(0, comment.repositoryPath.indexOf('/'));
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message,
				getMemberUsers(Notification.FIELD_COMMENTED, comment.repositoryPath)));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.FIELD_COMMENTED, group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.FIELD_COMMENTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "You were added to a group";
		String othersSubject = "A member was added to a group";
		String personalMessage = "You were added to group <a href=\"" + url + "\">" + group + "</a> by the user "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_GROUP_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.ADDED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "Your role in a group was changed";
		String othersSubject = "A role was changed in a group";
		String personalMessage = "Your role in group <a href=\"" + url + "\">" + group
				+ "</a> was changed by the user " + currentUser.name;
		String othersMessage = "The role of user " + member.name + " for group <a href=\"" + url + "\">" + group
				+ "</a> was changed by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.GROUP_ROLE_CHANGED))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, Team member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "A team you are in was added to a group";
		String othersSubject = "A team was added to a group";
		String personalMessage = "A team you are in was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		String othersMessage = "The team " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.ADDED_TO_GROUP_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.ADDED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(String group, Team member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "The role of a team you are in was changed for a group";
		String othersSubject = "The role of a team was changed for a group";
		String personalMessage = "The role of a team you are in was changed for group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		String othersMessage = "The role of team " + member.name + " was changed for group <a href=\"" + url + "\">"
				+ group
				+ "</a> by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.GROUP_ROLE_CHANGED, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "You were removed from a group";
		String othersSubject = "A member was removed from a group";
		String personalMessage = "You were removed from group " + group + " by the user  " + currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REMOVED_FROM_GROUP_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REMOVED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(String group, Team member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "A team you are in was removed from a group";
		String othersSubject = "A team was removed from a group";
		String personalMessage = "A team you are in was removed from group " + group + " by the user "
				+ currentUser.name;
		String othersMessage = "The team " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.REMOVED_FROM_GROUP_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REMOVED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, User member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "You were added to a repository";
		String othersSubject = "A member was added to a repository";
		String personalMessage = "You were added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(Repository repo, User member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "Your role in a repository was changed";
		String othersSubject = "A role was changed in a repository ";
		String personalMessage = "Your role in repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user " + currentUser.name;
		String othersMessage = "The role of user " + member.name + " for repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REPOSITORY_ROLE_CHANGED))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, Team member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "A team you are in was added to a repository";
		String othersSubject = "A team was added to a repository";
		String personalMessage = "A team you are in was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user " + currentUser.name;
		String othersMessage = "The team " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.ADDED_TO_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(Repository repo, Team member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "The role of a team you are in was changed for a repository";
		String othersSubject = "The role of a team was changed in a repository ";
		String personalMessage = "The role of a team you are in for repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user " + currentUser.name;
		String othersMessage = "The role of team " + member.name + " for repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.REPOSITORY_ROLE_CHANGED, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, User member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "You were removed from a repository";
		String othersSubject = "A member was removed from a repository";
		String personalMessage = "You were removed from repository " + path + " by the user "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REMOVED_FROM_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REMOVED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, Team member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "A team you are in was removed from a repository";
		String othersSubject = "A team was removed from a repository";
		String personalMessage = "A team you are in was removed from repository " + path + " by the user "
				+ currentUser.name;
		String othersMessage = "The team " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.REMOVED_FROM_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REMOVED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Team team, User member) {
		User currentUser = userService.getCurrentUser();
		String personalSubject = "You were added to a team";
		String othersSubject = "A member was added to a team";
		String personalMessage = "You were added to team " + team.name + " by the user " + currentUser.name;
		String othersMessage = "The user " + member.name + " was added to team " + team.name
				+ " by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_TEAM_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage, getTeamUsers(Notification.ADDED_TEAM_MEMBER, team)));
		emails.addAll(createEmails(othersSubject, othersMessage, getAdminUsers(Notification.ADDED_TEAM_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Team team, User member) {
		User currentUser = userService.getCurrentUser();
		String personalSubject = "You were removed from a team";
		String othersSubject = "A member was removed from a team";
		String personalMessage = "You were removed from team " + team.name + " by the user "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from team " + team.name
				+ " by the user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REMOVED_FROM_TEAM_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage, getTeamUsers(Notification.REMOVED_TEAM_MEMBER, team)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.REMOVED_TEAM_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob userCreated(User user, String password) {
		User currentUser = userService.getCurrentUser();
		String adminMessage = "The user " + user.name + " was created by the user  " + currentUser.name;
		String userMessage = "A new account with username " + user.username
				+ " was created for you by the user  "
				+ currentUser.name
				+ ". To login please navigate to " + baseUrl
				+ " and enter your credentials. The password for this was generated by the system: " + password;
		Set<EmailJob> emails = createEmails("A user was created", adminMessage,
				getAdminUsers(Notification.USER_CREATED, true));
		emails.add(createEmail("A user was created for you", userMessage, user));
		return new NotificationJob(emails);
	}

	public NotificationJob userDeleted(User user) {
		User currentUser = userService.getCurrentUser();
		String subject = "A user was deleted";
		String message = "The user " + user.name + " was deleted by the user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminUsers(Notification.USER_DELETED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamCreated(Team team) {
		User currentUser = userService.getCurrentUser();
		String subject = "A team was created";
		String message = "The team " + team.name + " was created by the user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminUsers(Notification.TEAM_CREATED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamDeleted(Team team) {
		User currentUser = userService.getCurrentUser();
		String subject = "A team was deleted";
		String message = "The team " + team.name + " was deleted by the user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminUsers(Notification.TEAM_DELETED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob taskStarted(Repository repo, Task task) {
		User currentUser = userService.getCurrentUser();
		String repoId = repo.toId();
		String repoUrl = baseUrl + "/" + repoId;
		String taskUrl = baseUrl + "/tasks/" + getUrlPart(task) + task.getId();
		String subject = "A task was started";
		String message = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> was started in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getTaskUsers(Notification.TASK_STARTED, repoId, task)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.TASK_STARTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskCompleted(Repository repo, Task task) {
		User currentUser = userService.getCurrentUser();
		String repoId = repo.toId();
		String repoUrl = baseUrl + "/" + repoId;
		String taskUrl = baseUrl + "/tasks/" + getUrlPart(task) + task.getId();
		String subject = "A task was completed";
		String message = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was completed by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getTaskUsers(Notification.TASK_COMPLETED, repoId, task)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.TASK_COMPLETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskCanceled(Repository repo, Task task) {
		User currentUser = userService.getCurrentUser();
		String repoId = repo.toId();
		String repoUrl = baseUrl + "/" + repoId;
		String taskUrl = baseUrl + "/tasks/" + getUrlPart(task) + task.getId();
		String subject = "A task was canceled";
		String message = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was canceled by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getTaskUsers(Notification.TASK_CANCELED, repoId, task)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.TASK_CANCELED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskAssigned(Repository repo, Task task, TaskAssignment assignment) {
		User currentUser = userService.getCurrentUser();
		String repoId = repo.toId();
		String repoUrl = baseUrl + "/" + repoId;
		String taskUrl = baseUrl + "/tasks/" + getUrlPart(task) + task.getId();
		String personalSubject = "A task was assigned to you";
		String otherSubject = "A task was assigned to a user";
		String personalMessage = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was assigned to you by the user " + currentUser.name;
		String otherMessage = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was assigned to the user " + assignment.assignedTo.name
				+ " by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (assignment.assignedTo.isEnabled(Notification.TASK_ASSIGNED))
			emails.add(createEmail(personalSubject, personalMessage, assignment.assignedTo));
		emails.addAll(createEmails(otherSubject, otherMessage,
				getMemberUsers(Notification.TASK_ASSIGNED, repoId, Permission.MANAGE_TASK)));
		emails.addAll(createEmails(otherSubject, otherMessage, getAdminUsers(Notification.TASK_ASSIGNED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskCompleted(Repository repo, Task task, TaskAssignment assignment) {
		User currentUser = userService.getCurrentUser();
		String repoId = repo.toId();
		String repoUrl = baseUrl + "/" + repoId;
		String taskUrl = baseUrl + "/tasks/" + getUrlPart(task) + task.getId();
		String subject = "A task assignment was completed by a user";
		String message = "The assignment to task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was completed by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message,
				getMemberUsers(Notification.TASK_COMPLETED, repoId, Permission.MANAGE_TASK)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.TASK_COMPLETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskRevoked(Repository repo, Task task, TaskAssignment assignment) {
		User currentUser = userService.getCurrentUser();
		String repoId = repo.toId();
		String repoUrl = baseUrl + "/" + repoId;
		String taskUrl = baseUrl + "/tasks/" + getUrlPart(task) + task.getId();
		String personalSubject = "A task assignment was revoked from you";
		String otherSubject = "A task assignment was revoked from a user";
		String personalMessage = "The assignment to task <a href=\"" + taskUrl + "\">" + task.name
				+ "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was revoked from you by the user " + currentUser.name;
		String otherMessage = "The assignment to task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was revoked from the user " + assignment.assignedTo.name
				+ " by the user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (assignment.assignedTo.isEnabled(Notification.TASK_REVOKED))
			emails.add(createEmail(personalSubject, personalMessage, assignment.assignedTo));
		emails.addAll(createEmails(otherSubject, otherMessage,
				getMemberUsers(Notification.TASK_REVOKED, repoId, Permission.MANAGE_TASK)));
		emails.addAll(createEmails(otherSubject, otherMessage, getAdminUsers(Notification.TASK_REVOKED, false)));
		return new NotificationJob(emails);
	}

	private EmailJob createEmail(String subject, String message, User recipient) {
		EmailJob emailJob = new EmailJob();
		emailJob.setSubject(subject);
		emailJob.setHtmlContent(toEmailContent(message, recipient));
		emailJob.setRecipient(recipient.email);
		return emailJob;
	}

	private String toEmailContent(String message, User user) {
		String content = "Dear " + user.name + ",<br><br>";
		content += message + "<br><br>";
		content += "<div style=\"font-size:80%;\">This message was automatically sent to you by the system. If you do not wish to receive this type of notification again, you can configure the notification settings in your <a href=\""
				+ baseUrl + "/user/notifications\">profile</a>" + "<br>";
		content += "<hr>";
		content += imprint.company + ", " + imprint.street + ", " + imprint.zipCode + " " + imprint.city + ", "
				+ imprint.country + "<br>";
		content += "Companies' Register: " + imprint.registration + "<br>";
		content += "Managing Director: " + imprint.ceo + "</div>";
		return content;
	}

	private Set<EmailJob> createEmails(String subject, String message, Set<User> recipients) {
		Set<EmailJob> emails = new HashSet<>();
		for (User recipient : recipients)
			emails.add(createEmail(subject, message, recipient));
		return emails;
	}

	private Set<User> getMemberUsers(Notification notification, String path) {
		return getMemberUsers(notification, path, null);
	}

	private Set<User> getMemberUsers(Notification notification, String path, Permission permission) {
		Set<User> users = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (Membership member : membershipService.getMemberships(path)) {
			if (member.user == null)
				continue;
			if (!member.user.isEnabled(notification))
				continue;
			if (currentUser.equals(member.user))
				continue;
			if (permission != null && (member.role == null || !member.role.getPermissions().contains(permission)))
				continue;
			users.add(member.user);
		}
		return users;
	}

	private Set<User> getTeamUsers(Notification notification, Team team) {
		Set<User> users = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (User user : team.users) {
			if (!user.isEnabled(notification))
				continue;
			if (currentUser.equals(user))
				continue;
			users.add(user);
		}
		return users;
	}

	private Set<User> getTaskUsers(Notification notification, String repoId, Task task) {
		Set<User> users = getMemberUsers(notification, repoId, Permission.MANAGE_TASK);
		User currentUser = userService.getCurrentUser();
		for (TaskAssignment assignment : task.assignments) {
			User user = assignment.assignedTo;
			if (user == null || currentUser.equals(user))
				continue;
			if (!user.isEnabled(notification))
				continue;
			users.add(user);
		}
		return users;
	}

	private Set<User> getAdminUsers(Notification notification, boolean adminMessage) {
		List<User> admins = userService.getAdmins();
		Set<User> users = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (User admin : admins) {
			if (!admin.isEnabled(notification))
				continue;
			if (!adminMessage && !admin.isEnabled(Notification.NOTIFY_FOR_ALL))
				continue;
			if (currentUser.equals(admin))
				continue;
			users.add(admin);
		}
		return users;
	}

	private String getUrlPart(Task task) {
		if (task instanceof Review)
			return "review/";
		return "";
	}

	public class NotificationJob {

		private final Collection<EmailJob> jobs;

		private NotificationJob() {
			this.jobs = Collections.emptyList();
		}

		private NotificationJob(Collection<EmailJob> jobs) {
			this.jobs = Collections.unmodifiableList(new ArrayList<>(jobs));
		}

		public void send() {
			for (EmailJob job : jobs)
				emailService.send(job);
		}

	}

}
