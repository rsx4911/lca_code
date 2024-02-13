package com.greendelta.collaboration.model.task;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
