package com.greendelta.collaboration.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
