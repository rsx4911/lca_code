package com.greendelta.collaboration.model.job;

import java.util.Date;

import com.greendelta.collaboration.model.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
