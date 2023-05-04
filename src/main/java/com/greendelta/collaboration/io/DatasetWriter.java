package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.openlca.git.model.Reference;

public interface DatasetWriter {

	File writeAll() throws IOException;
	File write(Collection<Reference> refs) throws IOException;
	
}
