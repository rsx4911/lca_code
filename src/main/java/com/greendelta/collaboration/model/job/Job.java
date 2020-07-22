package com.greendelta.collaboration.model.job;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.greendelta.collaboration.model.AbstractEntity;

@Entity
@Table(name = "jobs")
public class Job extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;
	
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	public JobType type;
	
	@Column(name = "token")
	public String token;

	@Column(name = "data")
	public String data;
	
	@Column(name = "valid_until")
	@Temporal(TemporalType.DATE)
	public Date validUntil;

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}
	
}
