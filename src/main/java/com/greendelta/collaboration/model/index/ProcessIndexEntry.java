package com.greendelta.collaboration.model.index;

import java.util.Collection;
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
	public Nomenclature[] supportedNomenclatures = new Nomenclature[0];
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
	public String[] inputs = new String[0];
	public String[] outputs = new String[0];

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

		public static Nomenclature[] from(Map<String, Object> map) {
			if (map == null)
				return new Nomenclature[0];
			Object value = map.get("supportedNomenclatures");
			if (value instanceof Nomenclature[])
				return (Nomenclature[]) value;
			String[] values = null;
			if (value instanceof String[]) {
				values = (String[]) value;
			} else if (value instanceof Collection) {
				try {
					@SuppressWarnings("unchecked")
					Collection<String> collection = (Collection<String>) value;
					values = collection.toArray(new String[collection.size()]);
				} catch (Exception e) {
					LoggerFactory.getLogger(ProcessIndexEntry.class).warn("Could not parse supported nomenclatures", e);
				}
			}
			if (values == null)
				return new Nomenclature[0];
			Nomenclature[] result = new Nomenclature[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = Nomenclature.valueOf(values[i]);
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
			e.supportedNomenclatures = new Nomenclature[supportedNomenclatures.length];
			for (int i = 0; i < supportedNomenclatures.length; i++) {
				e.supportedNomenclatures[i] = supportedNomenclatures[i];
			}
		}
		e.inputs = new String[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			e.inputs[i] = inputs[i];
		}
		e.outputs = new String[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			e.outputs[i] = outputs[i];
		}
		return e;
	}
}
