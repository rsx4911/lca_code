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
import com.greendelta.cloud.platform.Imprint;
import com.greendelta.cloud.platform.mail.EmailJob;
import com.greendelta.cloud.platform.mail.EmailService;

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
		String message = "A new group <a href=\"" + url + "\">" + group + "</a> was created by the LCA Cloud user  "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.GROUP_CREATED, true)));
		return new NotificationJob(emails);
	}

	public NotificationJob groupDeleted(String group) {
		User currentUser = userService.getCurrentUser();
		String subject = "A group was deleted";
		String message = "The group " + group + " was deleted by the LCA Cloud user  " + currentUser.name;
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
				+ "</a> was created by the LCA Cloud user  "
				+ currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_CREATED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.REPOSITORY_CREATED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryMoved(Repository oldRepo, Repository newRepo) {
		User currentUser = userService.getCurrentUser();
		String subject = "A repository was deleted";
		String message = "The repository " + oldRepo.toId() + " was moved to " + newRepo.toId()
				+ " by the LCA Cloud user  " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.REPOSITORY_MOVED, newRepo.toId())));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.REPOSITORY_MOVED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob repositoryDeleted(Repository repo) {
		User currentUser = userService.getCurrentUser();
		String subject = "A repository was deleted";
		String message = "The repository " + repo.toId() + " was deleted by the LCA Cloud user  " + currentUser.name;
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
				+ "</a> by the LCA Cloud user  "
				+ currentUser.name + " with message <a href=\"" + commitUrl + "\">" + commit.message + "</a>";
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.DATA_COMMITTED, repo.toId())));
		emails.addAll(createEmails(subject, message, getMemberUsers(Notification.DATA_COMMITTED, repo.group)));
		emails.addAll(createEmails(subject, message, getAdminUsers(Notification.DATA_COMMITTED, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "You were added to a group";
		String othersSubject = "A member was added to a group";
		String personalMessage = "You were added to group " + group + " by the LCA Cloud user  " + currentUser.name;
		String othersMessage = "The user " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the LCA Cloud user  "
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

	public NotificationJob memberAdded(String group, Team member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "A team you are in was added to a group";
		String othersSubject = "A team was added to a group";
		String personalMessage = "A team you are in was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the LCA Cloud user  " + currentUser.name;
		String othersMessage = "The team " + member.name + " was added to group <a href=\"" + url + "\">" + group
				+ "</a> by the LCA Cloud user  "
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

	public NotificationJob memberRemoved(String group, User member) {
		User currentUser = userService.getCurrentUser();
		String url = baseUrl + "/groups/" + group;
		String personalSubject = "You were removed from a group";
		String othersSubject = "A member was removed from a group";
		String personalMessage = "You were removed from group " + group + " by the LCA Cloud user  " + currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by the LCA Cloud user  " + currentUser.name;
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
		String personalMessage = "A team you are in was removed from group " + group + " by the LCA Cloud user  "
				+ currentUser.name;
		String othersMessage = "The team " + member.name + " was removed from group <a href=\"" + url + "\">" + group
				+ "</a> by the LCA Cloud user  " + currentUser.name;
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
				+ "</a> by the LCA Cloud user  "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the LCA Cloud user  " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		if (member.isEnabled(Notification.ADDED_TO_REPOSITORY_MEMBERS))
			emails.add(createEmail(personalSubject, personalMessage, member));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberAdded(Repository repo, Team member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "A team you are in was added to a repository";
		String othersSubject = "A team was added to a repository";
		String personalMessage = "A team you are in was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the LCA Cloud user  " + currentUser.name;
		String othersMessage = "The team " + member.name + " was added to repository <a href=\"" + url + "\">" + path
				+ "</a> by the LCA Cloud user  " + currentUser.name;
		Set<EmailJob> emails = new HashSet<>();
		emails.addAll(createEmails(personalSubject, personalMessage,
				getTeamUsers(Notification.ADDED_TO_REPOSITORY_MEMBERS, member)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getMemberUsers(Notification.ADDED_REPOSITORY_MEMBER, path)));
		emails.addAll(createEmails(othersSubject, othersMessage,
				getAdminUsers(Notification.ADDED_REPOSITORY_MEMBER, false)));
		return new NotificationJob(emails);
	}

	public NotificationJob memberRemoved(Repository repo, User member) {
		User currentUser = userService.getCurrentUser();
		String path = repo.toId();
		String url = baseUrl + "/" + path;
		String personalSubject = "You were removed from a repository";
		String othersSubject = "A member was removed from a repository";
		String personalMessage = "You were removed from repository " + path + " by the LCA Cloud user  "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by the LCA Cloud user  "
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
		String personalMessage = "A team you are in was removed from repository " + path + " by the LCA Cloud user  "
				+ currentUser.name;
		String othersMessage = "The team " + member.name + " was removed from repository <a href=\"" + url + "\">"
				+ path + "</a> by the LCA Cloud user  " + currentUser.name;
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
		String personalMessage = "You were added to team " + team.name + " by the LCA Cloud user  " + currentUser.name;
		String othersMessage = "The user " + member.name + " was added to team " + team.name
				+ " by the LCA Cloud user  "
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
		String personalMessage = "You were removed from team " + team.name + " by the LCA Cloud user  "
				+ currentUser.name;
		String othersMessage = "The user " + member.name + " was removed from team " + team.name
				+ " by the LCA Cloud user  "
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
		String adminMessage = "The user " + user.name + " was created by the LCA Cloud user  " + currentUser.name;
		String userMessage = "A new account with username " + user.username
				+ " was created for you by the LCA Cloud user  "
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
		String message = "The user " + user.name + " was deleted by the LCA Cloud user  " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminUsers(Notification.USER_DELETED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamCreated(Team team) {
		User currentUser = userService.getCurrentUser();
		String subject = "A team was created";
		String message = "The team " + team.name + " was created by the LCA Cloud user  " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminUsers(Notification.TEAM_CREATED, true));
		return new NotificationJob(emails);
	}

	public NotificationJob teamDeleted(Team team) {
		User currentUser = userService.getCurrentUser();
		String subject = "A team was deleted";
		String message = "The team " + team.name + " was deleted by the LCA Cloud user  " + currentUser.name;
		Set<EmailJob> emails = createEmails(subject, message, getAdminUsers(Notification.TEAM_DELETED, true));
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
		content += "<div style=\"font-size:80%;\">This message was automatically sent to you by the LCA Cloud system. If you do not wish to receive this type of notification again, you can configure the notification settings in your <a href=\""
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
		Set<User> users = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (Membership member : membershipService.getMemberships(path)) {
			if (member.user == null)
				continue;
			if (!member.user.isEnabled(notification))
				continue;
			if (currentUser.equals(member.user))
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
