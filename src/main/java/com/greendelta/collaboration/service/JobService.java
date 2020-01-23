package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting.Key;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.job.Job;
import com.greendelta.collaboration.model.job.JobResult;
import com.greendelta.collaboration.model.job.JobType;
import com.greendelta.collaboration.platform.mail.EmailJob;
import com.greendelta.collaboration.platform.mail.EmailService;
import com.greendelta.collaboration.service.SettingsService.Imprint;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Password;

public class JobService {

	private final Dao<Job> dao;
	private final UserService userService;
	private final EmailService emailService;
	private final SettingsService settingsService;

	@Inject
	public JobService(Dao<Job> dao, UserService userService, EmailService emailService,
			SettingsService settingsService) {
		this.dao = dao;
		this.userService = userService;
		this.emailService = emailService;
		this.settingsService = settingsService;
	}

	public void requestPasswordReset(String email) {
		User user = userService.getForEmail(email);
		if (user == null)
			return;
		String token = createJob(email, JobType.RESET_PASSWORD);
		EmailJob mail = new EmailJob();
		mail.setRecipient(email);
		mail.setSubject("Password reset request");
		mail.setHtmlContent(getPasswordResetRequestEmailText(user, token));
		emailService.send(mail);
	}

	private String createJob(String data, JobType type) {
		deleteExpired();
		Job job = new Job();
		job.type = type;
		job.token = UUID.randomUUID().toString().replace("-", "");
		Calendar validUntil = Calendar.getInstance();
		validUntil.add(Calendar.DAY_OF_MONTH, 1);
		job.validUntil = validUntil.getTime();
		job.data = data;
		job = dao.insert(job);
		return job.token;
	}
	
	private void deleteExpired() {
		List<Job> expired = new ArrayList<>();
		for (Job job : dao.getAll()) {
			if (!Dates.isBefore(Calendar.getInstance().getTime(), job.validUntil)) {
				expired.add(job);
			}
		}
		dao.delete(expired);
	}

	private String getPasswordResetRequestEmailText(User user, String token) {
		String baseUrl = settingsService.get(Key.SERVER_URL);
		String resetUrl = baseUrl + "/job?token=" + token + "&type=" + JobType.RESET_PASSWORD;
		String content = "Dear " + user.name + ",<br><br>";
		content += "You requested to reset your password. Please click the link below to proceed with the request, a new password will automatically be set and send to you.<br><br>";
		content += "<a href=\"" + resetUrl + "\">" + resetUrl + "</a><br><br>";
		Imprint imprint = settingsService.getImprint();
		if (imprint == null)
			return content;
		content += imprint.toEmailFooter();
		return content;
	}

	public JobResult run(String token) {
		Job job = dao.getFirstForAttribute("token", token);
		if (job == null)
			return JobResult.INVALID;
		dao.delete(job);
		if (!Dates.isBefore(Calendar.getInstance().getTime(), job.validUntil))
			return JobResult.EXPIRED;
		switch (job.type) {
		case RESET_PASSWORD:
			return resetPassword(job.data);
		}
		return JobResult.ERROR;
	}

	private JobResult resetPassword(String email) {
		User user = userService.getForEmail(email);
		if (user == null || !user.email.equals(email))
			return JobResult.ERROR;
		String password = Password.generate();
		userService.setPassword(user, password);
		user = userService.update(user);
		EmailJob mail = new EmailJob();
		mail.setRecipient(email);
		mail.setSubject("Your new password");
		mail.setHtmlContent(getPasswordResetEmailText(user, password));
		emailService.send(mail);
		return JobResult.SUCCESS;
	}

	private String getPasswordResetEmailText(User user, String password) {
		String content = "Dear " + user.name + ",<br><br>";
		content += "Your password was successfully reset to " + password
				+ " - Please update it directly after logging in.<br><br>";
		Imprint imprint = settingsService.getImprint();
		if (imprint == null)
			return content;
		content += imprint.toEmailFooter();
		return content;
	}

}
