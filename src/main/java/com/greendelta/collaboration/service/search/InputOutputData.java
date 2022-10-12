package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.model.FlowType;
import org.openlca.core.model.ProcessType;

public class InputOutputData {

	public String repositoryPath;
	public String refId;
	public String commitId;
	public final List<ProcessVersion> versions = new ArrayList<>();
	public final List<String> inputs = new ArrayList<>();
	public final List<String> outputs = new ArrayList<>();

	InputOutputData() {
	}

	InputOutputData(String repositoryPath, String commitId, String name, ProcessType type, FlowType flowType, String refId) {
		this.repositoryPath = repositoryPath;
		this.refId = refId;
		this.commitId = commitId;
		this.versions.add(new ProcessVersion(name, type, flowType, commitId));
	}

	static class ProcessVersion {

		public String name;
		public ProcessType processType;
		public FlowType flowType;
		public String commitId;

		private ProcessVersion() {
		}

		private ProcessVersion(String name, ProcessType processType, FlowType flowType, String commitId) {
			this.name = name;
			this.processType = processType;
			this.flowType = flowType;
			this.commitId = commitId;
		}

	}

}
