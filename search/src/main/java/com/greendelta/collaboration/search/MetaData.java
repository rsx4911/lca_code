package com.greendelta.collaboration.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.git.util.FieldDefinition;
import org.openlca.jsonld.Enums;
import org.openlca.jsonld.Json;
import org.openlca.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MetaData {

	private static final Logger log = LoggerFactory.getLogger(MetaData.class);

	static Map<String, Object> get(Reference r, OlcaRepository repo) {
		var ref = Maps.of(r);
		ref.remove("objectId");
		putDatasetInfo(ref, r, repo);
		return ref;
	}

	private static void putDatasetInfo(Map<String, Object> entry, Reference ref, OlcaRepository repo) {
		if (ref.isCategory) {
			entry.put("name", ref.path.substring(ref.path.lastIndexOf("/") + 1));
			return;
		}
		var defs = new ArrayList<FieldDefinition>();
		defs.add(FieldDefinition.firstOf("name"));
		defs.add(FieldDefinition.allOf("tags"));
		if (ref.type == ModelType.FLOW || ref.type == ModelType.PROCESS || ref.type == ModelType.EPD) {
			defs.add(FieldDefinition.firstOf("location.name"));
		}
		if (ref.type == ModelType.FLOW) {
			defs.add(FieldDefinition.firstOf("flowType", FlowType::valueOf));
		} else if (ref.type == ModelType.EPD) {
			defs.add(FieldDefinition.firstOf("validFrom", MetaData::getYear));
			defs.add(FieldDefinition.firstOf("validUntil", MetaData::getYear));
		} else if (ref.type == ModelType.PROCESS) {
			defs.add(FieldDefinition.firstOf("location.name"));
			defs.add(FieldDefinition.firstOf("processType", ProcessType::valueOf));
			defs.add(FieldDefinition.firstOf("exchanges.flow.flowType", FlowType::valueOf)
					.ifIs("exchanges.isQuantitativeReference")
					.name("flowType"));
			defs.add(FieldDefinition.firstOf("processDocumentation.dataSetOwner.name"));
			defs.add(FieldDefinition.firstOf("processDocumentation.validFrom", MetaData::getYear));
			defs.add(FieldDefinition.firstOf("processDocumentation.validUntil", MetaData::getYear));
			defs.add(FieldDefinition.firstOf("defaultAllocationMethod", MetaData::getModellingApproach));
			defs.add(FieldDefinition.allOf("processDocumentation.reviews.reviewType"));
			defs.add(FieldDefinition.allOf("processDocumentation.complianceDeclarations.system.name"));
			defs.add(FieldDefinition.allOf("processDocumentation.flowCompleteness.aspect")
					.ifHas("processDocumentation.flowCompleteness.value"));
			defs.add(FieldDefinition.allOf("processDocumentation.flowCompleteness.value")
					.ifHas("processDocumentation.flowCompleteness.aspect"));
		}
		var info = repo.datasets.parse(ref, defs);
		var location = info.get("location.name");
		if (location == null || location.toString().isEmpty()) {
			entry.put("name", info.get("name"));
		} else {
			entry.put("name", info.get("name") + " - " + location);
			entry.put("location", location);
		}
		if (ref.type == ModelType.FLOW) {
			entry.put("flowType", info.get("flowType"));
		} else if (ref.type == ModelType.PROCESS) {
			entry.put("processType", info.get("processType"));
			entry.put("flowType", info.get("flowType"));
		}
		entry.put("tags", info.get("tags"));
		if (ref.type == ModelType.EPD) {
			entry.put("validFromYear", info.get("validFrom"));
			entry.put("validUntilYear", info.get("validUntil"));
		} else if (ref.type == ModelType.PROCESS) {
			entry.put("contact", info.get("processDocumentation.dataSetOwner.name"));
			entry.put("validFromYear", info.get("processDocumentation.validFrom"));
			entry.put("validUntilYear", info.get("processDocumentation.validUntil"));
			entry.put("modellingApproach", info.get("defaultAllocationMethod"));
			var reviewTypes = Maps.getAll(info, "processDocumentation.reviews.reviewType", String.class).stream()
					.map(type -> Strings.nullOrEmpty(type) ? "unspecified" : type)
					.collect(Collectors.toSet());
			if (reviewTypes.isEmpty()) {
				reviewTypes.add("unreviewed");
			}
			entry.put("reviewTypes", reviewTypes);
			entry.put("complianceDeclarations",
					info.get("processDocumentation.complianceDeclarations.system.name"));
			var flowCompleteness = new HashSet<String>();
			var aspects = Maps.getAll(info, "processDocumentation.flowCompleteness.aspect", String.class);
			var values = Maps.getAll(info, "processDocumentation.flowCompleteness.value", String.class);
			if (aspects.size() != values.size()) {
				log.warn("Aspect count doesnt match value count");
				return;
			}
			for (var i = 0; i < aspects.size(); i++) {
				flowCompleteness.add(values.get(i));
				flowCompleteness.add(values.get(i) + "/" + aspects.get(i));
			}
			entry.put("flowCompleteness", flowCompleteness);
		}
	}

	private static Integer getYear(String date) {
		if (date == null || date.isEmpty())
			return null;
		var d = Json.parseDate(date);
		var time = d == null ? 0 : d.getTime();
		if (time == 0l)
			return null;
		var cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.YEAR);
	}

	private static ModellingApproach getModellingApproach(String value) {
		if (value == null)
			return ModellingApproach.UNKNOWN;
		if (value.equals(Enums.getLabel(AllocationMethod.PHYSICAL)))
			return ModellingApproach.PHYSICAL;
		if (value.equals(Enums.getLabel(AllocationMethod.ECONOMIC)))
			return ModellingApproach.ECONOMIC;
		if (value.equals(Enums.getLabel(AllocationMethod.CAUSAL)))
			return ModellingApproach.CAUSAL;
		if (value.equals(Enums.getLabel(AllocationMethod.NONE)))
			return ModellingApproach.NONE;
		return ModellingApproach.UNKNOWN;
	}

}
