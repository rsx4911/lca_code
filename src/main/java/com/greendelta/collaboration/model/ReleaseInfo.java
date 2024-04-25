package com.greendelta.collaboration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class ReleaseInfo extends AbstractEntity {

	@Column
	public String repositoryPath;
	
	@Column
	public String commitId;

	@Column
	public String label;

	@Column
	public String version;

	@Column(length = 4000)
	public String description;

	@Column(length = 4000)
	public String sourceInfo;

	@Column(length = 4000)
	public String contactInfo;

	@Column(length = 4000)
	public String projectInfo;

	@Column(length = 4000)
	public String projectFunding;

	@Column(length = 4000)
	public String appropriateUse;

	@Column(length = 4000)
	public String dqAssessment;

	@Column(length = 4000)
	public String citation;

	@Column(length = 4000)
	public String typeOfData;

}
