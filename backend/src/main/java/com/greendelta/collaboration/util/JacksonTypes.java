package com.greendelta.collaboration.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.greendelta.collaboration.model.Role;

public interface JacksonTypes {

	TypeReference<List<String>> STRING_LIST = new TypeReference<List<String>>() {
	};

	TypeReference<Map<String, Object>> OBJECT_MAP = new TypeReference<Map<String, Object>>() {
	};

	TypeReference<Map<String, Role>> ROLE_MAP = new TypeReference<Map<String, Role>>() {
	};

}
