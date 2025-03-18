package com.greendelta.collaboration.model;

import java.io.IOException;
import java.util.ArrayList;
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

	@Column(length = 65535)
	public String description;

	@Column(length = 65535)
	public String sourceInfo;

	@Column(length = 65535)
	public String contactInfo;
	
	@Column(length = 65535)
	public String changeLog;

	@Column(length = 65535)
	public String projectInfo;

	@Column(length = 65535)
	public String projectFunding;

	@Column(length = 65535)
	public String appropriateUse;

	@Column(length = 65535)
	public String dqAssessment;

	@Column(length = 65535)
	public String citation;

	@Column(length = 64)
	public String typeOfData;

	public List<String> getTags() {
		if (Strings.nullOrEmpty(tags))
			return new ArrayList<>();
		try {
			return new ObjectMapper().readValue(tags, JacksonTypes.STRING_LIST);
		} catch (IOException e) {
			return new ArrayList<>();
		}
	}

}
