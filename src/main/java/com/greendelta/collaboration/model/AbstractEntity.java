package com.greendelta.collaboration.model;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonAnySetter;

@MappedSuperclass
public abstract class AbstractEntity {

	public abstract long getId();

	public abstract void setId(long id);

	public boolean hasId() {
		return getId() > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(this.getClass().isInstance(obj)))
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		return this.getId() != 0 && this.getId() == other.getId();
	}

	@Override
	public int hashCode() {
		if (getId() != 0) {
			// from Long class's hash method, to avoid new Long(id)
			return (int) (getId() ^ (getId() >>> 32));
		} else {
			return super.hashCode();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getId() + ")";
	}

	@JsonAnySetter
	public void handleUnknown(String name, Object value) {
		// do nothing
	}

}
