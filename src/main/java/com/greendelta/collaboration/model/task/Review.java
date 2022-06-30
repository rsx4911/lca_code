package com.greendelta.collaboration.model.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Review extends Task {

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn
	public List<ReviewReference> references = new ArrayList<>();

	@Override
	public TaskType getType() {
		return TaskType.REVIEW;
	}

}
