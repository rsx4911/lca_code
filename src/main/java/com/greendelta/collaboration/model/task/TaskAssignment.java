package com.greendelta.collaboration.model.task;

import java.util.Date;

import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table
public class TaskAssignment extends AbstractEntity {

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date startDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date endDate;

	@OneToOne
	@JoinColumn
	public User assignedTo;

	@Column
	public long iteration;

	@Column
	public boolean canceled;

	@OneToOne
	@JoinColumn
	public User endedBy;

}
