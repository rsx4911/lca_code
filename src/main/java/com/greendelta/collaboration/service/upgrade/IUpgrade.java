package com.greendelta.collaboration.service.upgrade;

import com.google.gson.JsonObject;
import com.greendelta.collaboration.model.index.IndexEntry;
import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.service.SearchService;

public interface IUpgrade {

	String fromSchema();

	String toSchema();

	void run(Repository repo, SearchService searchService, GetJson getJson, PutJson putJson);

	public static interface GetJson {

		JsonObject apply(Repository repo, IndexEntry entry);

	}

	public static interface PutJson {

		void apply(Repository repo, IndexEntry entry, JsonObject json);

	}
}
