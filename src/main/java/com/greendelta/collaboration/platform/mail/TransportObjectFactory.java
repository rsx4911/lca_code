package com.greendelta.collaboration.platform.mail;

import javax.mail.Transport;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TransportObjectFactory implements PoolableObjectFactory<TransportHolder> {

	private static final Logger log = LogManager.getLogger(TransportObjectFactory.class);
	private final int mailsSentPerConnection = 10;
	private final int connectionLifetimeMs = 60000;
	private final EmailService emailService;

	TransportObjectFactory(EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	public TransportHolder makeObject() throws Exception {
		Transport transport = emailService.getTransport();
		if (transport == null)
			return null;
		TransportHolder object = new TransportHolder();
		object.setTransport(transport);
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
		if (obj.getMailsSent() >= mailsSentPerConnection) {
			log.debug("Expiring an SMTP connection due to reaching send message count of {} messages: {}",
					mailsSentPerConnection, obj);
			return false;
		}
		if (obj.getTransport() == null)
			return false;
		if (!obj.getTransport().isConnected()) {
			log.debug("Transport already deconnected: {}", obj);
			return false;
		}
		long alive = System.currentTimeMillis() - obj.getOpeningTime();
		if (alive < connectionLifetimeMs)
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