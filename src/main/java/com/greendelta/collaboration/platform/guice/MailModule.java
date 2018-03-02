package com.greendelta.collaboration.platform.guice;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.cloud.util.Logs;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.greendelta.collaboration.platform.guice.util.BindUtils;
import com.greendelta.collaboration.platform.guice.util.ShutdownListener;
import com.greendelta.collaboration.platform.mail.EmailService;

class MailModule extends AbstractModule {

	private static final Logger log = LogManager.getLogger(MailModule.class);

	@Override
	protected void configure() {
		install(ThrowingProviderBinder.forModule(this));
		BindUtils.multibind(binder(), ShutdownListener.class, MailSenderShutdownListener.class);
		log.info("Successfully configured {}", Logs.simpleClassName(this));
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
