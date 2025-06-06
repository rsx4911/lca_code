package com.greendelta.collaboration.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table
public class Message extends AbstractEntity {

	@OneToOne
	@JoinColumn
	public User from;

	@OneToOne
	@JoinColumn
	public User to;

	@OneToOne
	@JoinColumn
	public Team team;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;

	@Column(length = 4000)
	public String text;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date readDate;

	@Column
	public boolean showReadReceipt;

}
