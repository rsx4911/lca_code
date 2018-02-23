package com.greendelta.collaboration.model.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

public class ProcessIndexEntry extends IndexEntry {

	private static final long serialVersionUID = 2145646252325642438L;
	public ProcessType processType = ProcessType.UNKNOWN;
	public String completeness;
	public String sampleRepresentativeness;
	public String samplingProcedure;
	public long validFrom;
	public long validUntil;
	public String location;
	public String technology;
	public List<Nomenclature> supportedNomenclatures;
	public String representativeness;
	public ModellingPrinciple modellingPrinciple = ModellingPrinciple.UNKNOWN;
	public ModellingApproach modellingApproach = ModellingApproach.UNKNOWN;
	public String biogenicCarbon;
	public boolean reviewed;
	public String reviewer;
	public AggregationType aggregationType = AggregationType.UNKNOWN;
	public boolean copyrightProtected;
	public String copyrightHolder;
	public LicenseType licenseType = LicenseType.UNKNOWN;
	public String license;
	public String contact;
	public String description;
	public List<String> inputs;
	public List<String> outputs;

	public enum AggregationType {

		HORIZONTAL, VERTICAL, NONE, UNKNOWN;

		public static AggregationType from(Map<String, Object> map) {
			if (map == null)
				return null;
			Object value = map.get("aggregationType");
			if (value == null)
				return null;
			if (value instanceof AggregationType)
				return (AggregationType) value;
			String sValue = value.toString();
			if (sValue.isEmpty())
				return null;
			return valueOf(sValue.toUpperCase());
		}

	}

	public enum LicenseType {

		FREE, MIXED, CHARGED, UNKNOWN;

		public static LicenseType from(Map<String, Object> map) {
			if (map == null)
				return null;
			Object value = map.get("licenseType");
			if (value == null)
				return null;
			if (value instanceof LicenseType)
				return (LicenseType) value;
			String sValue = value.toString();
			if (sValue.isEmpty())
				return null;
			return valueOf(sValue.toUpperCase());
		}

	}

	public enum ModellingPrinciple {

		ATTRIBUTIONAL, CONSEQUENTIAL, UNKNOWN;

		public static ModellingPrinciple from(Map<String, Object> map) {
			if (map == null)
				return null;
			Object value = map.get("modellingPrinciple");
			if (value == null)
				return null;
			if (value instanceof ModellingPrinciple)
				return (ModellingPrinciple) value;
			String sValue = value.toString();
			if (sValue.isEmpty())
				return null;
			return valueOf(sValue.toUpperCase());
		}

	}

	public enum ModellingApproach {

		PHYSICAL, ECONOMIC, CAUSAL, NONE, UNKNOWN;

		public static ModellingApproach from(Map<String, Object> map) {
			if (map == null)
				return null;
			Object value = map.get("modellingApproach");
			if (value == null)
				return null;
			if (value instanceof ModellingApproach)
				return (ModellingApproach) value;
			String sValue = value.toString();
			if (sValue.isEmpty())
				return null;
			return valueOf(sValue.toUpperCase());
		}

	}

	public enum Nomenclature {

		ILCD;

		@SuppressWarnings("unchecked")
		public static List<Nomenclature> from(Map<String, Object> map) {
			if (map == null)
				return null;
			Object value = map.get("supportedNomenclatures");
			if (value instanceof Nomenclature[])
				return Arrays.asList((Nomenclature[]) value);
			if (value instanceof Collection)
				return new ArrayList<>((Collection<Nomenclature>) value);
			List<String> values = new ArrayList<>();
			if (value instanceof String[]) {
				values = Arrays.asList((String[]) value);
			} else if (value instanceof Collection) {
				try {
					values = new ArrayList<>((Collection<String>) value);
				} catch (Exception e) {
					LoggerFactory.getLogger(ProcessIndexEntry.class).warn("Could not parse supported nomenclatures", e);
				}
			}
			if (values == null || values.isEmpty())
				return null;
			List<Nomenclature> result = new ArrayList<>();
			for (int i = 0; i < values.size(); i++) {
				result.add(Nomenclature.valueOf(values.get(i)));
			}
			return result;
		}

	}

	public enum ProcessType {

		UNIT, SYSTEM, UNKNOWN;

		public static ProcessType from(Map<String, Object> map) {
			if (map == null)
				return null;
			Object value = map.get("processType");
			if (value == null)
				return null;
			if (value instanceof ProcessType)
				return (ProcessType) value;
			String sValue = value.toString();
			if (sValue.isEmpty())
				return null;
			return valueOf(sValue.toUpperCase());
		}

	}

	@Override
	public IndexEntry clone() {
		ProcessIndexEntry e = new ProcessIndexEntry();
		fillIndexEntryInfo(e);
		e.processType = processType;
		e.completeness = completeness;
		e.sampleRepresentativeness = sampleRepresentativeness;
		e.samplingProcedure = samplingProcedure;
		e.validFrom = validFrom;
		e.validUntil = validUntil;
		e.location = location;
		e.technology = technology;
		e.representativeness = representativeness;
		e.modellingPrinciple = modellingPrinciple;
		e.modellingApproach = modellingApproach;
		e.biogenicCarbon = biogenicCarbon;
		e.reviewed = reviewed;
		e.reviewer = reviewer;
		e.aggregationType = aggregationType;
		e.copyrightProtected = copyrightProtected;
		e.copyrightHolder = copyrightHolder;
		e.licenseType = licenseType;
		e.license = license;
		e.contact = contact;
		e.description = description;
		if (supportedNomenclatures != null) {
			e.supportedNomenclatures = new ArrayList<>(supportedNomenclatures);
		}
		if (inputs != null) {
			e.inputs = new ArrayList<>(inputs);
		}
		if (outputs != null) {
			e.outputs = new ArrayList<>(outputs);
		}
		return e;
	}
}
