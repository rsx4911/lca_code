package com.greendelta.collaboration.model;

import org.openlca.core.model.ModelType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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
