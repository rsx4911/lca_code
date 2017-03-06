package com.greendelta.collaboration.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.greendelta.collaboration.model.Permission.*;

public enum Role {

	NONE(1),

	READER(2, READ),
	
	REVIEWER(3, READ, COMMENT),

	CONTRIBUTOR(4, READ, COMMENT, WRITE),

	OWNER(5, READ, COMMENT, WRITE, MOVE, DELETE, EDIT_MEMBERS);

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
