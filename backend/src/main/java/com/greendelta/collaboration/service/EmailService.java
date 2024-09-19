package com.greendelta.collaboration.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.MailSetting;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailService {

	private static final Logger log = LogManager.getLogger(EmailService.class);
	private final SettingsService settings;

	public EmailService(SettingsService settings) {
		this.settings = settings;
	}

	public void send(EmailJob mail) {
		var config = settings.mailConfig;
		var sender = config.getMailSender();
		try {
			var message = sender.createMimeMessage();
			message.setRecipient(RecipientType.TO, new InternetAddress(mail.recipient));
			message.setSentDate(Calendar.getInstance().getTime());
			message.setSubject(mail.subject, StandardCharsets.UTF_8.name());
			message.setFrom(new InternetAddress(config.get(MailSetting.DEFAULT_FROM)));
			if (mail.isMixedContent()) {
				message.setContent(createMixedContent(mail));
			} else {
				message.setContent(createContent(mail));
			}
			String defaultReplyTo = config.get(MailSetting.DEFAULT_REPLY_TO);
			if (!Strings.nullOrEmpty(defaultReplyTo)) {
				message.setReplyTo(new InternetAddress[] { new InternetAddress(defaultReplyTo) });
			}
			message.saveChanges();
			new Thread(() -> {
				sender.send(message);
			}).start();
		} catch (MessagingException e) {
			log.error("Error sending mail", e);
		}
	}

	private MimeMultipart createContent(EmailJob mail) throws MessagingException {
		var content = new MimeMultipart();
		if (mail.textContent != null || mail.htmlContent == null) {
			var textContent = mail.textContent != null ? mail.textContent : "";
			content.addBodyPart(createPart(textContent, "plain"));
		} else if (mail.htmlContent != null) {
			content.addBodyPart(createRelated(mail, mail.htmlContent, "html"));
		}
		return content;
	}

	private MimeMultipart createMixedContent(EmailJob mail) throws MessagingException {
		var content = new MimeMultipart();
		content.addBodyPart(createAlternative(mail));
		return content;
	}

	private MimeBodyPart createAlternative(EmailJob mail) throws MessagingException {
		var alternative = new MimeMultipart("alternative");
		alternative.addBodyPart(createPart(mail.textContent, "plain"));
		alternative.addBodyPart(createRelated(mail, mail.htmlContent, "html"));
		return wrap(alternative);
	}

	private MimeBodyPart createRelated(EmailJob mail, String text, String type) throws MessagingException {
		var related = new MimeMultipart("related");
		related.addBodyPart(createPart(text, type));
		return wrap(related);
	}

	private MimeBodyPart wrap(MimeMultipart toWrap) throws MessagingException {
		var wrapped = new MimeBodyPart();
		wrapped.setContent(toWrap);
		return wrapped;
	}

	private MimeBodyPart createPart(String text, String type) throws MessagingException {
		var part = new MimeBodyPart();
		part.setContent(text, "text/" + type + "; charset=" + StandardCharsets.UTF_8.name());
		return part;
	}

	public static class EmailJob {

		public String recipient;
		public String subject;
		public String htmlContent;
		public String textContent;

		private boolean isMixedContent() {
			return textContent != null && htmlContent != null;
		}
	}

}
