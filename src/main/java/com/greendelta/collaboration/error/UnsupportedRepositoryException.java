package com.greendelta.collaboration.error;

import org.openlca.git.RepositoryInfo;
import org.openlca.git.Compatibility.UnsupportedServerVersionException;

public class UnsupportedRepositoryException extends RuntimeException {

	private static final long serialVersionUID = -2638066991601163780L;

	public UnsupportedRepositoryException(UnsupportedServerVersionException e) {
		super("Repository server version " + e.version + " is not compatible with current version "
				+ RepositoryInfo.REPOSITORY_CURRENT_SERVER_VERSION);
	}

}
