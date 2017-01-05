package com.greendelta.collaboration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "memberships")
public class Membership extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "f_user")
	public User user;

	// this looks a bit intricately but makes access members, roles and
	// permissions for user more convenient (in opposite to have teams and users
	// as possible members directly)
	@OneToOne
	@JoinColumn(name = "f_team")
	public Team team;

	// can be a group or repository
	@Column(name = "member_of")
	public String memberOf;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	public Role role;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
