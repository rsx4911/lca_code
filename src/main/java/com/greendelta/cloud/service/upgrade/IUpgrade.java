package com.greendelta.cloud.service.upgrade;

import com.google.gson.JsonObject;
import com.greendelta.cloud.index.DatasetIndex;
import com.greendelta.cloud.index.DatasetIndexEntry;
import com.greendelta.cloud.service.Repository;

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
