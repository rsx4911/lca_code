package com.greendelta.collaboration.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.job.Job;
import com.greendelta.collaboration.model.job.JobResult;
import com.greendelta.collaboration.model.job.JobType;
import com.greendelta.collaboration.service.EmailService.EmailJob;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.Password;

@Service
public class JobService {

	private final Dao<Job> dao;
	private final UserService userService;
	private final EmailService emailService;
	private final SettingsService settings;

	public JobService(Dao<Job> dao, UserService userService, EmailService emailService,
			SettingsService settings) {
		this.dao = dao;
		this.userService = userService;
		this.emailService = emailService;
		this.settings = settings;
	}

	public void requestPasswordReset(String email) {
		User user = userService.getForEmail(email);
		if (user == null)
			return;
		var token = createJob(email, JobType.RESET_PASSWORD);
		var mail = new EmailJob();
		mail.recipient = email;
		mail.subject = "Password reset request";
		mail.htmlContent = getPasswordResetRequestEmailText(user, token);
		emailService.send(mail);
	}

	private String createJob(String data, JobType type) {
		deleteExpired();
		var job = new Job();
		job.type = type;
		job.token = UUID.randomUUID().toString().replace("-", "");
		var validUntil = Calendar.getInstance();
		validUntil.add(Calendar.DAY_OF_MONTH, 1);
		job.validUntil = validUntil.getTime();
		job.data = data;
		job = dao.insert(job);
		return job.token;
	}

	private void deleteExpired() {
		var expired = new ArrayList<Job>();
		for (var job : dao.getAll()) {
			if (!Dates.isBefore(Calendar.getInstance().getTime(), job.validUntil)) {
				expired.add(job);
			}
		}
		dao.delete(expired);
	}

	private String getPasswordResetRequestEmailText(User user, String token) {
		String baseUrl = settings.serverConfig.getServerUrl();
		var resetUrl = baseUrl + "/job?token=" + token + "&type=" + JobType.RESET_PASSWORD;
		var content = "Dear " + user.name + ",<br><br>";
		content += "You requested to reset your password. Please click the link below to proceed with the request, a new password will automatically be set and send to you.<br><br>";
		content += "<a href=\"" + resetUrl + "\">" + resetUrl + "</a><br><br>";
		var imprint = settings.imprint;
		if (imprint == null)
			return content;
		content += imprint.toEmailFooter();
		return content;
	}

	public JobResult run(String token) {
		var job = dao.getFirstForAttribute("token", token);
		if (job == null)
			return JobResult.INVALID;
		dao.delete(job);
		if (!Dates.isBefore(Calendar.getInstance().getTime(), job.validUntil))
			return JobResult.EXPIRED;
		switch (job.type) {
		case RESET_PASSWORD:
			return resetPassword(job.data);
		default:
			return JobResult.ERROR;
		}
	}

	private JobResult resetPassword(String email) {
		var user = userService.getForEmail(email);
		if (user == null || !user.email.equals(email))
			return JobResult.ERROR;
		var password = Password.generate();
		userService.setPassword(user, password);
		user = userService.update(user);
		var mail = new EmailJob();
		mail.recipient = email;
		mail.subject = "Your new password";
		mail.htmlContent = getPasswordResetEmailText(user, password);
		emailService.send(mail);
		return JobResult.SUCCESS;
	}

	private String getPasswordResetEmailText(User user, String password) {
		var content = "Dear " + user.name + ",<br><br>";
		content += "Your password was successfully reset to " + password
				+ " - Please update it directly after logging in.<br><br>";
		var imprint = settings.imprint;
		if (imprint == null)
			return content;
		content += imprint.toEmailFooter();
		return content;
	}

}
