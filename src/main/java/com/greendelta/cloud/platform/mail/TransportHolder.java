package com.greendelta.cloud.platform.mail;

import javax.mail.Transport;

class TransportHolder {

	private Transport transport;
	private long openingTime;
	private volatile int mailsSent = 0;

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public long getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(long openingTime) {
		this.openingTime = openingTime;
	}

	public int getMailsSent() {
		return mailsSent;
	}

	public int incrementMailsSent() {
		mailsSent += 1;
		return mailsSent;
	}

	@Override
	public String toString() {
		return String.format("%s[age=%f,sent=%d,transport=%s]", super.toString(),
				(System.currentTimeMillis() - getOpeningTime()) / 1000.0, getMailsSent(), getTransport().toString());
	}
}
