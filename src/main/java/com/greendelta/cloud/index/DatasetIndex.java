package com.greendelta.cloud.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.openlca.cloud.model.data.Commit;
import org.openlca.cloud.model.data.Dataset;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class DatasetIndex {

	private final static Logger log = LoggerFactory
			.getLogger(DatasetIndex.class);
	final Directory directory;

	public DatasetIndex(File indexDirectory) {
		Directory directory = null;
		try {
			directory = FSDirectory.open(indexDirectory.toPath());
		} catch (IOException e) {
			log.error("Error creating dataset indexer", e);
		}
		this.directory = directory;
	}

	public void index(List<Dataset> datasets, Commit commit) {
		delete(toIds(datasets));
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (Dataset dataset : datasets)
				writer.addDocument(convert(dataset, commit));
			writer.close();
		} catch (IOException e) {
			log.error("Error indexing dataset identifiers", e);
		}
	}

	public void delete(String refId) {
		delete(Collections.singleton(refId));
	}

	public void delete(Set<String> refIds) {
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (String refId : refIds) {
				Term term = new Term("refId", refId);
				Query query = new TermQuery(term);
				writer.deleteDocuments(query);
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error deleting dataset identifier indices", e);
		}
	}

	public DatasetIndexEntry getForId(ModelType type, String refId) {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return null;
		try {
			Term term = new Term("refId", refId);
			Query query = new TermQuery(term);
			TopDocs topDocs = searcher.search(query, 1);
			if (topDocs.totalHits == 0)
				return null;
			return convert(searcher.doc(topDocs.scoreDocs[0].doc));
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return null;
		}
	}

	public List<DatasetIndexEntry> getForModelType(ModelType type, String nameFilter) {
		List<DatasetIndexEntry> entries = new ArrayList<>();
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return Collections.emptyList();
		try {
			Query squery1 = IndexUtil.andQuery(new Term("categoryRefId", ""), new Term("type", type.name()));
			Query squery2 = IndexUtil.andQuery(new Term("categoryRefId", ""), new Term("categoryType", type.name()));
			Query squery = IndexUtil.orQuery(squery1, squery2);
			Query query = null;
			if (Strings.isNullOrEmpty(nameFilter))
				query = squery;
			else {
				WildcardQuery nameQuery = new WildcardQuery(new Term("name", "*" + nameFilter.toLowerCase() + "*"));
				query = IndexUtil.andQuery(squery, nameQuery);
			}
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return Collections.emptyList();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = convert(searcher.doc(doc.doc));
				entries.add(entry);
			}
			return entries;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return Collections.emptyList();
		}
	}

	public List<DatasetIndexEntry> getForCategory(String categoryId, String nameFilter) {
		List<DatasetIndexEntry> entries = new ArrayList<>();
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return Collections.emptyList();
		try {
			if (categoryId == null)
				categoryId = "";
			Query query = null;
			TermQuery categoryQuery = new TermQuery(new Term("categoryRefId", categoryId));
			if (Strings.isNullOrEmpty(nameFilter))
				query = categoryQuery;
			else {
				WildcardQuery nameQuery = new WildcardQuery(new Term("name", "*" + nameFilter.toLowerCase() + "*"));
				query = IndexUtil.andQuery(categoryQuery, nameQuery);
			}
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return Collections.emptyList();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = convert(searcher.doc(doc.doc));
				entries.add(entry);
			}
			return entries;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return Collections.emptyList();
		}
	}

	public boolean categoryExists(String categoryId) {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return false;
		if (categoryId == null)
			categoryId = "";
		try {
			Query query = IndexUtil
					.andQuery(new Term("refId", categoryId), new Term("type", ModelType.CATEGORY.name()));
			TopDocs topDocs = searcher.search(query, 1);
			return topDocs.totalHits != 0;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return false;
		}
	}

	public List<DatasetIndexEntry> getAll() {
		List<DatasetIndexEntry> entries = new ArrayList<>();
		IndexReader reader = IndexUtil.getReader(directory);
		if (reader == null)
			return Collections.emptyList();
		try {
			for (int i = 0; i < reader.maxDoc(); i++)
				entries.add(convert(reader.document(i)));
			reader.close();
			return entries;
		} catch (IOException e) {
			log.error("Error retrieving all dataset identifiers", e);
			return Collections.emptyList();
		}
	}

	private Set<String> toIds(List<Dataset> datasets) {
		Set<String> ids = new HashSet<>();
		for (Dataset dataset : datasets)
			ids.add(dataset.refId);
		return ids;
	}

	private DatasetIndexEntry convert(Document document) {
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
		return new DatasetIndexEntry(type, refId, name, categoryType,
				categoryRefId, commitId, commitMessage, fullPath, lastUpdate);
	}

	private Document convert(Dataset dataset, Commit commit) {
		Document document = new Document();
		IndexUtil.addField(document, "refId", dataset.refId);
		IndexUtil.addField(document, "type", dataset.type.name());
		IndexUtil.addField(document, "name", dataset.name);
		IndexUtil.addField(document, "categoryRefId", dataset.categoryRefId);
		IndexUtil.addField(document, "categoryType", dataset.categoryType);
		IndexUtil.addField(document, "commitId", commit.id);
		IndexUtil.addField(document, "commitMessage", commit.message);
		IndexUtil.addField(document, "fullPath", dataset.fullPath);
		IndexUtil.addField(document, "lastUpdate", commit.timestamp);
		return document;
	}

}
