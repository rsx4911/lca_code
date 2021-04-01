package com.greendelta.collaboration.util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.greendelta.collaboration.model.Role;

public interface GsonTypes {

	Type STRING_LIST = new TypeToken<List<String>>() {
	}.getType();

	Type OBJECT_MAP = new TypeToken<Map<String, Object>>() {
	}.getType();

	Type ROLE_MAP = new TypeToken<Map<String, Role>>() {
	}.getType();

}
