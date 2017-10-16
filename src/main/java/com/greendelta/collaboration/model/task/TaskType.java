package com.greendelta.collaboration.model.task;

public enum TaskType {

	REVIEW(Review.class);

	public final Class<? extends Task> subclass;

	private TaskType(Class<? extends Task> subclass) {
		this.subclass = subclass;
	}

}
