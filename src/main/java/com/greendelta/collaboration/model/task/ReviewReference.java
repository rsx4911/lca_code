package com.greendelta.collaboration.model.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

@Entity
@Table(name = "review_references")
public class ReviewReference extends AbstractEntity {

	@Id
	@Column(name = "id")
	private long id;

	@Column(name = "ds_type")
	@Enumerated(EnumType.STRING)
	public ModelType type;

	@Column(name = "ds_ref_id")
	public String refId;

	@Column(name = "ds_commit_id")
	public String commitId;

	@Column(name = "ds_name")
	public String name;

	@OneToOne
	@JoinColumn(name = "f_reviewer")
	public User reviewer;
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ReviewReference))
			return false;
		ReviewReference ref = (ReviewReference) obj;
		if (!Strings.nullOrEqual(ref.refId, refId))
			return false;
		return ref.type == type;
	}

	@Override
	public int hashCode() {
		return (type.name() + refId).hashCode();
	}

}
