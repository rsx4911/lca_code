package com.greendelta.collaboration.model;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table
public class RestrictionSet extends AbstractEntity {

	@Column
	public String name;

	@Column(columnDefinition = "LONGTEXT")
	@Lob
	private String refIds;

	public List<String> getRefIds() {
		return Arrays.asList(refIds.split(";"));
	}

	public void setRefIds(List<String> refIds) {
		this.refIds = String.join(";", refIds);
	}

}
