package com.greendelta.collaboration.model;

import org.openlca.core.model.ModelType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class DatasetField {

	@Column
	@Enumerated(EnumType.STRING)
	public ModelType modelType;

	@Column
	public String refId;

	@Column
	public String commitId;

	@Column(length = 4000)
	public String path;

}
