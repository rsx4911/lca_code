package com.greendelta.collaboration.platform.mail;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;
import java.util.NoSuchElementException;

import javax.mail.Message.RecipientType;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.greendelta.collaboration.platform.mail.EmailJob.Attachment;
import com.greendelta.collaboration.platform.mail.EmailJob.EmbeddedImage;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.SettingsService.MailConfig;
import com.sun.mail.smtp.SMTPMessage;

@Singleton
public class EmailService implements Closeable {

	private static final Logger log = LogManager.getLogger(EmailService.class);

	private final int senderThreads = 1;
	private final int mailsSentPerConnection = 10;
	private final int connectionLifetimeMs = 60000;
	private final GenericObjectPool<TransportHolder> pool;
	private final SettingsService settingsService;

	@Inject
	public EmailService(SettingsService settingsService) {
		this.settingsService = settingsService;
		pool = new GenericObjectPool<>(new TransportObjectFactory(this), senderThreads,
				GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 8000, senderThreads, true, true, 500, 1000, -1, true);
		pool.setMaxActive(senderThreads);
		pool.setMaxIdle(senderThreads);
	}

	Transport getTransport() {
		MailConfig config = settingsService.getMailConfig();
		boolean useAuth = !Strings.isNullOrEmpty(config.user);
		Transport transport = null;
		try {
			transport = config.getSession().getTransport(config.proto);
			if (useAuth)
				transport.connect(config.user, config.pass);
			else
				transport.connect();
		} catch (Exception e) {
			log.error("Error when trying to connect to transport", e);
			try {
				if (transport != null)
					transport.close();
			} catch (Exception ex) {
				log.error("Closing transport after exception failed: {}", ex.getMessage());
			}
		}
		return transport;
	}

	@Override
	public void close() throws IOException {
		if (pool == null)
			return;
		try {
			pool.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void send(EmailJob mail) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				TransportHolder transportHolder = null;
				try {
					transportHolder = pool.borrowObject();
					log.info("Sending mail with subject {} to {}", mail.subject, mail.recipient);
					transportHolder.incrementMailsSent();
					send(mail, transportHolder.getTransport());
				} catch (NoSuchElementException nse) {
					log.error("Mail sending failed, overloaded or server down?", nse);
				} catch (Exception e) {
					log.error("Unknow error sending mail", e);
				} finally {
					try {
						pool.returnObject(transportHolder);
					} catch (Exception e) {
						log.error("Error while returning transport holder to pool", e);
					}
				}
			}
		}).start();
	}

	private void send(EmailJob mail, Transport transport) throws Exception {
		try {
			MailConfig config = settingsService.getMailConfig();
			SMTPMessage message = new SMTPMessage(config.getSession());
			message.setRecipient(RecipientType.TO, mail.recipient);
			message.setSentDate(Calendar.getInstance().getTime());
			message.setSubject(mail.subject, "utf-8");
			message.setFrom(new InternetAddress(config.defaultFrom));
			for (InternetAddress bcc : mail.bcc)
				message.addRecipient(RecipientType.BCC, bcc);
			if (mail.isMixedContent())
				message.setContent(createMixedContent(mail));
			else
				message.setContent(createContent(mail));
			message.setReplyTo(new InternetAddress[] { new InternetAddress(config.defaultReplyTo) });
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			log.error("Error: SendMailJobImpl.send()", e);
			throw e;
		}
	}

	private MimeMultipart createContent(EmailJob mail) throws Exception {
		MimeMultipart content = new MimeMultipart();
		if (mail.textContent != null || mail.htmlContent == null) {
			String textContent = mail.textContent != null ? mail.textContent : "";
			content.addBodyPart(createPart(textContent, "plain"));
		} else if (mail.htmlContent != null)
			content.addBodyPart(createRelated(mail, mail.htmlContent, "html"));
		for (Attachment attachment : mail.attachments)
			content.addBodyPart(createPart(attachment));
		return content;
	}

	private MimeMultipart createMixedContent(EmailJob mail) throws Exception {
		MimeMultipart content = new MimeMultipart();
		content.addBodyPart(createAlternative(mail));
		for (Attachment attachment : mail.attachments)
			content.addBodyPart(createPart(attachment));
		return content;
	}

	private MimeBodyPart createAlternative(EmailJob mail) throws Exception {
		MimeMultipart alternative = new MimeMultipart("alternative");
		alternative.addBodyPart(createPart(mail.textContent, "plain"));
		alternative.addBodyPart(createRelated(mail, mail.htmlContent, "html"));
		return wrap(alternative);
	}

	private MimeBodyPart createRelated(EmailJob mail, String text, String type) throws Exception {
		MimeMultipart related = new MimeMultipart("related");
		related.addBodyPart(createPart(text, type));
		for (EmbeddedImage image : mail.embeddedImages)
			related.addBodyPart(createPart(image));
		return wrap(related);
	}

	private MimeBodyPart wrap(MimeMultipart toWrap) throws Exception {
		MimeBodyPart wrapped = new MimeBodyPart();
		wrapped.setContent(toWrap);
		return wrapped;
	}

	private MimeBodyPart createPart(String text, String type) throws Exception {
		MimeBodyPart part = new MimeBodyPart();
		part.setContent(text, "text/" + type);
		return part;
	}

	private MimeBodyPart createPart(Attachment attachment) throws Exception {
		MimeBodyPart part = new MimeBodyPart();
		part.setContent(attachment.data, attachment.contentType);
		part.setFileName(attachment.filename);
		return part;
	}

	private MimeBodyPart createPart(EmbeddedImage image) throws Exception {
		MimeBodyPart part = new MimeBodyPart();
		part.setContentID("<" + image.cid + ">");
		part.setContent(image.data, "image/" + image.imageType);
		part.setDisposition(MimeBodyPart.INLINE);
		return part;
	}

	int getSenderThreads() {
		return senderThreads;
	}

	int getMailsSentPerConnection() {
		return mailsSentPerConnection;
	}

	int getConnectionLifetimeMs() {
		return connectionLifetimeMs;
	}

}
