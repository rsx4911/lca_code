package com.greendelta.collaboration.model.index;

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

	}

	public enum LicenseType {

		FREE, MIXED, CHARGED, UNKNOWN;

	}

	public enum ModellingPrinciple {

		ATTRIBUTIONAL, CONSEQUENTIAL, UNKNOWN;

	}

	public enum ModellingApproach {

		PHYSICAL, ECONOMIC, CAUSAL, NONE, UNKNOWN;

	}

	public enum Nomenclature {

		ILCD;

	}

	public enum ProcessType {

		UNIT, SYSTEM, UNKNOWN;

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
