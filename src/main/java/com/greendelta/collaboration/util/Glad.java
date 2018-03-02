package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Glad {

	static final List<String> GLAD_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "processType", "supportedNomenclatures", "modellingPrinciple", "modellingApproach",
			"aggregationType", "licenseType", "name", "category", "location", "completeness",
			"sampleRepresentativeness", "samplingProcedure", "technology", "representativeness", "biogenicCarbon",
			"reviewer", "copyrightHolder", "license", "contact", "description", "dataSetUrl", "format", "validFrom",
			"validUntil", "reviewed", "copyrightProtected"));

	public static Map<String, Object> cleanUp(Map<String, Object> data) {
		ObjectMap map = ObjectMap.fromMap(data);
		map.removeAllBut(GLAD_FIELDS.toArray(new String[GLAD_FIELDS.size()]));
		return map;
	}

}
