package com.greendelta.collaboration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
