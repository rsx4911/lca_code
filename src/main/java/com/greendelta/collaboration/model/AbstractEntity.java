package com.greendelta.collaboration.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonAnySetter;

@MappedSuperclass
public abstract class AbstractEntity {

	@Id
	@Column(name = "id")
	public long id;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(this.getClass().isInstance(obj)))
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		return this.id != 0 && this.id == other.id;
	}

	@Override
	public int hashCode() {
		if (id != 0) {
			// from Long class's hash method, to avoid new Long(id)
			return (int) (id ^ (id >>> 32));
		} else {
			return super.hashCode();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ")";
	}

	@JsonAnySetter
	public void handleUnknown(String name, Object value) {
		// do nothing
	}

}
