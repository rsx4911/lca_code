package com.greendelta.collaboration.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table
public class InputOutputData extends AbstractEntity {

	@Column
	public String repositoryPath;

	@Column
	public String commitId;

	@Lob
	private String inputsJson;
	@Transient
	public Map<String, Set<String>> inputs = new HashMap<>();

	@Lob
	private String outputsJson;
	@Transient
	public Map<String, Set<String>> outputs = new HashMap<>();

	@Lob
	private String descriptorsJson;
	@Transient
	public Map<String, ProcessDescriptor> descriptors = new HashMap<>();

	@PrePersist
	@PreUpdate
	private void prePersist() {
		inputsJson = write(inputs);
		outputsJson = write(outputs);
		descriptorsJson = write(descriptors);
	}

	@PostLoad
	private void postLoad() {
		inputs = read(inputsJson, new TypeReference<Map<String, Set<String>>>() {
		}, new HashMap<>());
		outputs = read(outputsJson, new TypeReference<Map<String, Set<String>>>() {
		}, new HashMap<>());
		descriptors = read(descriptorsJson, new TypeReference<Map<String, ProcessDescriptor>>() {
		}, new HashMap<>());
	}

	public Set<ProcessDescriptor> consumers(String flowRefId) {
		return exchanges(flowRefId, inputs);
	}

	public Set<ProcessDescriptor> producers(String flowRefId) {
		return exchanges(flowRefId, outputs);
	}

	private Set<ProcessDescriptor> exchanges(String refId, Map<String, Set<String>> exchanges) {
		return exchanges.keySet().stream()
				.filter(process -> exchanges.get(process).contains(refId))
				.map(descriptors::get)
				.collect(Collectors.toSet());
	}

	private static <T> T read(String value, TypeReference<T> type, T defaultValue) {
		if (value == null || value.isEmpty())
			return defaultValue;
		try {
			return new ObjectMapper().readValue(value, type);
		} catch (JsonProcessingException e) {
			return defaultValue;
		}
	}

	private static String write(Object value) {
		if (value == null)
			return null;
		try {
			return new ObjectMapper().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static class ProcessDescriptor {

		public ModelType type;
		public String refId;
		public String name;
		public ProcessType processType;

		public ProcessDescriptor() {
		}

		public ProcessDescriptor(String refId, String name, ProcessType processType) {
			this.type = ModelType.PROCESS;
			this.refId = refId;
			this.name = name;
			this.processType = processType;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof ProcessDescriptor))
				return false;
			return ((ProcessDescriptor) obj).refId.equals(refId);
		}

		@Override
		public int hashCode() {
			return refId.hashCode();
		}

	}

}
