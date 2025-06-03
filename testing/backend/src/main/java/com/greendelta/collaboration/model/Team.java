package com.greendelta.collaboration.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@Column
	@Lob
	@JsonIgnore
	public Blob avatar;

	/**
	 * Don't add/remove users directly, use teamService.addMember/removeMember
	 * so memberships are also added/removed
	 */
	@ManyToMany
	@JoinTable
	public final List<User> users = new ArrayList<>();

}
