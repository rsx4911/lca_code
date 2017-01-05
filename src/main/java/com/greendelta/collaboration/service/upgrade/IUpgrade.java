package com.greendelta.collaboration.service.upgrade;

import com.google.gson.JsonObject;
import com.greendelta.collaboration.index.DatasetIndex;
import com.greendelta.collaboration.index.DatasetIndexEntry;
import com.greendelta.collaboration.service.Repository;

public interface IUpgrade {

	String fromSchema();

	String toSchema();

	void run(Repository repo, DatasetIndex dsIndex, GetJson getJson, PutJson putJson);

	public static interface GetJson {

		JsonObject apply(Repository repo, DatasetIndexEntry entry);

	}

	public static interface PutJson {

		void apply(Repository repo, DatasetIndexEntry entry, JsonObject json);

	}
}
