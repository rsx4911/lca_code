package com.greendelta.collaboration.webservice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.openlca.core.database.ActorDao;
import org.openlca.core.database.CategoryDao;
import org.openlca.core.database.CurrencyDao;
import org.openlca.core.database.DQSystemDao;
import org.openlca.core.database.FileStore;
import org.openlca.core.database.FlowDao;
import org.openlca.core.database.FlowPropertyDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.database.LocationDao;
import org.openlca.core.database.ParameterDao;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.database.ProjectDao;
import org.openlca.core.database.SocialIndicatorDao;
import org.openlca.core.database.SourceDao;
import org.openlca.core.database.UnitGroupDao;
import org.openlca.core.model.Actor;
import org.openlca.core.model.AllocationFactor;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.CategorizedEntity;
import org.openlca.core.model.Category;
import org.openlca.core.model.Currency;
import org.openlca.core.model.DQIndicator;
import org.openlca.core.model.DQScore;
import org.openlca.core.model.DQSystem;
import org.openlca.core.model.Exchange;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.FlowPropertyFactor;
import org.openlca.core.model.FlowPropertyType;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ImpactCategory;
import org.openlca.core.model.ImpactFactor;
import org.openlca.core.model.ImpactMethod;
import org.openlca.core.model.ImpactMethod.ParameterMean;
import org.openlca.core.model.Location;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.NwFactor;
import org.openlca.core.model.NwSet;
import org.openlca.core.model.Parameter;
import org.openlca.core.model.ParameterRedef;
import org.openlca.core.model.ParameterScope;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.ProcessLink;
import org.openlca.core.model.ProcessType;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.Project;
import org.openlca.core.model.ProjectVariant;
import org.openlca.core.model.RiskLevel;
import org.openlca.core.model.RootEntity;
import org.openlca.core.model.SocialAspect;
import org.openlca.core.model.SocialIndicator;
import org.openlca.core.model.Source;
import org.openlca.core.model.UncertaintyType;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;

import com.greendelta.collaboration.util.Collections;

public class DatabaseAssertion {

	private final IDatabase database;

	public static void on(IDatabase database) throws IOException {
		new DatabaseAssertion(database).run();
	}

	private DatabaseAssertion(IDatabase database) {
		this.database = database;
	}

	private void run() throws IOException {
		categories();
		locations();
		sources();
		actors();
		currencies();
		unitGroups();
		flowProperties();
		dataQualitySystems();
		globalParameters();
		socialIndicators();
		flows();
		impactMethods();
		processes();
		productSystems();
		projects();
	}

	private void categories() {
		List<Category> categories = new CategoryDao(database).getAll();
		assertEquals(1, categories.size());
		category(categories.get(0));
	}

	private void category(Category category) {
		refId("f3d7a459-1bef-37da-936c-2499b7b9ed26", category);
		assertEquals("Elementary", category.getName());
		assertEquals(ModelType.FLOW, category.getModelType());
		assertEquals(0, category.getChildCategories().size());
		assertNull(category.getCategory());
	}

	private void locations() {
		List<Location> locations = new LocationDao(database).getAll();
		assertEquals(1, locations.size());
		location(locations.get(0));
	}

	private void location(Location location) {
		refId("f8e19f44-9f17-39d3-bdcc-93dd244ec3bb", location);
		assertEquals(1543222759783l, location.getLastChange());
		assertEquals(1, location.getVersion());
		assertNull(location.getCategory());
		assertEquals("Location", location.getName());
		assertEquals("A location", location.getDescription());
		assertEquals("LN", location.getCode());
		assertEquals(5d, location.getLongitude(), 0);
		assertEquals(10d, location.getLatitude(), 0);
		assertNotEquals(0, location.getKmz().length);
		// TODO more detailed test, some random ids in the kmz change when converting to
		// geojson and back, for now ensure, kmz is set
	}

	private void sources() throws IOException {
		List<Source> sources = new SourceDao(database).getAll();
		assertEquals(2, sources.size());
		unique(sources);
		for (Source source : sources) {
			assertNull(source.getCategory());
			switch (source.getRefId()) {
			case "9727bfb0-93dd-475f-be43-f4ada87d9f16":
				source(source);
				break;
			case "4cac8d31-ce26-4eaf-acfc-d629b1ee9e49":
				source2(source);
				break;
			default:
				fail("Unexpected source");
			}
		}
	}

	private void source(Source source) throws IOException {
		refId("9727bfb0-93dd-475f-be43-f4ada87d9f16", source);
		assertEquals(1543222834464l, source.getLastChange());
		assertEquals(1, source.getVersion());
		assertEquals("Source", source.getName());
		assertEquals("A source", source.getDescription());
		assertEquals("https://www.source.org", source.url);
		assertEquals("Source 2018", source.textReference);
		assertEquals((short) 2018, (short) source.year);
		assertEquals("sample.pdf", source.externalFile);
		fileLength(3028, source, source.externalFile);
	}

	private void source2(Source source) {
		refId("4cac8d31-ce26-4eaf-acfc-d629b1ee9e49", source);
		assertEquals(1543224656372l, source.getLastChange());
		assertEquals(0, source.getVersion());
		assertEquals("Another source", source.getName());
	}

	private void actors() {
		List<Actor> actors = new ActorDao(database).getAll();
		assertEquals(1, actors.size());
		actor(actors.get(0));
	}

	private void actor(Actor actor) {
		refId("4f433849-5668-48ec-a646-f26554170e74", actor);
		assertEquals(1543222927076l, actor.getLastChange());
		assertEquals(1, actor.getVersion());
		assertNull(actor.getCategory());
		assertEquals("Actor", actor.getName());
		assertEquals("An actor", actor.getDescription());
		assertEquals("A street", actor.address);
		assertEquals("A city", actor.city);
		assertEquals("A country", actor.country);
		assertEquals("email@test.com", actor.email);
		assertEquals("030123456", actor.telefax);
		assertEquals("030654321", actor.telephone);
		assertEquals("https://www.example.com", actor.website);
		assertEquals("13349", actor.zipCode);
	}

	private void currencies() {
		List<Currency> currencies = new CurrencyDao(database).getAll();
		assertEquals(2, currencies.size());
		unique(currencies);
		for (Currency currency : currencies) {
			switch (currency.getRefId()) {
			case "00d03a73-6cb4-4fa5-84bd-d031c73061de":
				currency(currency);
				break;
			case "12871763-1233-4972-ac31-1d569e6164c6":
				currency2(currency);
				break;
			default:
				fail("Unexpected currency");
			}
		}
	}

	private void currency(Currency currency) {
		refId("00d03a73-6cb4-4fa5-84bd-d031c73061de", currency);
		assertEquals(1543222934440l, currency.getLastChange());
		assertEquals(0, currency.getVersion());
		assertEquals("Currency", currency.getName());
		assertEquals("A currency", currency.getDescription());
		assertNull(currency.getCategory());
		assertEquals("Currency", currency.code);
		assertEquals(1d, currency.conversionFactor, 0);
		refId("00d03a73-6cb4-4fa5-84bd-d031c73061de", currency.referenceCurrency);
	}

	private void currency2(Currency currency) {
		refId("12871763-1233-4972-ac31-1d569e6164c6", currency);
		assertEquals(1543222948934l, currency.getLastChange());
		assertEquals(1, currency.getVersion());
		assertEquals("Another currency", currency.getName());
		assertEquals("Another currency", currency.getDescription());
		assertNull(currency.getCategory());
		assertEquals("Another currency", currency.code);
		assertEquals(2d, currency.conversionFactor, 0);
		refId("00d03a73-6cb4-4fa5-84bd-d031c73061de", currency.referenceCurrency);
	}

	private void unitGroups() {
		List<UnitGroup> groups = new UnitGroupDao(database).getAll();
		assertEquals(2, groups.size());
		unique(groups);
		for (UnitGroup group : groups) {
			switch (group.getRefId()) {
			case "675a82c5-3ba5-421b-80b4-278e374751f7":
				unitGroup(group);
				break;
			case "a642092c-d51d-455e-ab81-5ff6c9e2bfb2":
				unitGroup2(group);
				break;
			default:
				fail("Unexpected unit group");
			}
		}
	}

