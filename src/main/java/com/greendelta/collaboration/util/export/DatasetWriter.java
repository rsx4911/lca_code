package com.greendelta.collaboration.util.export;

import java.io.File;
import java.io.IOException;

import org.openlca.core.model.ModelType;

public interface DatasetWriter {

	void write(ModelType type, String refId) throws IOException;

	File close() throws IOException;

}
