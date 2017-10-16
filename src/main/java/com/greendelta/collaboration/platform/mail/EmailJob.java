package com.greendelta.collaboration.platform.mail;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EmailJob {

	private static final Logger log = LogManager.getLogger(EmailJob.class);
	private static final Pattern domPattern = Pattern.compile("[^\\s>]+");

	InternetAddress recipient;
	List<InternetAddress> bcc = new ArrayList<>();
	String subject;
	String textContent;
	String htmlContent;
	List<Attachment> attachments = new ArrayList<>();
	List<EmbeddedImage> embeddedImages = new ArrayList<>();

	public void setRecipient(String recipient) {
		this.recipient = toJavaMail(recipient);
	}

	public void addBcc(String bcc) {
		this.bcc.add(toJavaMail(bcc));
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public void addAttachment(String filename, byte[] data, String type) {
		attachments.add(new Attachment(filename, data, type));
	}

	public void addEmbeddedImage(String cid, byte[] data, String type) {
		embeddedImages.add(new EmbeddedImage(cid, data, type));
	}

	boolean isMixedContent() {
		return textContent != null && htmlContent != null;
	}

	/**
	 * Currently supports encoding of unicode-domains. Not: Unicode in local
	 * part (still only experimental UTF8SMPT only supported by few MTAs).
	 * 
	 * @param address
	 * @return
	 * @throws AddressException
	 */
	static InternetAddress toJavaMail(String address) {
		if (address == null)
			return null;
		try {
			int atPos = address.lastIndexOf('@');
			Matcher matcher = domPattern.matcher(address);
			if (!matcher.find(atPos + 1))
				return new InternetAddress(address);
			int end = matcher.end();
			String newAddr = "" + address.substring(0, atPos + 1) + IDN.toASCII(matcher.group())
					+ address.substring(end);
			return new InternetAddress(newAddr);
		} catch (AddressException e) {
			log.error("Error parsing email address " + address, e);
			return null;
		}
	}

	static class Attachment {

		final String filename;
		final byte[] data;
		final String contentType;

		private Attachment(String filename, byte[] data, String contentType) {
			this.filename = filename;
			this.data = data;
			this.contentType = contentType;
		}

	}

	static class EmbeddedImage {

		final String cid;
		final byte[] data;
		final String imageType;

		private EmbeddedImage(String cid, byte[] data, String imageType) {
			this.cid = cid;
			this.imageType = imageType;
			this.data = data;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EmailJob))
			return false;
		EmailJob job = (EmailJob) obj;
		return job.recipient.getAddress().equals(recipient.getAddress());
	}

	@Override
	public int hashCode() {
		return recipient.getAddress().hashCode();
	}

}
