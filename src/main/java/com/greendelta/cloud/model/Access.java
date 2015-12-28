package com.greendelta.cloud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "access")
public class Access extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "f_user")
	public User user;

	@Column(name = "repository_group")
	public String group;
	
	@Column(name = "repository_name")
	public String repository;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
