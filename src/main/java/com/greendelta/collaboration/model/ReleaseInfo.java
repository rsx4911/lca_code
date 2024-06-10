package com.greendelta.collaboration.model;

import java.io.IOException;
import java.util.List;

import org.openlca.util.Strings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greendelta.collaboration.util.JacksonTypes;

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

	@Column
	private String tags;

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

	public List<String> getTags() {
		if (Strings.nullOrEmpty(tags))
			return null;
		try {
			return new ObjectMapper().readValue(tags, JacksonTypes.STRING_LIST);
		} catch (IOException e) {
			return null;
		}
	}

}
