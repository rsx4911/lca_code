package com.greendelta.collaboration.platform.mail;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;
import java.util.NoSuchElementException;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.greendelta.collaboration.platform.mail.EmailJob.Attachment;
import com.greendelta.collaboration.platform.mail.EmailJob.EmbeddedImage;
import com.sun.mail.smtp.SMTPMessage;

@Singleton
public class EmailService implements Closeable {

	private static final Logger log = LoggerFactory
			.getLogger(EmailService.class);

	private final Session session;
	private final InternetAddress defaultFrom;
	private final InternetAddress defaultReplyTo;
	private int senderThreads = 1;
	private int mailsSentPerConnection = 10;
	private int connectionLifetimeMs = 60000;
	private GenericObjectPool<TransportHolder> pool;

	@Inject
	public EmailService(Session session, @Named("defaultFrom") InternetAddress defaultFrom,
			@Named("defaultReplyTo") InternetAddress defaultReplyTo) {
		this.session = session;
		this.defaultFrom = defaultFrom;
		this.defaultReplyTo = defaultReplyTo;
	}

	public void init(Provider<Transport> transportProvider) {
		pool = new GenericObjectPool<>(new TransportObjectFactory(this, transportProvider), senderThreads,
				GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 8000, senderThreads, true, true, 500, 1000, -1, true);
		pool.setMaxActive(senderThreads);
		pool.setMaxIdle(senderThreads);
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
					log.debug("Sending mail with subject {} to {}", mail.subject, mail.recipient);
					transportHolder.incrementMailsSent();
					send(mail, transportHolder.getTransport());
				} catch (NoSuchElementException nse) {
					log.debug("Mail sending failed, overloaded or server down?", nse);
				} catch (Exception e) {
					log.error("Unknow error sending mail", e);
				} finally {
					try {
						pool.returnObject(transportHolder);
					} catch (Exception e) {
						log.debug("Error while returning transport holder to pool", e);
					}
				}
			}
		}).start();
	}

	private void send(EmailJob mail, Transport transport) throws Exception {
		try {
			SMTPMessage message = new SMTPMessage(session);
			message.setRecipient(RecipientType.TO, mail.recipient);
			message.setSentDate(Calendar.getInstance().getTime());
			message.setSubject(mail.subject, "utf-8");
			message.setFrom(defaultFrom);
			for (InternetAddress bcc : mail.bcc)
				message.addRecipient(RecipientType.BCC, bcc);
			if (mail.isMixedContent())
				message.setContent(createMixedContent(mail));
			else
				message.setContent(createContent(mail));
			message.setReplyTo(new InternetAddress[] { defaultReplyTo });
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			log.debug("Error: SendMailJobImpl.send()");
			throw e;
		}
	}

	private MimeMultipart createContent(EmailJob mail) throws Exception {
		MimeMultipart content = new MimeMultipart();
		if (mail.textContent != null)
			content.addBodyPart(createPart(mail.textContent, "plain"));
		else if (mail.htmlContent != null)
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
