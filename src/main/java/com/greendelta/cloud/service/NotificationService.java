package com.greendelta.cloud.service;

import static com.greendelta.cloud.model.Notification.ADDED_TO_GROUP_MEMBERS;
import static com.greendelta.cloud.model.Notification.ADDED_TO_REPOSITORY_MEMBERS;
import static com.greendelta.cloud.model.Notification.ADDED_TO_TEAM_MEMBERS;
import static com.greendelta.cloud.model.Notification.DATA_COMMITTED;
import static com.greendelta.cloud.model.Notification.GROUP_CREATED;
import static com.greendelta.cloud.model.Notification.GROUP_DELETED;
import static com.greendelta.cloud.model.Notification.REMOVED_FROM_GROUP_MEMBERS;
import static com.greendelta.cloud.model.Notification.REMOVED_FROM_REPOSITORY_MEMBERS;
import static com.greendelta.cloud.model.Notification.REMOVED_FROM_TEAM_MEMBERS;
import static com.greendelta.cloud.model.Notification.REPOSITORY_CREATED;
import static com.greendelta.cloud.model.Notification.REPOSITORY_DELETED;
import static com.greendelta.cloud.model.Notification.TEAM_CREATED;
import static com.greendelta.cloud.model.Notification.TEAM_DELETED;
import static com.greendelta.cloud.model.Notification.USER_CREATED;
import static com.greendelta.cloud.model.Notification.USER_DELETED;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
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

	@Inject
	public NotificationService(UserService userService, MembershipService membershipService, EmailService emailService) {
		this.userService = userService;
		this.membershipService = membershipService;
		this.emailService = emailService;
	}

	public NotificationJob createNotificationToGroupOrRepository(Notification notification, String path) {
		return createNotificationToGroupOrRepository(notification, path, null);
	}

	public NotificationJob createNotificationToGroupOrRepository(Notification notification, String path, User user) {
		// User display name is relevant when adding/removing members
		if (!notification.isOneOf(GROUP_CREATED, GROUP_DELETED, REPOSITORY_CREATED, REPOSITORY_DELETED, DATA_COMMITTED,
				ADDED_TO_GROUP_MEMBERS, REMOVED_FROM_GROUP_MEMBERS, ADDED_TO_REPOSITORY_MEMBERS,
				REMOVED_FROM_REPOSITORY_MEMBERS))
			return new NotificationJob();
		Set<EmailJob> jobs = new HashSet<>();
		if (path.contains(File.separator)) {
			String group = path.substring(0, path.indexOf(File.separator));
			String repo = path.substring(path.indexOf(File.separator) + 1);
			jobs.addAll(createEmailsToMembersOf(notification, group, user));
			jobs.addAll(createEmailsToMembersOf(notification, repo, user));
		} else
			jobs.addAll(createEmailsToMembersOf(notification, path, user));
		jobs.addAll(createEmailsToAdmins(notification));
		return new NotificationJob(jobs);
	}

	public NotificationJob createTeamNotificationToUser(Notification notification, Team team, User user) {
		// User display name is relevant when adding/removing members
		if (!notification.isOneOf(ADDED_TO_TEAM_MEMBERS, REMOVED_FROM_TEAM_MEMBERS))
			return new NotificationJob();
		User currentUser = userService.getCurrentUser();
		if (currentUser.equals(user))
			return new NotificationJob();
		Set<EmailJob> jobs = new HashSet<>();
		jobs.add(createEmail(notification, user, true));
		jobs.addAll(createEmailsToAdmins(notification));
		return new NotificationJob(jobs);
	}

	public NotificationJob createAdminNotification(Notification notification) {
		if (!notification.isOneOf(USER_CREATED, USER_DELETED, TEAM_CREATED, TEAM_DELETED))
			return new NotificationJob();
		Set<EmailJob> jobs = new HashSet<>();
		jobs.addAll(createEmailsToAdmins(notification));
		return new NotificationJob(jobs);
	}

	private Collection<EmailJob> createEmailsToMembersOf(Notification notification, String path, User user) {
		Set<EmailJob> jobs = new HashSet<>();
		User currentUser = userService.getCurrentUser();
		for (Membership member : membershipService.getMemberships(path)) {
			if (!member.user.isEnabled(notification))
				continue;
			if (currentUser.equals(member.user))
				continue;
			jobs.add(createEmail(notification, member.user, member.user.equals(user)));
		}
		return jobs;
	}

	private Collection<EmailJob> createEmailsToAdmins(Notification notification) {
		List<User> admins = userService.getAdmins();
		List<EmailJob> jobs = new ArrayList<>();
		for (User admin : admins) {
			if (!admin.isEnabled(notification))
				continue;
			if (!admin.isEnabled(Notification.NOTIFY_FOR_ALL))
				continue;
			jobs.add(createEmail(notification, admin, false));
		}
		return jobs;
	}

	private EmailJob createEmail(Notification notification, User to, boolean directlyToUser) {
		EmailJob job = new EmailJob();
		job.setSubject(getTitle(notification, false));
		job.setRecipient(to.email);
		return job;
	}

	private String getTitle(Notification notification, boolean directlyToUser) {
		String subject = directlyToUser ? "You were" : "A user was";
		switch (notification) {
		case GROUP_DELETED:
			return "A group was deleted";
		case REPOSITORY_CREATED:
			return "A repository was created";
		case REPOSITORY_DELETED:
			return "A repository was deleted";
		case DATA_COMMITTED:
			return "Data was committed";
		case ADDED_TO_GROUP_MEMBERS:
			return subject + " added to a group";
		case REMOVED_FROM_GROUP_MEMBERS:
			return subject + " removed from a group";
		case ADDED_TO_REPOSITORY_MEMBERS:
			return subject + " added to a repository";
		case REMOVED_FROM_REPOSITORY_MEMBERS:
			return subject + " removed from a repository";
		case ADDED_TO_TEAM_MEMBERS:
			return subject + " added to a team";
		case REMOVED_FROM_TEAM_MEMBERS:
			return subject + " removed from a team";
		case USER_CREATED:
			return "A new user was created";
		case USER_DELETED:
			return "A user was deleted";
		case TEAM_CREATED:
			return "A new team was created";
		case TEAM_DELETED:
			return "A team was deleted";
		case GROUP_CREATED:
			return "A group was created";
		default:
			return null;
		}
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
