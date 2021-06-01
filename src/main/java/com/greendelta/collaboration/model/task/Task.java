package com.greendelta.collaboration.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(name = "REVIEW", value = Review.class) })
@MappedSuperclass
public abstract class Task extends AbstractEntity {

	@Column(name = "name")
	public String name;

	@Column(name = "repository_path")
	public String repositoryPath;

	@Column(name = "comment", length = 4000)
	public String comment;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date endDate;

	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	public TaskState state;

	@OneToOne
	@JoinColumn(name = "f_initiator")
	public User initiator;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "f_task")
	public List<TaskAssignment> assignments = new ArrayList<>();

	public abstract TaskType getType();

}
