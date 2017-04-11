package com.greendelta.collaboration.model.task;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

@Entity
@Table(name = "task_assignments")
public class TaskAssignment extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date endDate;

	@OneToOne
	@JoinColumn(name = "f_assigned_to")
	public User assignedTo;

	@Column(name = "iteration")
	public long iteration;

	@Column(name = "canceled")
	public boolean canceled;

	@OneToOne
	@JoinColumn(name = "f_ended_by")
	public User endedBy;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
