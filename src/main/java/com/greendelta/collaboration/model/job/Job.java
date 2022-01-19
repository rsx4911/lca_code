package com.greendelta.collaboration.model.job;

import java.util.Date;

import com.greendelta.collaboration.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table
public class Job extends AbstractEntity {

	@Column
	@Enumerated(EnumType.STRING)
	public JobType type;

	@Column
	public String token;

	@Column
	public String data;

	@Column
	@Temporal(TemporalType.DATE)
	public Date validUntil;

}
