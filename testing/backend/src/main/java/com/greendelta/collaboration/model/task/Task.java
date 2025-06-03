package com.greendelta.collaboration.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(name = "REVIEW", value = Review.class) })
@MappedSuperclass
public abstract class Task extends AbstractEntity {

	@Column
	public String name;

	@Column
	public String repositoryPath;

	@Column(length = 4000)
	public String comment;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date startDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date endDate;

	@Column
	@Enumerated(EnumType.STRING)
	public TaskState state;

	@OneToOne
	@JoinColumn
	public User initiator;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn
	public List<TaskAssignment> assignments = new ArrayList<>();

	public abstract TaskType getType();

}
