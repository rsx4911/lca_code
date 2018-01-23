package com.greendelta.collaboration.model.index;

import org.openlca.core.model.FlowType;

public class FlowIndexEntry extends IndexEntry {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2343644252925342419L;
	public FlowType flowType = FlowType.ELEMENTARY_FLOW;
	

	@Override
	public IndexEntry clone() {
		FlowIndexEntry e = new FlowIndexEntry();
		fillIndexEntryInfo(e);
		e.flowType = flowType;
		return e;
	}
}
