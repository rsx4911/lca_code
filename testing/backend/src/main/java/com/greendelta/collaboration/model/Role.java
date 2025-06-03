package com.greendelta.collaboration.model;

import static com.greendelta.collaboration.model.Permission.CREATE_CHANGE_LOG;
import static com.greendelta.collaboration.model.Permission.MANAGE_RELEASES;
import static com.greendelta.collaboration.model.Permission.COMMENT;
import static com.greendelta.collaboration.model.Permission.CREATE;
import static com.greendelta.collaboration.model.Permission.DELETE;
import static com.greendelta.collaboration.model.Permission.EDIT_MEMBERS;
import static com.greendelta.collaboration.model.Permission.MANAGE_COMMENTS;
import static com.greendelta.collaboration.model.Permission.MANAGE_TASK;
import static com.greendelta.collaboration.model.Permission.MOVE;
import static com.greendelta.collaboration.model.Permission.READ;
import static com.greendelta.collaboration.model.Permission.REVIEW;
import static com.greendelta.collaboration.model.Permission.SET_SETTINGS;
import static com.greendelta.collaboration.model.Permission.WRITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Role {

	NONE(1),

	READER(2, READ),

	CONTRIBUTOR(3, READ, WRITE),

	REVIEWER(4, READ, WRITE, COMMENT, REVIEW),

	EDITOR(5, READ, WRITE, COMMENT, REVIEW, MANAGE_COMMENTS, MANAGE_TASK, SET_SETTINGS),
	
	RELEASE_MANAGER(6, READ, WRITE, COMMENT, REVIEW, MANAGE_COMMENTS, MANAGE_TASK, SET_SETTINGS, MANAGE_RELEASES),

	OWNER(7, READ, WRITE, COMMENT, REVIEW, MANAGE_COMMENTS, MANAGE_TASK, SET_SETTINGS, MANAGE_RELEASES, CREATE, EDIT_MEMBERS, MOVE, CREATE_CHANGE_LOG, DELETE);

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
