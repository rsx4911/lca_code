package com.greendelta.collaboration.model.task;

import java.util.Date;

import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
