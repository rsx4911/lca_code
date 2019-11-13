package com.greendelta.collaboration.model.index;

import org.openlca.core.model.ProcessType;

import com.greendelta.collaboration.model.glad.ModellingApproach;

public class ProcessIndexEntry extends IndexEntry {

	private static final long serialVersionUID = 2145646252325642438L;
	public ProcessType processType = ProcessType.UNIT_PROCESS;
	public Integer validFromYear;
	public Integer validUntilYear;
	public String location;
	public ModellingApproach modellingApproach = ModellingApproach.UNKNOWN;
	public String contact;
	
	@Override
	public IndexEntry clone() {
		ProcessIndexEntry e = new ProcessIndexEntry();
		fillIndexEntryInfo(e);
		e.processType = processType;
		e.validFromYear = validFromYear;
		e.validUntilYear = validUntilYear;
		e.location= location;
		e.modellingApproach = modellingApproach;
		e.contact = contact;
		return e;
	}
}
