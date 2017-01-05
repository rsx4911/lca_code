package com.greendelta.collaboration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "messages")
public class Message extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "f_from_user")
	public User from;

	@OneToOne
	@JoinColumn(name = "f_to_user")
	public User to;

	@OneToOne
	@JoinColumn(name = "f_team")
	public Team team;

	@Column(name = "date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;

	@Column(name = "text", length = 4000)
	public String text;

	@Column(name = "read_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date read;

	@Column(name = "show_read_receipt")
	public boolean showReadReceipt;
	
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
