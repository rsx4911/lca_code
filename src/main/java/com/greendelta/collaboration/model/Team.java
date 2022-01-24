package com.greendelta.collaboration.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Team extends AbstractEntity {

	@Column
	public String teamname;

	@Column
	public String name;

	@Column
	@Lob
	public byte[] avatar;

	/**
	 * Don't add/remove users directly, use teamService.addMember/removeMember
	 * so memberships are also added/removed
	 */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable
	public final List<User> users = new ArrayList<>();

}
