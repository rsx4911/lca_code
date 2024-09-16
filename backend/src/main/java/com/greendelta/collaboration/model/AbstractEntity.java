package com.greendelta.collaboration.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {

	@Id
	@Column
	public long id;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(this.getClass().isInstance(obj)))
			return false;
		var other = (AbstractEntity) obj;
		return this.id != 0 && this.id == other.id;
	}

	@Override
	public int hashCode() {
		if (id == 0)
			return super.hashCode();
		// from Long class's hash method, to avoid new Long(id)
		return (int) (id ^ (id >>> 32));
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
