package com.greendelta.collaboration.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table
public class RestrictionSet extends AbstractEntity {

	@Column
	public String name;

	@Column
	@Lob
	private String refIds;

	public List<String> getRefIds() {
		return Arrays.asList(refIds.split(";"));
	}

	public void setRefIds(List<String> refIds) {
		this.refIds = String.join(";", refIds);
	}

}
