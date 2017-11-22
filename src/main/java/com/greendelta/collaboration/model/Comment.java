package com.greendelta.collaboration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "comments")
public class Comment extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "repository_path")
	public String repositoryPath;

	@Embedded
	public DatasetField field;

	@OneToOne
	@JoinColumn(name = "f_user")
	public User user;

	@Column(name = "date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;

	@Column(name = "text", length = 4000)
	public String text;

	@OneToOne
	@JoinColumn(name = "f_reply_to")
	public Comment replyTo;

	@Column(name = "restricted_to_role")
	@Enumerated(EnumType.STRING)
	public Role restrictedToRole;

	@Column(name = "released")
	public boolean released;

	@Column(name = "approved")
	public boolean approved;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
