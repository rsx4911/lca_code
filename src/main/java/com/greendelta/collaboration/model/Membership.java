package com.greendelta.collaboration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Membership extends AbstractEntity {

	@OneToOne
	@JoinColumn
	public User user;

	// this looks a bit intricately but makes access members, roles and
	// permissions for user more convenient (in opposite to have teams and users
	// as possible members directly)
	@OneToOne
	@JoinColumn
	public Team team;

	// can be a group or repository
	@Column
	public String memberOf;

	@Column
	@Enumerated(EnumType.STRING)
	public Role role;

}
