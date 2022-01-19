package com.greendelta.collaboration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