	private void unitGroup(UnitGroup group) {
		refId("675a82c5-3ba5-421b-80b4-278e374751f7", group);
		assertEquals(1543223003461l, group.getLastChange());
		assertEquals(2, group.getVersion());
		assertEquals("Unit group", group.getName());
		assertEquals("A unit group", group.getDescription());
		assertNull(group.getCategory());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", group.getDefaultFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", group.getReferenceUnit());
		units(group.getUnits());
	}

	private void units(List<Unit> units) {
		assertEquals(2, units.size());
		unique(units);
		for (Unit unit : units) {
			switch (unit.getRefId()) {
			case "47f37b03-1d2c-4460-8b80-5bd0ca519f4e":
				unit(unit);
				break;
			case "cd32dd5c-6a2a-471e-800e-97eb0454dcb2":
				unit2(unit);
				break;
			default:
				fail("Unexpected unit");
			}
		}
	}

	private void unit(Unit unit) {
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", unit);
		assertEquals("unit", unit.getName());
		assertEquals("a unit", unit.getDescription());
		assertArrayEquals(new String[] { "u" }, unit.getSynonyms().split(";"));
		assertEquals(1d, unit.getConversionFactor(), 0);
	}

	private void unit2(Unit unit) {
		refId("cd32dd5c-6a2a-471e-800e-97eb0454dcb2", unit);
		assertEquals("other", unit.getName());
		assertEquals("another unit", unit.getDescription());
		assertArrayEquals(new String[] { "o", "ou" }, unit.getSynonyms().split(";"));
		assertEquals(2d, unit.getConversionFactor(), 0);
	}

	private void unitGroup2(UnitGroup group) {
		refId("a642092c-d51d-455e-ab81-5ff6c9e2bfb2", group);
		assertEquals(1543223595877l, group.getLastChange());
		assertEquals(0, group.getVersion());
		assertEquals("Money", group.getName());
		assertNull(group.getCategory());
		assertEquals(1, group.getUnits().size());
		assertNull(group.getDefaultFlowProperty());
		refId("234b3562-f55a-44e9-8131-70fd9092ed84", group.getReferenceUnit());
		unit3(group.getUnits().get(0));
	}

	private void unit3(Unit unit) {
		assertEquals("234b3562-f55a-44e9-8131-70fd9092ed84", unit.getRefId());
		assertEquals("Dollar", unit.getName());
		assertEquals(1d, unit.getConversionFactor(), 0);
	}

	private void flowProperties() {
		List<FlowProperty> properties = new FlowPropertyDao(database).getAll();
		assertEquals(2, properties.size());
		unique(properties);
		for (FlowProperty property : properties) {
			switch (property.getRefId()) {
			case "70a1c611-c314-45ef-8fe0-7129f401df6f":
				flowProperty(property);
				break;
			case "42d414ef-595f-43ec-94a2-2633de752e72":
				flowProperty2(property);
				break;
			default:
				fail("Unexpected flow property");
			}
		}
	}

	private void flowProperty(FlowProperty property) {
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
		assertEquals(1543222997820l, property.getLastChange());
		assertEquals(0, property.getVersion());
		assertEquals("Flow property", property.getName());
		assertNull(property.getCategory());
		assertEquals(FlowPropertyType.PHYSICAL, property.getFlowPropertyType());
		refId("675a82c5-3ba5-421b-80b4-278e374751f7", property.getUnitGroup());
	}

	private void flowProperty2(FlowProperty property) {
		refId("42d414ef-595f-43ec-94a2-2633de752e72", property);
		assertEquals(1543223624846l, property.getLastChange());
		assertEquals(1, property.getVersion());
		assertEquals("Economic", property.getName());
		assertEquals("An economic flow property", property.getDescription());
		assertNull(property.getCategory());
		assertEquals(FlowPropertyType.ECONOMIC, property.getFlowPropertyType());
		refId("a642092c-d51d-455e-ab81-5ff6c9e2bfb2", property.getUnitGroup());
	}

	private void dataQualitySystems() {
		List<DQSystem> systems = new DQSystemDao(database).getAll();
		assertEquals(1, systems.size());
		dataQualitySystem(systems.get(0));
	}

	private void dataQualitySystem(DQSystem system) {
		assertEquals("44c55909-51dc-49ee-970f-0b13a2da5f21", system.getRefId());
		assertEquals(1543223255454l, system.getLastChange());
		assertEquals(1, system.getVersion());
		assertNull(system.getCategory());
		assertEquals("Data quality system", system.getName());
		assertEquals("A data quality system", system.getDescription());
		assertTrue(system.hasUncertainties);
		assertEquals(2, system.getScoreCount());
		refId("9727bfb0-93dd-475f-be43-f4ada87d9f16", system.source);
		dataQualityIndicators(system.indicators);
	}

	private void dataQualityIndicators(List<DQIndicator> indicators) {
		assertEquals(2, indicators.size());
		unique(indicators, indicator -> indicator.position);
		for (DQIndicator indicator : indicators) {
			switch (indicator.position) {
			case 1:
				dataQualityIndicator(indicator);
				break;
			case 2:
				dataQualityIndicator2(indicator);
				break;
			default:
				fail("Unexpected indicator");
			}
		}
	}

	private void dataQualityIndicator(DQIndicator indicator) {
		assertEquals(1, indicator.position);
		assertEquals("Indicator 1", indicator.name);
		dataQualityScores(indicator.scores);
	}

	private void dataQualityScores(List<DQScore> scores) {
		assertEquals(2, scores.size());
		unique(scores, score -> score.position);
		for (DQScore score : scores) {
			switch (score.position) {
			case 1:
				dataQualityScore(score);
				break;
			case 2:
				dataQualityScore2(score);
				break;
			default:
				fail("Unexpected score");
			}
		}
	}

	private void dataQualityScore(DQScore score) {
		assertEquals(1, score.position);
		assertEquals("Score 1", score.label);
		assertEquals("Indicator 1 - score 1", score.description);
		assertEquals(1d, score.uncertainty, 0);
	}

	private void dataQualityScore2(DQScore score) {
		assertEquals(2, score.position);
		assertEquals("Score 2", score.label);
		assertEquals("Indicator 1 - score 2", score.description);
		assertEquals(2d, score.uncertainty, 0);
	}

	private void dataQualityIndicator2(DQIndicator indicator) {
		assertEquals(2, indicator.position);
		assertEquals("Indicator 2", indicator.name);
		dataQualityScores2(indicator.scores);
	}

	private void dataQualityScores2(List<DQScore> scores) {
		assertEquals(2, scores.size());
		unique(scores, score -> score.position);
		for (DQScore score : scores) {
			switch (score.position) {
			case 1:
				dataQualityScore3(score);
				break;
			case 2:
				dataQualityScore4(score);
				break;
			default:
				fail("Unexpected score");
			}
		}
	}

	private void dataQualityScore3(DQScore score) {
		assertEquals(1, score.position);
		assertEquals("Score 1", score.label);
		assertEquals("Indicator 2 - score 1", score.description);
		assertEquals(3d, score.uncertainty, 0);
	}

	private void dataQualityScore4(DQScore score) {
		assertEquals(2, score.position);
		assertEquals("Score 2", score.label);
		assertEquals("Indicator 2 - score 2", score.description);
		assertEquals(4d, score.uncertainty, 0);
	}

	private void globalParameters() {
		List<Parameter> parameters = new ParameterDao(database).getGlobalParameters();
		assertEquals(6, parameters.size());
		unique(parameters);
		for (Parameter parameter : parameters) {
			switch (parameter.getRefId()) {
			case "845e1d93-b217-4cbf-b1da-2f08f1de3287":
				globalParameter(parameter);
				break;
			case "0c63ad51-be49-45e0-aefd-9de1d5f16257":
				globalParameter2(parameter);
				break;
			case "81466aa1-0673-44f2-a9cb-63fe21c2a71a":
				globalParameter3(parameter);
				break;
			case "1726a5fa-076c-4320-a28d-24c09a714735":
				globalParameter4(parameter);
				break;
			case "0fab72cf-89fa-40fc-bc38-b99f519e8b95":
				globalParameter5(parameter);
				break;
			case "1b724ef1-5c09-4bac-beff-5cef0190c39f":
				globalParameter6(parameter);
				break;
			default:
				fail("Unexpected parameter");
			}
		}
	}

	private void globalParameter(Parameter parameter) {
		refId("845e1d93-b217-4cbf-b1da-2f08f1de3287", parameter);
		assertEquals(1543223279458l, parameter.getLastChange());
		assertEquals(0, parameter.getVersion());
		assertEquals("Input_Parameter", parameter.getName());
		assertEquals("An input parameter", parameter.getDescription());
		assertNull(parameter.getCategory());
		assertEquals(ParameterScope.GLOBAL, parameter.scope);
		assertTrue(parameter.isInputParameter);
		assertEquals(4d, parameter.value, 0);
		assertNull(parameter.formula);
		assertNull(parameter.uncertainty);
	}

	private void globalParameter2(Parameter parameter) {
		refId("0c63ad51-be49-45e0-aefd-9de1d5f16257", parameter);
		assertEquals(1543223305339l, parameter.getLastChange());
		assertEquals(0, parameter.getVersion());
		assertEquals("Dependent_Parameter", parameter.getName());
		assertEquals("A dependent parameter", parameter.getDescription());
		assertNull(parameter.getCategory());
		assertEquals(ParameterScope.GLOBAL, parameter.scope);
		assertFalse(parameter.isInputParameter);
		assertEquals(8d, parameter.value, 0);
		assertEquals("2*Input_Parameter", parameter.formula);
		assertNull(parameter.uncertainty);
	}

	private void globalParameter3(Parameter parameter) {
		refId("81466aa1-0673-44f2-a9cb-63fe21c2a71a", parameter);
		assertEquals(1543231659168l, parameter.getLastChange());
		assertEquals(1, parameter.getVersion());
		assertEquals("p1", parameter.getName());
		assertNull(parameter.getCategory());
		assertEquals(ParameterScope.GLOBAL, parameter.scope);
		assertTrue(parameter.isInputParameter);
		assertEquals(1d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.LOG_NORMAL, parameter.uncertainty.distributionType);
		assertEquals(1d, parameter.uncertainty.parameter1, 0);
		assertEquals(1d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void globalParameter4(Parameter parameter) {
		refId("1726a5fa-076c-4320-a28d-24c09a714735", parameter);
		assertEquals(1543231672692l, parameter.getLastChange());
		assertEquals(1, parameter.getVersion());
		assertEquals("p2", parameter.getName());
		assertNull(parameter.getCategory());
		assertEquals(ParameterScope.GLOBAL, parameter.scope);
		assertTrue(parameter.isInputParameter);
		assertEquals(2d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.NORMAL, parameter.uncertainty.distributionType);
		assertEquals(2d, parameter.uncertainty.parameter1, 0);
		assertEquals(1d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void globalParameter5(Parameter parameter) {
		refId("0fab72cf-89fa-40fc-bc38-b99f519e8b95", parameter);
		assertEquals(1543231695132l, parameter.getLastChange());
		assertEquals(1, parameter.getVersion());
		assertEquals("p3", parameter.getName());
		assertNull(parameter.getCategory());
		assertEquals(ParameterScope.GLOBAL, parameter.scope);
		assertTrue(parameter.isInputParameter);
		assertEquals(3d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.TRIANGLE, parameter.uncertainty.distributionType);
		assertEquals(1d, parameter.uncertainty.parameter1, 0);
		assertEquals(2d, parameter.uncertainty.parameter2, 0);
		assertEquals(3d, parameter.uncertainty.parameter3, 0);
	}

	private void globalParameter6(Parameter parameter) {
		refId("1b724ef1-5c09-4bac-beff-5cef0190c39f", parameter);
		assertEquals(1543231701239l, parameter.getLastChange());
		assertEquals(1, parameter.getVersion());
		assertEquals("p4", parameter.getName());
		assertNull(parameter.getCategory());
		assertEquals(ParameterScope.GLOBAL, parameter.scope);
		assertTrue(parameter.isInputParameter);
		assertEquals(4d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.UNIFORM, parameter.uncertainty.distributionType);
		assertEquals(2d, parameter.uncertainty.parameter1, 0);
		assertEquals(5d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void socialIndicators() {
		List<SocialIndicator> indicators = new SocialIndicatorDao(database).getAll();
		assertEquals(1, indicators.size());
		socialIndicator(indicators.get(0));
	}

	private void socialIndicator(SocialIndicator indicator) {
		assertEquals("9a6bc341-efbd-4227-9d6a-1782b39cded8", indicator.getRefId());
		assertEquals(1543223421786l, indicator.getLastChange());
		assertEquals(1, indicator.getVersion());
		assertNull(indicator.getCategory());
		assertEquals("Social indicator", indicator.getName());
		assertEquals("A social indicator", indicator.getDescription());
		assertEquals("uom", indicator.unitOfMeasurement);
		assertEquals("A schema", indicator.evaluationScheme);
		assertEquals("act", indicator.activityVariable);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", indicator.activityQuantity);
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", indicator.activityUnit);
	}

	private void flows() {
		List<Flow> flows = new FlowDao(database).getAll();
		assertEquals(10, flows.size());
		unique(flows);
		for (Flow flow : flows) {
			switch (flow.getRefId()) {
			case "7a192079-0e37-478c-a24d-4a700887c847":
				flow(flow);
				break;
			case "859f05d5-2db0-4f9f-9635-65c13cbc5666":
				flow2(flow);
				break;
			case "68d817b0-c50b-48d5-b432-ffa2595ba64d":
				flow3(flow);
				break;
			case "c9dba359-0cd8-491b-af53-1a0d12123833":
				flow4(flow);
				break;
			case "04ce7d8a-4d22-4724-8b79-b7189f1b7952":
				flow5(flow);
				break;
			case "81c33501-1706-4a3a-8a9e-5af4a3d064a9":
				flow6(flow);
				break;
			case "e8d61918-2e62-40cc-bda2-565fbb2f651d":
				flow7(flow);
				break;
			case "4ca7a7d1-845e-4e42-b565-c45de118fafe":
				flow8(flow);
				break;
			case "bc9261d4-f083-4697-99a9-354f7b04dd9d":
				flow9(flow);
				break;
			case "229351c0-5289-4744-82ed-72ce017d135b":
				flow10(flow);
				break;
			default:
				fail("Unexpected flow");
			}
		}
	}

	private void flow(Flow flow) {
		refId("7a192079-0e37-478c-a24d-4a700887c847", flow);
		assertEquals(1543224456950l, flow.getLastChange());
		assertEquals(2, flow.getVersion());
		refId("f3d7a459-1bef-37da-936c-2499b7b9ed26", flow.getCategory());
		assertEquals("Elementary flow", flow.getName());
		assertEquals(FlowType.ELEMENTARY_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow2(Flow flow) {
		refId("859f05d5-2db0-4f9f-9635-65c13cbc5666", flow);
		assertEquals(1543224456969l, flow.getLastChange());
		assertEquals(2, flow.getVersion());
		refId("f3d7a459-1bef-37da-936c-2499b7b9ed26", flow.getCategory());
		assertEquals("Elementary flow 2", flow.getName());
		assertEquals(FlowType.ELEMENTARY_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow3(Flow flow) {
		refId("68d817b0-c50b-48d5-b432-ffa2595ba64d", flow);
		assertEquals(1543224456971l, flow.getLastChange());
		assertEquals(2, flow.getVersion());
		refId("f3d7a459-1bef-37da-936c-2499b7b9ed26", flow.getCategory());
		assertEquals("Elementary flow 3", flow.getName());
		assertEquals(FlowType.ELEMENTARY_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow4(Flow flow) {
		refId("c9dba359-0cd8-491b-af53-1a0d12123833", flow);
		assertEquals(1543224456974l, flow.getLastChange());
		assertEquals(2, flow.getVersion());
		refId("f3d7a459-1bef-37da-936c-2499b7b9ed26", flow.getCategory());
		assertEquals("Elementary flow 4", flow.getName());
		assertEquals(FlowType.ELEMENTARY_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow5(Flow flow) {
		refId("04ce7d8a-4d22-4724-8b79-b7189f1b7952", flow);
		assertEquals(1543224456976l, flow.getLastChange());
		assertEquals(2, flow.getVersion());
		refId("f3d7a459-1bef-37da-936c-2499b7b9ed26", flow.getCategory());
		assertEquals("Elementary flow 5", flow.getName());
		assertEquals(FlowType.ELEMENTARY_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow6(Flow flow) {
		refId("81c33501-1706-4a3a-8a9e-5af4a3d064a9", flow);
		assertEquals(1543233726335l, flow.getLastChange());
		assertEquals(1, flow.getVersion());
		assertNull(flow.getCategory());
		assertEquals("Input product", flow.getName());
		assertEquals(FlowType.PRODUCT_FLOW, flow.getFlowType());
		assertTrue(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow7(Flow flow) {
		refId("e8d61918-2e62-40cc-bda2-565fbb2f651d", flow);
		assertEquals(1543224787825l, flow.getLastChange());
		assertEquals(1, flow.getVersion());
		assertNull(flow.getCategory());
		assertEquals("By-product", flow.getName());
		assertEquals(FlowType.PRODUCT_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow8(Flow flow) {
		refId("4ca7a7d1-845e-4e42-b565-c45de118fafe", flow);
		assertEquals(1543224350460l, flow.getLastChange());
		assertEquals(0, flow.getVersion());
		assertNull(flow.getCategory());
		assertEquals("Waste flow", flow.getName());
		assertEquals("A waste flow", flow.getDescription());
		assertEquals(FlowType.WASTE_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow9(Flow flow) {
		refId("bc9261d4-f083-4697-99a9-354f7b04dd9d", flow);
		assertEquals(1543224423646l, flow.getLastChange());
		assertEquals(0, flow.getVersion());
		assertNull(flow.getCategory());
		assertEquals("Waste flow avoided", flow.getName());
		assertEquals(FlowType.WASTE_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		FlowProperty property = flow.getFlowPropertyFactors().get(0).getFlowProperty();
		assertEquals(1, flow.getFlowPropertyFactors().size());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", property);
	}

	private void flow10(Flow flow) {
		refId("229351c0-5289-4744-82ed-72ce017d135b", flow);
		assertEquals(1543223637023l, flow.getLastChange());
		assertEquals(4, flow.getVersion());
		assertNull(flow.getCategory());
		assertEquals("Product flow", flow.getName());
		assertEquals(FlowType.PRODUCT_FLOW, flow.getFlowType());
		assertFalse(flow.isInfrastructureFlow());
		assertEquals("123-CAS", flow.getCasNumber());
		assertEquals("CO2SO3", flow.getFormula());
		assertEquals("Product", flow.synonyms);
		refId("f8e19f44-9f17-39d3-bdcc-93dd244ec3bb", flow.getLocation());
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", flow.getReferenceFlowProperty());
		flowPropertyFactors(flow.getFlowPropertyFactors());
	}

	private void flowPropertyFactors(List<FlowPropertyFactor> factors) {
		assertEquals(2, factors.size());
		unique(factors, factor -> factor.getFlowProperty().getRefId());
		for (FlowPropertyFactor factor : factors) {
			switch (factor.getFlowProperty().getRefId()) {
			case "70a1c611-c314-45ef-8fe0-7129f401df6f":
				flowPropertyFactor(factor);
				break;
			case "42d414ef-595f-43ec-94a2-2633de752e72":
				flowPropertyFactor2(factor);
				break;
			default:
				fail("Unexpected flow property factor");
			}
		}
	}

	private void flowPropertyFactor(FlowPropertyFactor factor) {
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", factor.getFlowProperty());
		assertEquals(1d, factor.getConversionFactor(), 0);

	}

	private void flowPropertyFactor2(FlowPropertyFactor factor) {
		refId("42d414ef-595f-43ec-94a2-2633de752e72", factor.getFlowProperty());
		assertEquals(2d, factor.getConversionFactor(), 0);

	}

	private void impactMethods() throws IOException {
		List<ImpactMethod> methods = new ImpactMethodDao(database).getAll();
		assertEquals(1, methods.size());
		impactMethod(methods.get(0));
	}

	private void impactMethod(ImpactMethod method) throws IOException {
		assertEquals("eedd7332-f522-40e6-ae77-9a0083b1b881", method.getRefId());
		assertEquals(1543241115364l, method.getLastChange());
		assertEquals(14, method.getVersion());
		assertNull(method.getCategory());
		assertEquals("Impact method", method.getName());
		assertEquals("An impact method", method.getDescription());
		assertEquals(ParameterMean.ARITHMETIC_MEAN, method.parameterMean);
		assertEquals(1, method.impactCategories.size());
		impactCategory(method.impactCategories.get(0));
		assertEquals(1, method.nwSets.size());
		normalizationAndWeightingSet(method.nwSets.get(0));
		parameters(method.parameters);
		shapeFiles(method);
	}

	private void shapeFiles(ImpactMethod method) throws IOException {
		fileLength(55928578, method, "AWARE_SHP.dbf");
		fileLength(865, method, "AWARE_SHP.gisolca");
		fileLength(143, method, "AWARE_SHP.prj");
		fileLength(35717, method, "AWARE_SHP.qgs");
		fileLength(35666, method, "AWARE_SHP.qgs~");
		fileLength(257, method, "AWARE_SHP.qpj");
		fileLength(18559724, method, "AWARE_SHP.shp");
		fileLength(576676, method, "AWARE_SHP.shx");
	}

	private void normalizationAndWeightingSet(NwSet set) {
		assertEquals("Nw set", set.getName());
		assertEquals("normed", set.weightedScoreUnit);
		assertEquals(1, set.factors.size());
		normalizationAndWeightingFactor(set.factors.get(0));
	}

	private void normalizationAndWeightingFactor(NwFactor factor) {
		refId("bbf94074-16f3-4acc-bb6a-77ae0d9070de", factor.getImpactCategory());
		assertEquals(2d, factor.getNormalisationFactor(), 0);
		assertEquals(3d, factor.getWeightingFactor(), 0);
	}

	private void impactCategory(ImpactCategory category) {
		assertEquals("bbf94074-16f3-4acc-bb6a-77ae0d9070de", category.getRefId());
		assertEquals("Impact category", category.getName());
		assertEquals("An impact category", category.getDescription());
		assertEquals("ref-unit", category.referenceUnit);
		impactFactors(category.impactFactors);
	}

	private void impactFactors(List<ImpactFactor> factors) {
		assertEquals(5, factors.size());
		unique(factors, factor -> factor.flow.getRefId());
		for (ImpactFactor factor : factors) {
			switch (factor.flow.getRefId()) {
			case "7a192079-0e37-478c-a24d-4a700887c847":
				impactFactor(factor);
				break;
			case "859f05d5-2db0-4f9f-9635-65c13cbc5666":
				impactFactor2(factor);
				break;
			case "68d817b0-c50b-48d5-b432-ffa2595ba64d":
				impactFactor3(factor);
				break;
			case "c9dba359-0cd8-491b-af53-1a0d12123833":
				impactFactor4(factor);
				break;
			case "04ce7d8a-4d22-4724-8b79-b7189f1b7952":
				impactFactor5(factor);
				break;
			default:
				fail("Unexpected impact factor");
			}
		}
	}

	private void impactFactor(ImpactFactor factor) {
		refId("7a192079-0e37-478c-a24d-4a700887c847", factor.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", factor.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", factor.unit);
		assertEquals(3.956977077e9, factor.value, 0);
		assertEquals("2*cons_irri", factor.formula);
		assertNull(factor.uncertainty);
	}

	private void impactFactor2(ImpactFactor factor) {
		refId("859f05d5-2db0-4f9f-9635-65c13cbc5666", factor.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", factor.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", factor.unit);
		assertEquals(2d, factor.value, 0);
		assertNotNull(factor.uncertainty);
		assertEquals(UncertaintyType.LOG_NORMAL, factor.uncertainty.distributionType);
		assertEquals(3d, factor.uncertainty.parameter1, 0);
		assertEquals(2d, factor.uncertainty.parameter2, 0);
		assertNull(factor.uncertainty.parameter3);
	}

	private void impactFactor3(ImpactFactor factor) {
		refId("68d817b0-c50b-48d5-b432-ffa2595ba64d", factor.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", factor.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", factor.unit);
		assertEquals(3d, factor.value, 0);
		assertNotNull(factor.uncertainty);
		assertEquals(UncertaintyType.NORMAL, factor.uncertainty.distributionType);
		assertEquals(4d, factor.uncertainty.parameter1, 0);
		assertEquals(2d, factor.uncertainty.parameter2, 0);
		assertNull(factor.uncertainty.parameter3);
	}

	private void impactFactor4(ImpactFactor factor) {
		refId("c9dba359-0cd8-491b-af53-1a0d12123833", factor.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", factor.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", factor.unit);
		assertEquals(4d, factor.value, 0);
		assertNotNull(factor.uncertainty);
		assertEquals(UncertaintyType.TRIANGLE, factor.uncertainty.distributionType);
		assertEquals(3d, factor.uncertainty.parameter1, 0);
		assertEquals(4d, factor.uncertainty.parameter2, 0);
		assertEquals(5d, factor.uncertainty.parameter3, 0);
	}

	private void impactFactor5(ImpactFactor factor) {
		refId("04ce7d8a-4d22-4724-8b79-b7189f1b7952", factor.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", factor.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", factor.unit);
		assertEquals(5d, factor.value, 0);
		assertNotNull(factor.uncertainty);
		assertEquals(UncertaintyType.UNIFORM, factor.uncertainty.distributionType);
		assertEquals(2d, factor.uncertainty.parameter1, 0);
		assertEquals(8d, factor.uncertainty.parameter2, 0);
		assertNull(factor.uncertainty.parameter3);
	}

	private void parameters(List<Parameter> parameters) {
		assertEquals(6, parameters.size());
		unique(parameters, parameter -> parameter.getName());
		for (Parameter parameter : parameters) {
			switch (parameter.getName()) {
			case "cons_irri":
				parameter(parameter);
				break;
			case "p_2":
				parameter2(parameter);
				break;
			case "p_3":
				parameter3(parameter);
				break;
			case "p_4":
				parameter4(parameter);
				break;
			case "p_5":
				parameter5(parameter);
				break;
			case "p_6":
				parameter6(parameter);
				break;
			default:
				fail("Unknown parameter");
			}
		}
	}

	private void parameter(Parameter parameter) {
		assertEquals("cons_irri", parameter.getName());
		assertEquals("from shapefile: AWARE_SHP", parameter.getDescription());
		assertEquals("AWARE_SHP", parameter.externalSource);
		assertEquals("SHAPE_FILE", parameter.sourceType);
		assertEquals(1.9784885385e9d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.UNIFORM, parameter.uncertainty.distributionType);
		assertEquals(0d, parameter.uncertainty.parameter1, 0);
		assertEquals(3.956977077e9d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void parameter2(Parameter parameter) {
		assertEquals("p_2", parameter.getName());
		assertEquals(2d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.LOG_NORMAL, parameter.uncertainty.distributionType);
		assertEquals(3d, parameter.uncertainty.parameter1, 0);
		assertEquals(2d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void parameter3(Parameter parameter) {
		assertEquals("p_3", parameter.getName());
		assertEquals(3d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.NORMAL, parameter.uncertainty.distributionType);
		assertEquals(5d, parameter.uncertainty.parameter1, 0);
		assertEquals(2d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void parameter4(Parameter parameter) {
		assertEquals("p_4", parameter.getName());
		assertEquals(4d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.TRIANGLE, parameter.uncertainty.distributionType);
		assertEquals(1d, parameter.uncertainty.parameter1, 0);
		assertEquals(4d, parameter.uncertainty.parameter2, 0);
		assertEquals(7d, parameter.uncertainty.parameter3, 0);
	}

	private void parameter5(Parameter parameter) {
		assertEquals("p_5", parameter.getName());
		assertEquals(5d, parameter.value, 0);
		assertNull(parameter.uncertainty);
	}

	private void parameter6(Parameter parameter) {
		assertEquals("p_6", parameter.getName());
		assertEquals(6d, parameter.value, 0);
		assertEquals("2*p_3", parameter.formula);
		assertNull(parameter.uncertainty);
	}

	private void processes() {
		List<Process> processes = new ProcessDao(database).getAll();
		assertEquals(3, processes.size());
		unique(processes);
		for (Process process : processes) {
			assertNull(process.getCategory());
			switch (process.getRefId()) {
			case "e25c672d-f31f-4420-a564-274b762b5a3d":
				process(process);
				break;
			case "ca6d3914-a049-4750-9b02-93e1418d7fe6":
				process2(process);
				break;
			case "3043dfd7-ab00-4d29-84ea-fc7471f48600":
				process3(process);
				break;
			default:
				fail("Unexpected process");
			}
		}
	}

	private void process(Process process) {
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", process);
		assertEquals(1543243419254l, process.getLastChange());
		assertEquals(20, process.getVersion());
		assertEquals("Process", process.getName());
		assertEquals("A process", process.getDescription());
		assertEquals(ProcessType.UNIT_PROCESS, process.getProcessType());
		assertTrue(process.isInfrastructureProcess());
		assertEquals(0, process.getAllocationFactors().size());
		assertNull(process.getDefaultAllocationMethod());
		assertNull(process.currency);
		assertEquals("(1;2)", process.dqEntry);
		refId("44c55909-51dc-49ee-970f-0b13a2da5f21", process.dqSystem);
		refId("44c55909-51dc-49ee-970f-0b13a2da5f21", process.exchangeDqSystem);
		refId("44c55909-51dc-49ee-970f-0b13a2da5f21", process.socialDqSystem);
		refId("f8e19f44-9f17-39d3-bdcc-93dd244ec3bb", process.getLocation());
		documentation(process.getDocumentation());
		refId("229351c0-5289-4744-82ed-72ce017d135b", process.getQuantitativeReference().flow);
		assertEquals(8, process.lastInternalId);
		exchanges(process.getExchanges());
		assertEquals(1, process.socialAspects.size());
		socialAspect(process.socialAspects.get(0));
		parameters2(process.getParameters());
	}

	private void documentation(ProcessDocumentation doc) {
		assertEquals(1541026800000l, doc.getValidFrom().getTime());
		assertEquals(1543532400000l, doc.getValidUntil().getTime());
		assertEquals(1543224179304l, doc.getCreationDate().getTime());
		assertEquals("A time description", doc.getTime());
		assertEquals("A location description", doc.getGeography());
		assertEquals("A technology description", doc.getTechnology());
		assertEquals("An intention", doc.getIntendedApplication());
		assertEquals("Some restrictions", doc.getRestrictions());
		assertEquals("A project", doc.getProject());
		assertTrue(doc.isCopyright());
		refId("4f433849-5668-48ec-a646-f26554170e74", doc.getDataSetOwner());
		refId("4f433849-5668-48ec-a646-f26554170e74", doc.getDataGenerator());
		refId("4f433849-5668-48ec-a646-f26554170e74", doc.getDataDocumentor());
		refId("9727bfb0-93dd-475f-be43-f4ada87d9f16", doc.getPublication());
		assertEquals("An LCI method", doc.getInventoryMethod());
		assertEquals("Some modeling constants", doc.getModelingConstants());
		assertEquals("Completeness of data", doc.getCompleteness());
		assertEquals("Selection of data", doc.getDataSelection());
		assertEquals("Treatment of data", doc.getDataTreatment());
		assertEquals("A sampling procedure", doc.getSampling());
		assertEquals("A period in time", doc.getDataCollectionPeriod());
		refId("4f433849-5668-48ec-a646-f26554170e74", doc.getReviewer());
		assertEquals("Evalutation", doc.getReviewDetails());
		sources(doc.getSources());
	}

	private void sources(List<Source> sources) {
		assertEquals(2, sources.size());
		unique(sources);
		for (Source source : sources) {
			switch (source.getRefId()) {
			case "4cac8d31-ce26-4eaf-acfc-d629b1ee9e49":
			case "9727bfb0-93dd-475f-be43-f4ada87d9f16":
				continue;
			default:
				fail("Unknown source");
			}
		}
	}

	private void exchanges(List<Exchange> exchanges) {
		assertEquals(8, exchanges.size());
		unique(exchanges, exchange -> exchange.internalId);
		for (Exchange exchange : exchanges) {
			switch (exchange.internalId) {
			case 1:
				exchange(exchange);
				break;
			case 2:
				exchange2(exchange);
				break;
			case 3:
				exchange3(exchange);
				break;
			case 4:
				exchange4(exchange);
				break;
			case 5:
				exchange5(exchange);
				break;
			case 6:
				exchange6(exchange);
				break;
			case 7:
				exchange7(exchange);
				break;
			case 8:
				exchange8(exchange);
				break;
			default:
				fail("Unexpected exchange");
			}
		}
	}

	private void exchange(Exchange exchange) {
		assertEquals(1, exchange.internalId);
		refId("229351c0-5289-4744-82ed-72ce017d135b", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertFalse(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
		assertNull(exchange.uncertainty);
	}

	private void exchange2(Exchange exchange) {
		assertEquals(2, exchange.internalId);
		refId("859f05d5-2db0-4f9f-9635-65c13cbc5666", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(2d, exchange.amount, 0);
		assertNotNull(exchange.uncertainty);
		assertEquals(UncertaintyType.TRIANGLE, exchange.uncertainty.distributionType);
		assertEquals(1d, exchange.uncertainty.parameter1, 0);
		assertEquals(2d, exchange.uncertainty.parameter2, 0);
		assertEquals(3d, exchange.uncertainty.parameter3, 0);
	}

	private void exchange3(Exchange exchange) {
		assertEquals(3, exchange.internalId);
		refId("68d817b0-c50b-48d5-b432-ffa2595ba64d", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(3d, exchange.amount, 0);
		assertNotNull(exchange.uncertainty);
		assertEquals(UncertaintyType.NORMAL, exchange.uncertainty.distributionType);
		assertEquals(2d, exchange.uncertainty.parameter1, 0);
		assertEquals(1d, exchange.uncertainty.parameter2, 0);
		assertNull(exchange.uncertainty.parameter3);
	}

	private void exchange4(Exchange exchange) {
		assertEquals(4, exchange.internalId);
		refId("7a192079-0e37-478c-a24d-4a700887c847", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
		assertEquals("(2;1)", exchange.dqEntry);
		assertNotNull(exchange.uncertainty);
		assertEquals(UncertaintyType.LOG_NORMAL, exchange.uncertainty.distributionType);
		assertEquals(1d, exchange.uncertainty.parameter1, 0);
		assertEquals(4.35952777044281d, exchange.uncertainty.parameter2, 0);
		assertNull(exchange.uncertainty.parameter3);
	}

	private void exchange5(Exchange exchange) {
		assertEquals(5, exchange.internalId);
		refId("c9dba359-0cd8-491b-af53-1a0d12123833", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(4d, exchange.amount, 0);
		assertNotNull(exchange.uncertainty);
		assertEquals(UncertaintyType.UNIFORM, exchange.uncertainty.distributionType);
		assertEquals(1d, exchange.uncertainty.parameter1, 0);
		assertEquals(3d, exchange.uncertainty.parameter2, 0);
		assertNull(exchange.uncertainty.parameter3);
	}

	private void exchange6(Exchange exchange) {
		assertEquals(6, exchange.internalId);
		refId("04ce7d8a-4d22-4724-8b79-b7189f1b7952", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(5d, exchange.amount, 0);
		assertNull(exchange.uncertainty);
	}

	private void exchange7(Exchange exchange) {
		assertEquals(7, exchange.internalId);
		refId("81c33501-1706-4a3a-8a9e-5af4a3d064a9", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
		assertNull(exchange.uncertainty);
		assertEquals("An input product", exchange.description);
		assertEquals(6d, exchange.costs, 0);
		assertEquals("2.0*p_3", exchange.costFormula);
		refId("00d03a73-6cb4-4fa5-84bd-d031c73061de", exchange.currency);
		Process provider = new ProcessDao(database).getForId(exchange.defaultProviderId);
		refId("ca6d3914-a049-4750-9b02-93e1418d7fe6", provider);
	}

	private void exchange8(Exchange exchange) {
		assertEquals(8, exchange.internalId);
		refId("4ca7a7d1-845e-4e42-b565-c45de118fafe", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertFalse(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(2d, exchange.amount, 0);
		assertNull(exchange.uncertainty);
		Process wasteProvider = new ProcessDao(database).getForId(exchange.defaultProviderId);
		refId("3043dfd7-ab00-4d29-84ea-fc7471f48600", wasteProvider);
	}

	private void parameters2(List<Parameter> parameters) {
		assertEquals(6, parameters.size());
		unique(parameters, parameter -> parameter.getName());
		for (Parameter parameter : parameters) {
			switch (parameter.getName()) {
			case "p_1":
				parameter7(parameter);
				break;
			case "p_2":
				parameter8(parameter);
				break;
			case "p_3":
				parameter9(parameter);
				break;
			case "p_4":
				parameter10(parameter);
				break;
			case "p_5":
				parameter11(parameter);
				break;
			case "p_6":
				parameter12(parameter);
				break;
			default:
				fail("Unknown parameter");
			}
		}
	}

	private void parameter7(Parameter parameter) {
		assertEquals("p_1", parameter.getName());
		assertEquals(1d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.LOG_NORMAL, parameter.uncertainty.distributionType);
		assertEquals(3d, parameter.uncertainty.parameter1, 0);
		assertEquals(2d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void parameter8(Parameter parameter) {
		assertEquals("p_2", parameter.getName());
		assertEquals(2d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.NORMAL, parameter.uncertainty.distributionType);
		assertEquals(2d, parameter.uncertainty.parameter1, 0);
		assertEquals(1d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void parameter9(Parameter parameter) {
		assertEquals("p_3", parameter.getName());
		assertEquals(3d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.TRIANGLE, parameter.uncertainty.distributionType);
		assertEquals(2d, parameter.uncertainty.parameter1, 0);
		assertEquals(5d, parameter.uncertainty.parameter2, 0);
		assertEquals(6d, parameter.uncertainty.parameter3, 0);
	}

	private void parameter10(Parameter parameter) {
		assertEquals("p_4", parameter.getName());
		assertEquals(4d, parameter.value, 0);
		assertNotNull(parameter.uncertainty);
		assertEquals(UncertaintyType.UNIFORM, parameter.uncertainty.distributionType);
		assertEquals(2d, parameter.uncertainty.parameter1, 0);
		assertEquals(5d, parameter.uncertainty.parameter2, 0);
		assertNull(parameter.uncertainty.parameter3);
	}

	private void parameter11(Parameter parameter) {
		assertEquals("p_5", parameter.getName());
		assertEquals("A description", parameter.getDescription());
		assertEquals(5d, parameter.value, 0);
		assertNull(parameter.uncertainty);
	}

	private void parameter12(Parameter parameter) {
		assertEquals("p_6", parameter.getName());
		assertEquals("Another description", parameter.getDescription());
		assertEquals(1d, parameter.value, 0);
		assertEquals("1.0", parameter.formula);
		assertNull(parameter.uncertainty);
	}

	private void socialAspect(SocialAspect aspect) {
		refId("9a6bc341-efbd-4227-9d6a-1782b39cded8", aspect.indicator);
		refId("9727bfb0-93dd-475f-be43-f4ada87d9f16", aspect.source);
		assertEquals(3d, aspect.activityValue, 0);
		assertEquals("A comment", aspect.comment);
		assertEquals("(2;1)", aspect.quality);
		assertEquals("2", aspect.rawAmount);
		assertEquals(RiskLevel.VERY_LOW_RISK, aspect.riskLevel);
	}

	private void process2(Process process) {
		refId("ca6d3914-a049-4750-9b02-93e1418d7fe6", process);
		assertEquals(1543254797382l, process.getLastChange());
		assertEquals(4295098372l, process.getVersion());
		assertEquals("Input process", process.getName());
		assertEquals(ProcessType.LCI_RESULT, process.getProcessType());
		assertEquals(0, process.socialAspects.size());
		assertFalse(process.isInfrastructureProcess());
		assertEquals(AllocationMethod.ECONOMIC, process.getDefaultAllocationMethod());
		assertNull(process.currency);
		assertNull(process.dqEntry);
		assertNull(process.dqSystem);
		assertNull(process.exchangeDqSystem);
		assertNull(process.socialDqSystem);
		assertNull(process.getLocation());
		refId("81c33501-1706-4a3a-8a9e-5af4a3d064a9", process.getQuantitativeReference().flow);
		assertEquals(4, process.lastInternalId);
		exchanges2(process.getExchanges());
		allocationFactors(process);
	}

	private void exchanges2(List<Exchange> exchanges) {
		assertEquals(4, exchanges.size());
		unique(exchanges, exchange -> exchange.internalId);
		for (Exchange exchange : exchanges) {
			switch (exchange.internalId) {
			case 1:
				exchange9(exchange);
				break;
			case 2:
				exchange10(exchange);
				break;
			case 3:
				exchange11(exchange);
				break;
			case 4:
				exchange12(exchange);
				break;
			default:
				fail("Unexpected exchange");
			}
		}
	}

	private void exchange9(Exchange exchange) {
		assertEquals(1, exchange.internalId);
		refId("81c33501-1706-4a3a-8a9e-5af4a3d064a9", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertFalse(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
	}

	private void exchange10(Exchange exchange) {
		assertEquals(2, exchange.internalId);
		refId("7a192079-0e37-478c-a24d-4a700887c847", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
	}

	private void exchange11(Exchange exchange) {
		assertEquals(3, exchange.internalId);
		refId("e8d61918-2e62-40cc-bda2-565fbb2f651d", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertFalse(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(0.2d, exchange.amount, 0);
	}

	private void exchange12(Exchange exchange) {
		assertEquals(4, exchange.internalId);
		refId("859f05d5-2db0-4f9f-9635-65c13cbc5666", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
	}

	private void allocationFactors(Process process) {
		Map<Long, Flow> productMap = Collections.map(process.getExchanges(), e -> e.flow.getId(), e -> e.flow);
		Set<String> ids = new HashSet<>();
		for (AllocationFactor factor : process.getAllocationFactors()) {
			Flow product = productMap.get(factor.productId);
			AllocationMethod type = factor.method;
			switch (type) {
			case PHYSICAL:
				ids.add(type.name() + "-" + product.getRefId());
				physicalAllocationFactor(factor, product);
				break;
			case ECONOMIC:
				ids.add(type.name() + "-" + product.getRefId());
				economicAllocationFactor(factor, product);
				break;
			case CAUSAL:
				ids.add(type.name() + "-" + product.getRefId() + "-" + factor.exchange.internalId);
				causalAllocationFactor(factor, product);
				break;
			default:
				fail("Unexpected allocation factor");
			}
		}
		assertEquals(process.getAllocationFactors().size(), ids.size());
	}

	private void physicalAllocationFactor(AllocationFactor factor, Flow product) {
		switch (product.getRefId()) {
		case "81c33501-1706-4a3a-8a9e-5af4a3d064a9":
			physicalAllocationFactor(factor);
			break;
		case "e8d61918-2e62-40cc-bda2-565fbb2f651d":
			physicalAllocationFactor2(factor);
			break;
		default:
			fail("Unexpected allocation factor");
		}
	}

	private void physicalAllocationFactor(AllocationFactor factor) {
		assertEquals(AllocationMethod.PHYSICAL, factor.method);
		assertNull(factor.exchange);
		assertEquals(0.6d, factor.value, 0);
	}

	private void physicalAllocationFactor2(AllocationFactor factor) {
		assertEquals(AllocationMethod.PHYSICAL, factor.method);
		assertNull(factor.exchange);
		assertEquals(0.4d, factor.value, 0);
	}

	private void economicAllocationFactor(AllocationFactor factor, Flow product) {
		switch (product.getRefId()) {
		case "81c33501-1706-4a3a-8a9e-5af4a3d064a9":
			economicAllocationFactor(factor);
			break;
		case "e8d61918-2e62-40cc-bda2-565fbb2f651d":
			economicAllocationFactor2(factor);
			break;
		default:
			fail("Unexpected allocation factor");
		}
	}

	private void economicAllocationFactor(AllocationFactor factor) {
		assertEquals(AllocationMethod.ECONOMIC, factor.method);
		assertNull(factor.exchange);
		assertEquals(0.7d, factor.value, 0);
	}

	private void economicAllocationFactor2(AllocationFactor factor) {
		assertEquals(AllocationMethod.ECONOMIC, factor.method);
		assertNull(factor.exchange);
		assertEquals(0.3d, factor.value, 0);
	}

	private void causalAllocationFactor(AllocationFactor factor, Flow product) {
		switch (factor.exchange.flow.getRefId()) {
		case "7a192079-0e37-478c-a24d-4a700887c847":
			switch (product.getRefId()) {
			case "81c33501-1706-4a3a-8a9e-5af4a3d064a9":
				causalAllocationFactor(factor);
				break;
			case "e8d61918-2e62-40cc-bda2-565fbb2f651d":
				causalAllocationFactor2(factor);
				break;
			default:
				fail("Unexpected allocation factor");
			}
			break;
		case "859f05d5-2db0-4f9f-9635-65c13cbc5666":
			switch (product.getRefId()) {
			case "81c33501-1706-4a3a-8a9e-5af4a3d064a9":
				causalAllocationFactor3(factor);
				break;
			case "e8d61918-2e62-40cc-bda2-565fbb2f651d":
				causalAllocationFactor4(factor);
				break;
			default:
				fail("Unexpected allocation factor");
			}
			break;
		default:
			fail("Unexpected allocation factor");
		}
	}

	private void causalAllocationFactor(AllocationFactor factor) {
		assertEquals(AllocationMethod.CAUSAL, factor.method);
		assertNotNull(factor.exchange);
		assertEquals(0.65, factor.value, 0);
	}

	private void causalAllocationFactor2(AllocationFactor factor) {
		assertEquals(AllocationMethod.CAUSAL, factor.method);
		assertNotNull(factor.exchange);
		assertEquals(0.35, factor.value, 0);
	}

	private void causalAllocationFactor3(AllocationFactor factor) {
		assertEquals(AllocationMethod.CAUSAL, factor.method);
		assertNotNull(factor.exchange);
		assertEquals(0.55, factor.value, 0);
	}

	private void causalAllocationFactor4(AllocationFactor factor) {
		assertEquals(AllocationMethod.CAUSAL, factor.method);
		assertNotNull(factor.exchange);
		assertEquals(0.45, factor.value, 0);
	}

	private void process3(Process process) {
		refId("3043dfd7-ab00-4d29-84ea-fc7471f48600", process);
		assertEquals(1543224577001l, process.getLastChange());
		assertEquals(5, process.getVersion());
		assertEquals("Waste treatment", process.getName());
		assertEquals(ProcessType.UNIT_PROCESS, process.getProcessType());
		assertFalse(process.isInfrastructureProcess());
		assertNull(process.getDefaultAllocationMethod());
		assertNull(process.currency);
		assertNull(process.dqEntry);
		assertNull(process.dqSystem);
		assertNull(process.exchangeDqSystem);
		assertNull(process.socialDqSystem);
		assertNull(process.getLocation());
		assertEquals(0, process.socialAspects.size());
		assertEquals(0, process.getAllocationFactors().size());
		refId("4ca7a7d1-845e-4e42-b565-c45de118fafe", process.getQuantitativeReference().flow);
		assertEquals(3, process.lastInternalId);
		exchanges3(process.getExchanges());
	}

	private void exchanges3(List<Exchange> exchanges) {
		assertEquals(3, exchanges.size());
		unique(exchanges, exchange -> exchange.internalId);
		for (Exchange exchange : exchanges) {
			switch (exchange.internalId) {
			case 1:
				exchange13(exchange);
				break;
			case 2:
				exchange14(exchange);
				break;
			case 3:
				exchange15(exchange);
				break;
			default:
				fail("Unexpected exchange");
			}
		}
	}

	private void exchange13(Exchange exchange) {
		assertEquals(1, exchange.internalId);
		refId("4ca7a7d1-845e-4e42-b565-c45de118fafe", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertFalse(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
	}

	private void exchange14(Exchange exchange) {
		assertEquals(2, exchange.internalId);
		refId("bc9261d4-f083-4697-99a9-354f7b04dd9d", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertFalse(exchange.isInput);
		assertTrue(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
	}

	private void exchange15(Exchange exchange) {
		assertEquals(3, exchange.internalId);
		refId("229351c0-5289-4744-82ed-72ce017d135b", exchange.flow);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", exchange.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", exchange.unit);
		assertTrue(exchange.isInput);
		assertTrue(exchange.isAvoided);
		assertEquals(1d, exchange.amount, 0);
	}

	private void productSystems() {
		List<ProductSystem> systems = new ProductSystemDao(database).getAll();
		assertEquals(1, systems.size());
		ProductSystem system = systems.get(0);
		assertEquals("f05fe9b1-9892-4436-be0e-7c92172c5298", system.getRefId());
		assertEquals(1543225363747l, system.getLastChange());
		assertEquals(5, system.getVersion());
		assertNull(system.getCategory());
		assertEquals("Product system", system.getName());
		String expectedDescription = "First created: 2018-11-26T10:40:00\nLinking approach during creation: Prefer default providers; Preferred process type: Unit process";
		assertEquals(expectedDescription, system.getDescription());
		assertEquals(2d, system.targetAmount, 0);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", system.referenceProcess);
		refId("229351c0-5289-4744-82ed-72ce017d135b", system.referenceExchange.flow);
		assertEquals(1, system.referenceExchange.internalId);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", system.targetFlowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", system.targetUnit);
		processes(system.processes);
		processLinks(system.processLinks);
		parameterRedefs(system.parameterRedefs);
	}

	private void processes(Set<Long> processIds) {
		assertEquals(3, processIds.size());
		Set<String> refIds = new HashSet<>();
		for (Long processId : processIds) {
			Process process = new ProcessDao(database).getForId(processId);
			refIds.add(process.getRefId());
			switch (process.getRefId()) {
			case "e25c672d-f31f-4420-a564-274b762b5a3d":
			case "ca6d3914-a049-4750-9b02-93e1418d7fe6":
			case "3043dfd7-ab00-4d29-84ea-fc7471f48600":
				continue;
			default:
				fail("Unknown process");
			}
		}
		assertEquals(processIds.size(), refIds.size());
	}

	private void processLinks(List<ProcessLink> links) {
		assertEquals(3, links.size());
		Set<String> refIds = new HashSet<>();
		for (ProcessLink link : links) {
			Flow flow = new FlowDao(database).getForId(link.flowId);
			Process process = new ProcessDao(database).getForId(link.processId);
			Process provider = new ProcessDao(database).getForId(link.providerId);
			Exchange exchange = null;
			for (Exchange ex : process.getExchanges()) {
				if (ex.getId() != link.exchangeId)
					continue;
				exchange = ex;
				break;
			}
			refIds.add(flow.getRefId());
			switch (flow.getRefId()) {
			case "81c33501-1706-4a3a-8a9e-5af4a3d064a9":
				processLink(process, exchange, provider, flow);
				break;
			case "229351c0-5289-4744-82ed-72ce017d135b":
				processLink2(process, exchange, provider, flow);
				break;
			case "4ca7a7d1-845e-4e42-b565-c45de118fafe":
				processLink3(process, exchange, provider, flow);
				break;
			default:
				fail("Unknown process link");
			}
		}
		assertEquals(links.size(), refIds.size());
	}

	private void processLink(Process process, Exchange exchange, Process provider, Flow flow) {
		assertNotNull(exchange);
		refId("81c33501-1706-4a3a-8a9e-5af4a3d064a9", flow);
		refId("81c33501-1706-4a3a-8a9e-5af4a3d064a9", exchange.flow);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", process);
		refId("ca6d3914-a049-4750-9b02-93e1418d7fe6", provider);
		assertEquals(7, exchange.internalId);
	}

	private void processLink2(Process process, Exchange exchange, Process provider, Flow flow) {
		assertNotNull(exchange);
		refId("229351c0-5289-4744-82ed-72ce017d135b", flow);
		refId("229351c0-5289-4744-82ed-72ce017d135b", exchange.flow);
		refId("3043dfd7-ab00-4d29-84ea-fc7471f48600", process);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", provider);
		assertEquals(3, exchange.internalId);
	}

	private void processLink3(Process process, Exchange exchange, Process provider, Flow flow) {
		assertNotNull(exchange);
		refId("4ca7a7d1-845e-4e42-b565-c45de118fafe", flow);
		refId("4ca7a7d1-845e-4e42-b565-c45de118fafe", exchange.flow);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", process);
		refId("3043dfd7-ab00-4d29-84ea-fc7471f48600", provider);
		assertEquals(8, exchange.internalId);
	}

	private void parameterRedefs(List<ParameterRedef> redefs) {
		assertEquals(6, redefs.size());
		unique(redefs, redef -> redef.name);
		for (ParameterRedef redef : redefs) {
			switch (redef.name) {
			case "Input_Parameter":
				parameterRedef(redef);
				break;
			case "p_1":
				parameterRedef2(redef);
				break;
			case "p_2":
				parameterRedef3(redef);
				break;
			case "p_3":
				parameterRedef4(redef);
				break;
			case "p_4":
				parameterRedef5(redef);
				break;
			case "p_5":
				parameterRedef6(redef);
				break;
			default:
				fail("Unexpected parameter redef");
			}
		}
	}

	private void parameterRedef(ParameterRedef redef) {
		assertEquals("Input_Parameter", redef.name);
		assertNull(redef.contextType);
		assertNull(redef.contextId);
		assertNull(redef.uncertainty);
		assertEquals(2d, redef.value, 0);
	}

	private void parameterRedef2(ParameterRedef redef) {
		assertEquals("p_1", redef.name);
		assertEquals(ModelType.PROCESS, redef.contextType);
		Process processRef1 = new ProcessDao(database).getForId(redef.contextId);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", processRef1);
		assertEquals(6d, redef.value, 0);
		assertNotNull(redef.uncertainty);
		assertEquals(UncertaintyType.LOG_NORMAL, redef.uncertainty.distributionType);
		assertEquals(6d, redef.uncertainty.parameter1, 0);
		assertEquals(1d, redef.uncertainty.parameter2, 0);
		assertNull(redef.uncertainty.parameter3);
	}

	private void parameterRedef3(ParameterRedef redef) {
		assertEquals("p_2", redef.name);
		assertEquals(ModelType.PROCESS, redef.contextType);
		Process processRef2 = new ProcessDao(database).getForId(redef.contextId);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", processRef2);
		assertEquals(7d, redef.value, 0);
		assertNotNull(redef.uncertainty);
		assertEquals(UncertaintyType.NORMAL, redef.uncertainty.distributionType);
		assertEquals(7d, redef.uncertainty.parameter1, 0);
		assertEquals(1d, redef.uncertainty.parameter2, 0);
		assertNull(redef.uncertainty.parameter3);
	}

	private void parameterRedef4(ParameterRedef redef) {
		assertEquals("p_3", redef.name);
		assertEquals(ModelType.PROCESS, redef.contextType);
		Process processRef3 = new ProcessDao(database).getForId(redef.contextId);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", processRef3);
		assertEquals(8d, redef.value, 0);
		assertNotNull(redef.uncertainty);
		assertEquals(UncertaintyType.TRIANGLE, redef.uncertainty.distributionType);
		assertEquals(6d, redef.uncertainty.parameter1, 0);
		assertEquals(8d, redef.uncertainty.parameter2, 0);
		assertEquals(9d, redef.uncertainty.parameter3, 0);
	}

	private void parameterRedef5(ParameterRedef redef) {
		assertEquals("p_4", redef.name);
		assertEquals(ModelType.PROCESS, redef.contextType);
		Process processRef4 = new ProcessDao(database).getForId(redef.contextId);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", processRef4);
		assertEquals(9d, redef.value, 0);
		assertNotNull(redef.uncertainty);
		assertEquals(UncertaintyType.UNIFORM, redef.uncertainty.distributionType);
		assertEquals(4d, redef.uncertainty.parameter1, 0);
		assertEquals(10d, redef.uncertainty.parameter2, 0);
		assertNull(redef.uncertainty.parameter3);
	}

	private void parameterRedef6(ParameterRedef redef) {
		assertEquals("p_5", redef.name);
		assertEquals(ModelType.PROCESS, redef.contextType);
		Process processRef5 = new ProcessDao(database).getForId(redef.contextId);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", processRef5);
		assertEquals(10d, redef.value, 0);
		assertNull(redef.uncertainty);
	}

	private void projects() {
		List<Project> projects = new ProjectDao(database).getAll();
		assertEquals(1, projects.size());
		Project project = projects.get(0);
		assertEquals("cb629a7d-b7df-478e-a368-cbd25c0cfebb", project.getRefId());
		assertEquals(1543235240827l, project.getLastChange());
		assertEquals(2, project.getVersion());
		assertNull(project.getCategory());
		assertEquals("Project", project.getName());
		assertEquals("A project", project.getDescription());
		ImpactMethod method = new ImpactMethodDao(database).getForId(project.impactMethodId);
		refId("eedd7332-f522-40e6-ae77-9a0083b1b881", method);
		refId("5aa4c338-d52a-44a6-bb6b-e815e295e6c9", method.nwSets.get(0));
		assertEquals(method.nwSets.get(0).getId(), (long) project.nwSetId);
		assertEquals(1, project.variants.size());
		projectVariant(project.variants.get(0));
	}

	private void projectVariant(ProjectVariant variant) {
		assertEquals("Option1", variant.name);
		refId("f05fe9b1-9892-4436-be0e-7c92172c5298", variant.productSystem);
		assertEquals(AllocationMethod.CAUSAL, variant.allocationMethod);
		assertEquals(2d, variant.amount, 0);
		refId("70a1c611-c314-45ef-8fe0-7129f401df6f", variant.flowPropertyFactor.getFlowProperty());
		refId("47f37b03-1d2c-4460-8b80-5bd0ca519f4e", variant.unit);
		parameterRedefs2(variant.parameterRedefs);
	}

	private void parameterRedefs2(List<ParameterRedef> redefs) {
		assertEquals(3, redefs.size());
		unique(redefs, redef -> redef.name);
		for (ParameterRedef redef : redefs) {
			switch (redef.name) {
			case "Input_Parameter":
				parameterRedef7(redef);
				break;
			case "p_1":
				parameterRedef8(redef);
				break;
			case "p_2":
				parameterRedef9(redef);
				break;
			default:
				fail("Unexpected parameter redef");
			}
		}
	}

	private void parameterRedef7(ParameterRedef redef) {
		assertEquals("Input_Parameter", redef.name);
		assertNull(redef.contextType);
		assertNull(redef.contextId);
		assertEquals(2d, redef.value, 0);
	}

	private void parameterRedef8(ParameterRedef redef) {
		assertEquals("p_1", redef.name);
		assertEquals(ModelType.PROCESS, redef.contextType);
		Process processRef = new ProcessDao(database).getForId(redef.contextId);
		refId("e25c672d-f31f-4420-a564-274b762b5a3d", processRef);
		assertEquals(6d, redef.value, 0);
	}

	private void parameterRedef9(ParameterRedef redef) {
		assertEquals("p_2", redef.name);
		assertEquals(ModelType.IMPACT_METHOD, redef.contextType);
		ImpactMethod methodRef = new ImpactMethodDao(database).getForId(redef.contextId);
		refId("eedd7332-f522-40e6-ae77-9a0083b1b881", methodRef);
		assertEquals(3d, redef.value, 0);
	}

	private void fileLength(int expected, CategorizedEntity entity, String filename) throws IOException {
		File root = database.getFileStorageLocation();
		FileStore fs = new FileStore(root);
		File folder = fs.getFolder(entity);
		File file = new File(folder, filename);
		if (!file.exists()) {
			fail("File " + filename + " does not exist");
			return;
		}
		assertEquals(expected, Files.readAllBytes(file.toPath()).length);
	}

	private void unique(List<? extends RootEntity> list) {
		unique(list, element -> element.getRefId());
	}

	private <T, V> void unique(List<T> list, Function<T, V> converter) {
		Set<V> ids = new HashSet<>();
		list.forEach(element -> ids.add(converter.apply(element)));
		assertEquals(list.size(), ids.size());
	}

	private void refId(String expected, RootEntity entity) {
		assertNotNull(entity);
		assertEquals(expected, entity.getRefId());
	}

}
