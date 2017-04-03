package com.greendelta.collaboration.index;

import org.apache.lucene.document.Document;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.ModelType;

import com.google.common.base.Strings;
import com.greendelta.collaboration.service.Repository;

class ConversionUtil {

	static DatasetIndexEntry convert(Document document) {
		String refId = document.get("refId");
		ModelType type = ModelType.valueOf(document.get("type"));
		String name = document.get("name");
		String categoryRefId = document.get("categoryRefId");
		ModelType categoryType = null;
		if (!Strings.isNullOrEmpty(document.get("categoryType")))
			categoryType = ModelType.valueOf(document.get("categoryType"));
		String commitId = document.get("commitId");
		String commitMessage = document.get("commitMessage");
		String fullPath = document.get("fullPath");
		long lastUpdate = Long.parseLong(document.get("lastUpdate"));
		String repositoryId = document.get("repositoryId");
		return new DatasetIndexEntry(type, refId, name, categoryType,
				categoryRefId, commitId, commitMessage, fullPath, lastUpdate, repositoryId);
	}

	static Document convert(Repository repo, Dataset dataset, Commit commit) {
		Document document = new Document();
		IndexUtil.addField(document, "refId", dataset.refId);
		IndexUtil.addField(document, "commitId", commit.id);
		IndexUtil.addField(document, "type", dataset.type);
		IndexUtil.addField(document, "categoryRefId", dataset.categoryRefId);
		IndexUtil.addField(document, "categoryType", dataset.categoryType);
		IndexUtil.addField(document, "lastUpdate", commit.timestamp);
		IndexUtil.addField(document, "repositoryId", repo.toId());
		IndexUtil.addTextField(document, "fullPath", dataset.fullPath);
		IndexUtil.addTextField(document, "name", dataset.name);
		IndexUtil.addTextField(document, "commitMessage", commit.message);
		return document;
	}

	static Document convert(DatasetIndexEntry entry) {
		Document document = new Document();
		IndexUtil.addField(document, "refId", entry.refId);
		IndexUtil.addField(document, "commitId", entry.commitId);
		IndexUtil.addField(document, "type", entry.type);
		IndexUtil.addField(document, "categoryRefId", entry.categoryRefId);
		IndexUtil.addField(document, "categoryType", entry.categoryType);
		IndexUtil.addField(document, "lastUpdate", entry.lastUpdate);
		IndexUtil.addField(document, "repositoryId", entry.repositoryId);
		IndexUtil.addTextField(document, "fullPath", entry.fullPath);
		IndexUtil.addTextField(document, "name", entry.name);
		IndexUtil.addTextField(document, "commitMessage", entry.commitMessage);
		return document;
	}

}
