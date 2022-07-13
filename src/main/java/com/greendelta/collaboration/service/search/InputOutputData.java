package com.greendelta.collaboration.service.search;

import java.util.ArrayList;
import java.util.List;

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

	InputOutputData(String repositoryPath, String commitId, String name, ProcessType type, String refId) {
		this.repositoryPath = repositoryPath;
		this.refId = refId;
		this.commitId = commitId;
		this.versions.add(new ProcessVersion(name, type, commitId));
	}

	void addCommitId(String commitId) {
		var last = versions.get(versions.size() - 1);
		versions.add(new ProcessVersion(last.name, last.processType, commitId));
	}

	static class ProcessVersion {

		public String name;
		public ProcessType processType;
		public String commitId;

		private ProcessVersion() {
		}

		private ProcessVersion(String name, ProcessType processType, String commitId) {
			this.name = name;
			this.processType = processType;
			this.commitId = commitId;
		}

	}

}
