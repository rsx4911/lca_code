package com.greendelta.cloud.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "teams")
public class Team extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "teamname")
	public String teamname;

	@Column(name = "name")
	public String name;

	@Column(name = "avatar")
	public byte[] avatar;

	/**
	 * Don't add/remove users directly, use teamService.addMember/removeMember
	 * so memberships are also added/removed
	 */
	@OneToMany
	@JoinTable(name = "team_users", joinColumns = { @JoinColumn(name = "f_team") }, inverseJoinColumns = { @JoinColumn(name = "f_user") })
	public final List<User> users = new ArrayList<>();

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
