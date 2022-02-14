package com.greendelta.collaboration.error;

import org.openlca.jsonld.SchemaVersion;

public class UnsupportedSchemaException extends RuntimeException {

	private static final long serialVersionUID = -2638066991601163780L;

	public UnsupportedSchemaException(SchemaVersion version) {
		super("Schema version " + (version != null ? version.value() : "null") + " is not compatible with "
				+ SchemaVersion.current().value());
	}

}
