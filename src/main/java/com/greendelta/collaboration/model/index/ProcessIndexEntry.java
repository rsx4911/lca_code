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

}
