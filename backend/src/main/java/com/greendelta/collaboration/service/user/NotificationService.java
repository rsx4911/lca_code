package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.openlca.git.model.Commit;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Notification;
import com.greendelta.collaboration.model.Permission;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.settings.ServerSetting;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.service.EmailService;
import com.greendelta.collaboration.service.EmailService.EmailJob;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SettingsService;

@Service
public class NotificationService {

	private final UserService userService;
	private final MembershipService membershipService;
	private final EmailService emailService;
	private final SettingsService settings;

	public NotificationService(UserService userService, MembershipService membershipService, EmailService emailService,
			SettingsService settings) {
		this.userService = userService;
		this.membershipService = membershipService;
		this.emailService = emailService;
		this.settings = settings;
	}

	public NotificationJob groupCreated(String group) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var subject = "A new group was created";
		var message = "A new group <a href=\"" + url + "\">" + group + "</a> was created by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.GROUP_CREATED, true)));
		return new NotificationJob(emails);
	}

	public NotificationJob groupDeleted(String group) {
		var currentUser = userService.getCurrentUser();
		var subject = "A group was deleted";
		var message = "The group " + group + " was deleted by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.GROUP_DELETED, group)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.GROUP_DELETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob groupSizeLimitExceeded(String group, long maxSize, long actualSize) {
		var subject = "A group size limit was exceeded";
		var start = userService.getForUsername(group) != null
				? ("The user group of the user " + group)
				: "The group " + group;
		var message = start + " has exceeded its size limit of " + toSize(maxSize) + ". The actual size is now " + toSize(actualSize);
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.GROUP_SIZE_LIMIT_EXCEEDED, true)));
		return new NotificationJob(emails);
	}
	
	private static String toSize(long size) {
		if (size < 1073741824l)
			return (Math.round(size * 100d / 1048576d) / 100d) + " MB";
		return (Math.round(size  * 100d / 1073741824d) / 100d) + " GB";
	}	
	
	public NotificationJob repositoryCreated(Repository repo) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/" + repo.path();
		var subject = "A new repository was created";
		var message = "A new repository <a href=\"" + url + "\">" + repo.path()
				+ "</a> was created by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_CREATED, repo.group)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.REPOSITORY_CREATED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryMoved(Repository oldRepo, Repository newRepo) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/" + newRepo.path();
		var subject = "A repository was moved";
		var message = "The repository " + oldRepo.path() + " was moved to <a href=\"" + url + "\">" + newRepo.path()
				+ "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_MOVED, newRepo.path())));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.REPOSITORY_MOVED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryDeleted(Repository repo) {
		var currentUser = userService.getCurrentUser();
		var subject = "A repository was deleted";
		var message = "The repository " + repo.path() + " was deleted by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_DELETED, repo.path())));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_DELETED, repo.group)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.REPOSITORY_DELETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob dataPushed(Repository repo, Commit commit) {
		var currentUser = userService.getCurrentUser();
		var repoUrl = getBaseUrl() + "/" + repo.path();
		var commitUrl = getBaseUrl() + "/" + repo.path() + "/commit/" + commit.id;
		var subject = "Data was pushed";
		var message = "Data was pushed to <a href=\"" + repoUrl + "\">" + repo.path()
				+ "</a> by the user "
				+ currentUser.name + "; last commit was <a href=\"" + commitUrl + "\">" + commit.message + "</a>";
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.DATA_PUSHED, repo.path())));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.DATA_PUSHED, repo.group)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.DATA_PUSHED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob fieldCommented(Comment comment) {
		var currentUser = userService.getCurrentUser();
		var repoUrl = getBaseUrl() + "/" + comment.repositoryPath;
		var commentUrl = repoUrl + "/comments";
		var datasetUrl = repoUrl + "/dataset/" + comment.field.modelType.name() + "/" + comment.field.refId + "/"
				+ comment.field.commitId;
		var subject = "A field was commented";
		var message = "The user " + currentUser.name + " added a comment '<a href=\"" + commentUrl + "\">"
				+ comment.text + "</a>' in <a href=\"" + repoUrl
				+ "\">" + comment.repositoryPath
				+ "</a> on <a href=\"" + datasetUrl + "\">dataset</a>";
		var group = comment.repositoryPath.substring(0, comment.repositoryPath.indexOf('/'));
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message,
				getMemberUsers(Notification.FIELD_COMMENTED, comment.repositoryPath)));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.FIELD_COMMENTED, group)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.FIELD_COMMENTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, User member) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var personalSubject = "You were added to a group";
		var othersSubject = "A member was added to a group";
		var personalMessage = "You were added to group <a href=\"" + url + "\">" + group + "</a> by the user "
				+ currentUser.name;
		var othersMessage = "The user " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.ADDED_TO_GROUP_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.ADDED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(String group, User member) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var personalSubject = "Your role in a group was changed";
		var othersSubject = "A role was changed in a group";
		var personalMessage = "Your role in group <a href=\"" + url + "\">" + group
				+ "</a> was changed by the user " + currentUser.name;
		var othersMessage = "The role of user " + member.name + " for group <a href=\"" + url + "\">" + group
				+ "</a> was changed by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.GROUP_ROLE_CHANGED))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, Team member) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var personalSubject = "A team you are in was added to a group";
		var othersSubject = "A team was added to a group";
		var personalMessage = "A team you are in was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		var othersMessage = "The team " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.ADDED_TO_GROUP_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.ADDED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(String group, Team member) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var personalSubject = "The role of a team you are in was changed for a group";
		var othersSubject = "The role of a team was changed for a group";
		var personalMessage = "The role of a team you are in was changed for group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		var othersMessage = "The role of team " + member.name + " was changed for group <a href=\"" + url + "\">"
				+ group
				+ "</a> by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.GROUP_ROLE_CHANGED, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.GROUP_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(String group, User member) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var personalSubject = "You were removed from a group";
		var othersSubject = "A member was removed from a group";
		var personalMessage = "You were removed from group " + group + " by the user  " + currentUser.name;
		var othersMessage = "The user " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.REMOVED_FROM_GROUP_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REMOVED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(String group, Team member) {
		var currentUser = userService.getCurrentUser();
		var url = getBaseUrl() + "/groups/" + group;
		var personalSubject = "A team you are in was removed from a group";
		var othersSubject = "A team was removed from a group";
		var personalMessage = "A team you are in was removed from group " + group + " by the user "
				+ currentUser.name;
		var othersMessage = "The team " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.REMOVED_FROM_GROUP_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REMOVED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, User member) {
		var currentUser = userService.getCurrentUser();
		var path = repo.path();
		var url = getBaseUrl() + "/" + path;
		var personalSubject = "You were added to a repository";
		var othersSubject = "A member was added to a repository";
		var personalMessage = "You were added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user "
				+ currentUser.name;
		var othersMessage = "The user " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.ADDED_TO_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(Repository repo, User member) {
		var currentUser = userService.getCurrentUser();
		var path = repo.path();
		var url = getBaseUrl() + "/" + path;
		var personalSubject = "Your role in a repository was changed";
		var othersSubject = "A role was changed in a repository ";
		var personalMessage = "Your role in repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user " + currentUser.name;
		var othersMessage = "The role of user " + member.name + " for repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.REPOSITORY_ROLE_CHANGED))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, Team member) {
		var currentUser = userService.getCurrentUser();
		var path = repo.path();
		var url = getBaseUrl() + "/" + path;
		var personalSubject = "A team you are in was added to a repository";
		var othersSubject = "A team was added to a repository";
		var personalMessage = "A team you are in was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user " + currentUser.name;
		var othersMessage = "The team " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.ADDED_TO_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob roleChanged(Repository repo, Team member) {
		var currentUser = userService.getCurrentUser();
		var path = repo.path();
		var url = getBaseUrl() + "/" + path;
		var personalSubject = "The role of a team you are in was changed for a repository";
		var othersSubject = "The role of a team was changed in a repository ";
		var personalMessage = "The role of a team you are in for repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user " + currentUser.name;
		var othersMessage = "The role of team " + member.name + " for repository <a href=\"" + url + "\">" + path
				+ "</a> was changed by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.REPOSITORY_ROLE_CHANGED, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REPOSITORY_ROLE_OF_MEMBER_CHANGED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, User member) {
		var currentUser = userService.getCurrentUser();
		var path = repo.path();
		var url = getBaseUrl() + "/" + path;
		var personalSubject = "You were removed from a repository";
		var othersSubject = "A member was removed from a repository";
		var personalMessage = "You were removed from repository " + path + " by the user "
				+ currentUser.name;
		var othersMessage = "The user " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.REMOVED_FROM_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REMOVED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, Team member) {
		var currentUser = userService.getCurrentUser();
		var path = repo.path();
		var url = getBaseUrl() + "/" + path;
		var personalSubject = "A team you are in was removed from a repository";
		var othersSubject = "A team was removed from a repository";
		var personalMessage = "A team you are in was removed from repository " + path + " by the user "
				+ currentUser.name;
		var othersMessage = "The team " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.REMOVED_FROM_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.REMOVED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REMOVED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Team team, User member) {
		var currentUser = userService.getCurrentUser();
		var personalSubject = "You were added to a team";
		var othersSubject = "A member was added to a team";
		var personalMessage = "You were added to team " + team.name + " by the user " + currentUser.name;
		var othersMessage = "The user " + member.name + " was added to team " + team.name
				+ " by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.ADDED_TO_TEAM_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage, getTeamUsers(Notification.ADDED_TEAM_MEMBER, team)));
		emails.addAll(
				createEmails(othersSubject, othersMessage, getManagerUsers(Notification.ADDED_TEAM_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Team team, User member) {
		var currentUser = userService.getCurrentUser();
		var personalSubject = "You were removed from a team";
		var othersSubject = "A member was removed from a team";
		var personalMessage = "You were removed from team " + team.name + " by the user "
				+ currentUser.name;
		var othersMessage = "The user " + member.name + " was removed from team " + team.name
				+ " by the user "
				+ currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (member.settings.isEnabled(Notification.REMOVED_FROM_TEAM_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage, getTeamUsers(Notification.REMOVED_TEAM_MEMBER, team)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getManagerUsers(Notification.REMOVED_TEAM_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob userCreated(User user, String password) {
		var currentUser = userService.getCurrentUser();
		var managerMessage = "The user " + user.name + " was created by the user  " + currentUser.name;
		var userMessage = "A new account with username " + user.username
				+ " was created for you by the user  "
				+ currentUser.name
				+ ". To login please navigate to " + getBaseUrl()
				+ " and enter your credentials. The password for this was generated by the system: " + password;
		var managers = getManagerUsers(Notification.USER_CREATED, true);
		managers.remove(user); // in case new user is a manager
		var emails = createEmails("A user was created", managerMessage, managers);
		emails.add(createEmail("A user was created for you", userMessage, user));
		return new NotificationJob(emails);
	}

	public NotificationJob userRegistered(User user) {
		var subject = "A new user registered";
		var message = "A new user with username " + user.username + " registered.";
		if (settings.is(ServerSetting.USER_REGISTRATION_APPROVAL_ENABLED)) {
			subject += " and is awaiting approval";
			String profileUrl = getBaseUrl() + "/administration/user/profile/" + user.username;
			message += " To approve the new user account you need to active the user at <a href=\"" + profileUrl + "\">"
					+ profileUrl + "</a>";
		}
		var managers = getManagerUsers(Notification.USER_REGISTERED, true);
		var emails = createEmails(subject, message, managers);
		return new NotificationJob(emails);
	}

	public NotificationJob userDeleted(User user) {
		var currentUser = userService.getCurrentUser();
		var subject = "A user was deleted";
		var message = "The user " + user.name + " was deleted by the user " + currentUser.name;
		var emails = createEmails(subject, message, getManagerUsers(Notification.USER_DELETED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamCreated(Team team) {
		var currentUser = userService.getCurrentUser();
		var subject = "A team was created";
		var message = "The team " + team.name + " was created by the user " + currentUser.name;
		var emails = createEmails(subject, message, getManagerUsers(Notification.TEAM_CREATED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamDeleted(Team team) {
		var currentUser = userService.getCurrentUser();
		var subject = "A team was deleted";
		var message = "The team " + team.name + " was deleted by the user " + currentUser.name;
		var emails = createEmails(subject, message, getManagerUsers(Notification.TEAM_DELETED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob taskStarted(Repository repo, Task task) {
		var currentUser = userService.getCurrentUser();
		var repoId = repo.path();
		var repoUrl = getBaseUrl() + "/" + repoId;
		var taskUrl = getBaseUrl() + "/tasks/" + getUrlPart(task) + task.id;
		var subject = "A task was started";
		var message = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> was started in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getTaskUsers(Notification.TASK_STARTED, repoId, task)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.TASK_STARTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskCompleted(Repository repo, Task task) {
		var currentUser = userService.getCurrentUser();
		var repoId = repo.path();
		var repoUrl = getBaseUrl() + "/" + repoId;
		var taskUrl = getBaseUrl() + "/tasks/" + getUrlPart(task) + task.id;
		var subject = "A task was completed";
		var message = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was completed by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getTaskUsers(Notification.TASK_COMPLETED, repoId, task)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.TASK_COMPLETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskCanceled(Repository repo, Task task) {
		var currentUser = userService.getCurrentUser();
		var repoId = repo.path();
		var repoUrl = getBaseUrl() + "/" + repoId;
		var taskUrl = getBaseUrl() + "/tasks/" + getUrlPart(task) + task.id;
		var subject = "A task was canceled";
		var message = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was canceled by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message, getTaskUsers(Notification.TASK_CANCELED, repoId, task)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.TASK_CANCELED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskAssigned(Repository repo, Task task, TaskAssignment assignment) {
		var currentUser = userService.getCurrentUser();
		var repoId = repo.path();
		var repoUrl = getBaseUrl() + "/" + repoId;
		var taskUrl = getBaseUrl() + "/tasks/" + getUrlPart(task) + task.id;
		var personalSubject = "A task was assigned to you";
		var otherSubject = "A task was assigned to a user";
		var personalMessage = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was assigned to you by the user " + currentUser.name;
		var otherMessage = "The task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was assigned to the user " + assignment.assignedTo.name
				+ " by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (assignment.assignedTo.settings.isEnabled(Notification.TASK_ASSIGNED))
			emails.add(createEmail(personalSubject, personalMessage, assignment.assignedTo));
		emails.addAll(createEmails(otherSubject, otherMessage,
				getMemberUsers(Notification.TASK_ASSIGNED, repoId, Permission.MANAGE_TASK)));
		emails.addAll(createEmails(otherSubject, otherMessage, getManagerUsers(Notification.TASK_ASSIGNED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskCompleted(Repository repo, Task task, TaskAssignment assignment) {
		var currentUser = userService.getCurrentUser();
		var repoId = repo.path();
		var repoUrl = getBaseUrl() + "/" + repoId;
		var taskUrl = getBaseUrl() + "/tasks/" + getUrlPart(task) + task.id;
		var subject = "A task assignment was completed by a user";
		var message = "The assignment to task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was completed by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		emails.addAll(createEmails(subject, message,
				getMemberUsers(Notification.TASK_COMPLETED, repoId, Permission.MANAGE_TASK)));
		emails.addAll(createEmails(subject, message, getManagerUsers(Notification.TASK_COMPLETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob taskRevoked(Repository repo, Task task, TaskAssignment assignment) {
		var currentUser = userService.getCurrentUser();
		var repoId = repo.path();
		var repoUrl = getBaseUrl() + "/" + repoId;
		var taskUrl = getBaseUrl() + "/tasks/" + getUrlPart(task) + task.id;
		var personalSubject = "A task assignment was revoked from you";
		var otherSubject = "A task assignment was revoked from a user";
		var personalMessage = "The assignment to task <a href=\"" + taskUrl + "\">" + task.name
				+ "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was revoked from you by the user " + currentUser.name;
		var otherMessage = "The assignment to task <a href=\"" + taskUrl + "\">" + task.name + "</a> in <a href=\""
				+ repoUrl + "\">" + repoId + "</a> was revoked from the user " + assignment.assignedTo.name
				+ " by the user " + currentUser.name;
		var emails = new HashSet<EmailJob>();
		if (assignment.assignedTo.settings.isEnabled(Notification.TASK_REVOKED))
			emails.add(createEmail(personalSubject, personalMessage, assignment.assignedTo));
		emails.addAll(createEmails(otherSubject, otherMessage,
				getMemberUsers(Notification.TASK_REVOKED, repoId, Permission.MANAGE_TASK)));
		emails.addAll(createEmails(otherSubject, otherMessage, getManagerUsers(Notification.TASK_REVOKED, false)));
		return new NotificationJob(emails);
	}

	private EmailJob createEmail(String subject, String message, User recipient) {
		var emailJob = new EmailJob();
		emailJob.subject = subject;
		emailJob.htmlContent = toEmailContent(message, recipient);
		emailJob.recipient = recipient.email;
		return emailJob;
	}

	private String toEmailContent(String message, User user) {
		var content = "Dear " + user.name + ",<br><br>";
		content += message + "<br><br>";
		content += "<div style=\"font-size:80%;\">This message was automatically sent to you by the system. If you do not wish to receive this type of notification again, you can configure the notification settings in your <a href=\""
				+ getBaseUrl() + "/user/notifications\">profile</a>" + "<br>";
		content += "<hr>";
		var imprint = settings.imprint;
		if (imprint == null)
			return content;
		content += imprint.toEmailFooter();
		return content;
	}

	private String getBaseUrl() {
		return settings.serverConfig.getServerUrl();
	}

	private Set<EmailJob> createEmails(String subject, String message, Set<User> recipients) {
		return recipients.stream()
				.map(recipient -> createEmail(subject, message, recipient))
				.collect(Collectors.toSet());
	}

	private Set<User> getMemberUsers(Notification notification, String path) {
		return getMemberUsers(notification, path, null);
	}

	private Set<User> getMemberUsers(Notification notification, String path, Permission permission) {
		var users = new HashSet<User>();
		var currentUser = userService.getCurrentUser();
		for (var member : membershipService.getMemberships(path)) {
			if (member.user == null)
				continue;
			if (!member.user.settings.isEnabled(notification))
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
		var users = new HashSet<User>();
		var currentUser = userService.getCurrentUser();
		for (var user : team.users) {
			if (!user.settings.isEnabled(notification))
				continue;
			if (currentUser.equals(user))
				continue;
			users.add(user);
		}
		return users;
	}

	private Set<User> getTaskUsers(Notification notification, String repoId, Task task) {
		var users = getMemberUsers(notification, repoId, Permission.MANAGE_TASK);
		var currentUser = userService.getCurrentUser();
		for (var assignment : task.assignments) {
			User user = assignment.assignedTo;
			if (user == null || currentUser.equals(user))
				continue;
			if (!user.settings.isEnabled(notification))
				continue;
			users.add(user);
		}
		return users;
	}

	private Set<User> getManagerUsers(Notification notification, boolean managerMessage) {
		var managers = userService.getUserManagers();
		var users = new HashSet<User>();
		var currentUser = userService.getCurrentUser();
		for (User manager : managers) {
			if (!manager.settings.isEnabled(notification))
				continue;
			if (!managerMessage && !manager.settings.isEnabled(Notification.NOTIFY_FOR_ALL))
				continue;
			if (currentUser.equals(manager))
				continue;
			users.add(manager);
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
			if (!settings.is(ServerSetting.NOTIFICATIONS_ENABLED))
				return;
			for (var job : jobs) {
				emailService.send(job);
			}
		}

	}

}
