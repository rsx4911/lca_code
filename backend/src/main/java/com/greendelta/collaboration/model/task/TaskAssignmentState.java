package com.greendelta.collaboration.model.task;

public enum TaskAssignmentState {

	CREATED,

	// Task was assigned to users and not all of them completed it yet
	PROCESSING,

	// All users completed the task, initiator has to verify the result
	VERIFYING,

	CANCELED,

	COMPLETED;

}
