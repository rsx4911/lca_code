package com.greendelta.collaboration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table
public class Comment extends AbstractEntity {

	@Column
	public String repositoryPath;

	@Embedded
	public DatasetField field;

	@OneToOne
	@JoinColumn
	public User user;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;

	@Column(length = 4000)
	public String text;

	@OneToOne
	@JoinColumn
	public Comment replyTo;

	@Column
	@Enumerated(EnumType.STRING)
	public Role restrictedToRole;

	@Column
	public boolean released;

	@Column
	public boolean approved;

}
