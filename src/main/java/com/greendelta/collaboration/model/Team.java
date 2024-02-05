package com.greendelta.collaboration.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table
public class Team extends AbstractEntity {

	@Column
	public String teamname;

	@Column
	public String name;

	@Column(columnDefinition = "LONGBLOB")
	@Lob
	public byte[] avatar;

	/**
	 * Don't add/remove users directly, use teamService.addMember/removeMember
	 * so memberships are also added/removed
	 */
	@ManyToMany
	@JoinTable
	public final List<User> users = new ArrayList<>();

}
