package com.greendelta.collaboration.platform.mail;

import javax.mail.Transport;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Provider;

class TransportObjectFactory implements PoolableObjectFactory<TransportHolder> {

	private static final Logger log = LogManager.getLogger(TransportObjectFactory.class);
	private EmailService emailService;
	private Provider<Transport> transportProvider;

	TransportObjectFactory(EmailService emailService,
			Provider<Transport> transportProvider) {
		this.emailService = emailService;
		this.transportProvider = transportProvider;
	}

	@Override
	public TransportHolder makeObject() throws Exception {
		TransportHolder object = new TransportHolder();
		object.setTransport(transportProvider.get());
		object.setOpeningTime(System.currentTimeMillis());
		return object;
	}

	@Override
	public void destroyObject(TransportHolder object) throws Exception {
		if (object == null || object.getTransport() == null)
			return;
		if (!object.getTransport().isConnected())
			return;
		object.getTransport().close();
	}

	@Override
	public boolean validateObject(TransportHolder obj) {
		if (obj == null)
			return false;
		if (obj.getMailsSent() >= this.emailService.getMailsSentPerConnection()) {
			log.debug("Expiring an SMTP connection due to reaching send message count of {} messages: {}",
					this.emailService.getMailsSentPerConnection(), obj);
			return false;
		}
		if (obj.getTransport() == null)
			return false;
		if (!obj.getTransport().isConnected()) {
			log.debug("Transport already deconnected: {}", obj);
			return false;
		}
		long alive = System.currentTimeMillis() - obj.getOpeningTime();
		if (alive < this.emailService.getConnectionLifetimeMs())
			return true;
		log.debug("Expiring an SMTP connection due to exceeded lifetime, life was {} ms: {}", alive, obj);
		return false;
	}

	@Override
	public void activateObject(TransportHolder obj) throws Exception {
	}

	@Override
	public void passivateObject(TransportHolder obj) throws Exception {
	}
}