package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;

import org.openlca.git.model.Reference;

public interface DatasetWriter {

	void write(Reference entry);
	
	File close() throws IOException;
	
	void withReferences();

}
