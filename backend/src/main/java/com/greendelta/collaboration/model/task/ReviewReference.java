package com.greendelta.collaboration.model.task;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.AbstractEntity;
import com.greendelta.collaboration.model.User;

@Entity
@Table
public class ReviewReference extends AbstractEntity {

	@Column
	@Enumerated(EnumType.STRING)
	public ModelType type;

	@Column
	public String refId;

	@Column
	public String commitId;

	@OneToOne
	@JoinColumn
	public User reviewer;

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ReviewReference))
			return false;
		var ref = (ReviewReference) obj;
		if (!Strings.nullOrEqual(ref.refId, refId))
			return false;
		if (!Strings.nullOrEqual(ref.commitId, commitId))
			return false;
		return ref.type == type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type.name(), refId, commitId);
	}

}
