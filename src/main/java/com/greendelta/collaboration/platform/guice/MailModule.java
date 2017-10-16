package com.greendelta.collaboration.platform.guice;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openlca.cloud.util.Logs;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.collaboration.platform.guice.util.StartupListener;
import com.greendelta.collaboration.platform.mail.EmailService;

/**
 * Sending should happen with SMTPMessage if FROM needs customization.
 */
class MailModule extends AbstractModule {

	private static final Logger log = LogManager.getLogger(MailModule.class);

	@Override
	protected void configure() {
		install(ThrowingProviderBinder.forModule(this));
		BindUtils.multibind(binder(), StartupListener.class, MailSenderStartupListener.class);
		BindUtils.multibind(binder(), ShutdownListener.class, MailSenderShutdownListener.class);
		log.info("Successfully configured {}", Logs.simpleClassName(this));
	}

	@Provides
	@Named("defaultFrom")
	public InternetAddress provideDefaultFrom(
			@Named("mail.defaultFrom") String from) {
		try {
			return new InternetAddress(from);
		} catch (AddressException e) {
			log.error("mail.defaultFrom cannot be parsed!");
		}
		return null;
	}

	@Provides
	@Named("defaultReplyTo")
	public InternetAddress provideDefaultReplyTo(
			@Named("mail.defaultReplyTo") String replyTo) {
		try {
			return new InternetAddress(replyTo);
		} catch (AddressException e) {
			log.error("mail.defaultReplyTo cannot be parsed!");
		}
		return null;
	}

	@Provides
	@Singleton
	public Session provideSession(@Named("mail.proto") String proto,
			@Named("mail.user") String user, @Named("mail.host") String host,
			@Named("mail.port") int port, @Named("mail.ssl") String ssl,
			@Named("mail.tls") String tls,
			@Named("defaultFrom") InternetAddress from) {
		boolean useAuth = !Strings.isNullOrEmpty(user);
		Properties props = new Properties();
		props.put("mail." + proto + ".auth", useAuth ? "true" : "false");
		props.put("mail." + proto + ".host", host);
		props.put("mail." + proto + ".port", port);
		props.put("mail." + proto + ".from", from.getAddress());
		if (ssl.equals("true"))
			props.put("mail." + proto + ".ssl.enable", "true");
		if (tls.equals("true"))
			props.put("mail." + proto + ".starttls.enable", "true");
		return Session.getInstance(props);
	}

	@Provides
	public Transport provideTransport(Session session,
			@Named("mail.proto") String proto, @Named("mail.user") String user,
			@Named("mail.pass") String pass) {
		boolean useAuth = !Strings.isNullOrEmpty(user);
		Transport transport = null;
		try {
			transport = session.getTransport(proto);
			if (useAuth)
				transport.connect(user, pass);
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

	private static class MailSenderStartupListener implements StartupListener {

		@Inject
		private EmailService emailService;

		@Inject
		private Provider<Transport> transportProvider;

		@Override
		public void startup() {
			try {
				log.info("Starting mail sender service");
				emailService.init(transportProvider);
				log.debug("Started mail sender service");
			} catch (Exception e) {
				log.warn("MailSenderService startup failed: {}", e.getMessage());
			}
		}
	}

	private static class MailSenderShutdownListener implements ShutdownListener {

		@Inject
		private EmailService emailService;

		@Override
		public void shutdown() {
			try {
				log.info("Shutting down mail sender service");
				emailService.close();
				log.debug("Shut down mail sender service");
			} catch (IOException e) {
				log.warn("MailSenderService shutdown failed: {}", e.getMessage());
			}
		}
	}

}
