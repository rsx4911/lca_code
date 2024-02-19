package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;

import org.openlca.git.model.Entry;

public interface DatasetWriter {

	void write(Entry entry);
	
	File close() throws IOException;
	
	void withReferences();

}
