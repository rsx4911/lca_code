package com.greendelta.cloud.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openlca.cloud.model.data.Commit;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.greendelta.cloud.model.Membership;
import com.greendelta.cloud.model.Notification;
import com.greendelta.cloud.model.Team;
import com.greendelta.cloud.model.User;
import com.greendelta.cloud.platform.mail.EmailJob;
import com.greendelta.cloud.platform.mail.EmailService;

public class NotificationService {

	private final UserService userService;
	private final MembershipService membershipService;
	private final EmailService emailService;
	private final String baseUrl;

	@Inject
	public NotificationService(UserService userService, MembershipService membershipService, EmailService emailService,
			@Named("base.url") String baseUrl) {
		this.userService = userService;
		this.membershipService = membershipService;
		this.emailService = emailService;
		this.baseUrl = baseUrl;
	}

	public NotificationJob groupCreated(String group) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String subject = "A new group was created";
		String message = "A new group <a href=\"" + url + "\">" + group + "</a> was created by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getAdminEmails(Notification.GROUP_CREATED, true)));
		return new NotificationJob(emails);
	}

	public NotificationJob groupDeleted(String group) {
		User currentUser = userService.getCurrentUser();
		String subject = "A group was deleted";
		String message = "The group " + group + " was deleted by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberEmails(Notification.GROUP_DELETED, group)));
		emails.addAll(createEmails(subject, message, getAdminEmails(Notification.GROUP_DELETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryCreated(Repository repo) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/" + repo.toId();
		String subject = "A new repository was created";
		String message = "A new repository <a href=\"" + url + "\">" + repo.toId() + "</a> was created by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberEmails(Notification.REPOSITORY_CREATED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminEmails(Notification.REPOSITORY_CREATED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryDeleted(Repository repo) {
		User currentUser = userService.getCurrentUser();
		String subject = "A repository was deleted";
		String message = "The repository " + repo.toId() + " was deleted by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberEmails(Notification.REPOSITORY_DELETED, repo.toId())));
		emails.addAll(createEmails(subject, message, getMemberEmails(Notification.REPOSITORY_DELETED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminEmails(Notification.REPOSITORY_DELETED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob dataCommitted(Repository repo, Commit commit) {
		User currentUser = userService.getCurrentUser();
		String repoUrl = baseUrl + "/" + repo.toId();
		String commitUrl = baseUrl + "/" + repo.toId() + "/commit/" + commit.id;
		String subject = "Data was committed";
		String message = "Data was committed to <a href=\"" + repoUrl + "\">" + repo.toId() + "</a> by user "
				+ currentUser.name + " with message <a href=\"" + commitUrl + "\">" + commit.message + "</a>";
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberEmails(Notification.DATA_COMMITTED, repo.toId())));
		emails.addAll(createEmails(subject, message, getMemberEmails(Notification.DATA_COMMITTED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminEmails(Notification.DATA_COMMITTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "You were added to a group";
		String othersSubject = "A member was added to a group";
		String personalMessage = "You were added to group " + group + " by user " + currentUser.name;
		String othersMessage = "The user " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_GROUP_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member.email));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.ADDED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.ADDED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, Team member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "A team you are in was added to a group";
		String othersSubject = "A team was added to a group";
		String personalMessage = "A team you are in was added to group <a href=\"" + url + "\">" + group
				+ "</a> by user " + currentUser.name;
		String othersMessage = "The team " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamEmails(Notification.ADDED_TO_GROUP_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.ADDED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.ADDED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "You were removed from a group";
		String othersSubject = "A member was removed from a group";
		String personalMessage = "You were removed from group " + group + " by user " + currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REMOVED_FROM_GROUP_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member.email));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.REMOVED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.REMOVED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(String group, Team member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "A team you are in was removed from a group";
		String othersSubject = "A team was removed from a group";
		String personalMessage = "A team you are in was removed from group " + group + " by user " + currentUser.name;
		String othersMessage = "The team " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamEmails(Notification.REMOVED_FROM_GROUP_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.REMOVED_GROUP_MEMBER, group)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.REMOVED_GROUP_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, User member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "You were added to a repository";
		String othersSubject = "A member was added to a repository";
		String personalMessage = "You were added to repository <a href=\"" + url + "\">" + path + "</a> by user "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member.email));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, Team member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "A team you are in was added to a repository";
		String othersSubject = "A team was added to a repository";
		String personalMessage = "A team you are in was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by user " + currentUser.name;
		String othersMessage = "The team " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamEmails(Notification.ADDED_TO_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, User member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "You were removed from a repository";
		String othersSubject = "A member was removed from a repository";
		String personalMessage = "You were removed from repository " + path + " by user " + currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REMOVED_FROM_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member.email));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.REMOVED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.REMOVED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, Team member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "A team you are in was removed from a repository";
		String othersSubject = "A team was removed from a repository";
		String personalMessage = "A team you are in was removed from repository " + path + " by user "
				+ currentUser.name;
		String othersMessage = "The team " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by user " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamEmails(Notification.REMOVED_FROM_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberEmails(Notification.REMOVED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.REMOVED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Team team, User member) {
		User currentUser = userService.getCurrentUser();
		String personalSubject = "You were added to a team";
		String othersSubject = "A member was added to a team";
		String personalMessage = "You were added to team " + team.name + " by user " + currentUser.name;
		String othersMessage = "The user " + member.name + " was added to team " + team.name + " by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_TEAM_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member.name));
		emails.addAll(createEmails(othersSubject, othersMessage, getTeamEmails(Notification.ADDED_TEAM_MEMBER, team)));
		emails.addAll(createEmails(othersSubject, othersMessage, getAdminEmails(Notification.ADDED_TEAM_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Team team, User member) {
		User currentUser = userService.getCurrentUser();
		String personalSubject = "You were removed from a team";
		String othersSubject = "A member was removed from a team";
		String personalMessage = "You were removed from team " + team.name + " by user " + currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from team " + team.name + " by user "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.REMOVED_FROM_TEAM_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member.name));
		emails.addAll(createEmails(othersSubject, othersMessage, getTeamEmails(Notification.REMOVED_TEAM_MEMBER, team)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminEmails(Notification.REMOVED_TEAM_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob userCreated(User user) {
		User currentUser = userService.getCurrentUser();
		String subject = "A user was created";
		String message = "The user " + user.name + " was created by user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminEmails(Notification.USER_CREATED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob userDeleted(User user) {
		User currentUser = userService.getCurrentUser();
		String subject = "A user was deleted";
		String message = "The user " + user.name + " was deleted by user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminEmails(Notification.USER_DELETED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamCreated(Team team) {
		User currentUser = userService.getCurrentUser();
		String subject = "A team was created";
		String message = "The team " + team.name + " was created by user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminEmails(Notification.TEAM_CREATED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamDeleted(Team team) {
		User currentUser = userService.getCurrentUser();
		String subject = "A team was deleted";
		String message = "The team " + team.name + " was deleted by user " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminEmails(Notification.TEAM_DELETED, true));
		return new NotificationJob(emails);
	}

	private EmailJob createEmail(String subject, String message, String recipient) {
		EmailJob emailJob = new EmailJob();
		emailJob.setSubject(subject);
		emailJob.setHtmlContent(message);
		emailJob.setRecipient(recipient);
		return emailJob;
	}

	private Set<EmailJob> createEmails(String subject, String message, Set<String> recipients) {
		Set<EmailJob> emails = new HashSet<>();
		for (String recipient : recipients)
			emails.add(createEmail(subject, message, recipient));
		return emails;
	}

	private Set<String> getMemberEmails(Notification notification, String path) {
		Set<String> emails = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (Membership member : membershipService.getMemberships(path)) {
			if (!member.user.isEnabled(notification))
				continue;
			if (currentUser.equals(member.user))
				continue;
			emails.add(member.user.email);
		}
		return emails;
	}

	private Set<String> getTeamEmails(Notification notification, Team team) {
		Set<String> emails = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (User user : team.users) {
			if (!user.isEnabled(notification))
				continue;
			if (currentUser.equals(user))
				continue;
			emails.add(user.email);
		}
		return emails;
	}

	private Set<String> getAdminEmails(Notification notification, boolean adminMessage) {
		List<User> admins = userService.getAdmins();
		Set<String> emails = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (User admin : admins) {
			if (!admin.isEnabled(notification))
				continue;
			if (!adminMessage && !admin.isEnabled(Notification.NOTIFY_FOR_ALL))
				continue;
			if (currentUser.equals(admin))
				continue;
			emails.add(admin.email);
		}
		return emails;
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
