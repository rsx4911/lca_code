package com.greendelta.collaboration.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.greendelta.collaboration.model.Permission.*;

public enum Role {

	NONE(1),

	READER(2, READ),

	CONTRIBUTOR(3, READ, WRITE),

	REVIEWER(4, READ, WRITE, COMMENT, REVIEW),

	OWNER(5, READ, WRITE, COMMENT, REVIEW, MOVE, DELETE, EDIT_MEMBERS, MANAGE_COMMENTS, MANAGE_TASK, SET_PUBLIC);

	private List<Permission> permissions;
	private int level;

	private Role(int level, Permission... permissions) {
		this.level = level;
		this.permissions = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(permissions)));
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public static Role best(Role r1, Role r2) {
		if (r1.level > r2.level)
			return r1;
		return r2;
	}

}
