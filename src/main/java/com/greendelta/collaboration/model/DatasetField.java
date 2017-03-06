package com.greendelta.collaboration.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.openlca.core.model.ModelType;

@Embeddable
public class DatasetField {

	@Column(name = "ds_type")
	@Enumerated(EnumType.STRING)
	public ModelType modelType;

	@Column(name = "ds_ref_id")
	public String refId;

	@Column(name = "ds_commit_id")
	public String commitId;

	@Column(name = "ds_field", length = 4000)
	public String field;

}
