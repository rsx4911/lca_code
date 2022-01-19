package com.greendelta.collaboration.model.task;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Review extends Task {

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn
	public Set<ReviewReference> references = new HashSet<>();

	@Override
	public TaskType getType() {
		return TaskType.REVIEW;
	}

}
