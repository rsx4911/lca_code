package com.greendelta.collaboration.model.task;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review extends Task {

	@Override
	public TaskType getType() {
		return TaskType.REVIEW;
	}

}
