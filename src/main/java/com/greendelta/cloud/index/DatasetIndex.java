package com.greendelta.cloud.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
import com.greendelta.cloud.service.Repository;

public class DatasetIndex {

	private final static Logger log = LoggerFactory.getLogger(DatasetIndex.class);
	final Directory directory;
	final Repository repo;

	public DatasetIndex(Repository repo, File indexDirectory) {
		Directory directory = null;
		try {
			directory = FSDirectory.open(indexDirectory.toPath());
		} catch (IOException e) {
			log.error("Error creating dataset indexer", e);
		}
		this.directory = directory;
		this.repo = repo;
	}

	public void index(List<Dataset> datasets, Commit commit) {
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (Dataset dataset : datasets)
				writer.addDocument(ConversionUtil.convert(repo, dataset, commit));
			writer.close();
		} catch (IOException e) {
			log.error("Error indexing dataset identifiers", e);
		}
	}

	public DatasetIndexEntry getForId(ModelType type, String refId, Function<DatasetIndexEntry, Boolean> isLatestEntry) {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return null;
		try {
			Term term = new Term("refId", refId);
			Query query = new TermQuery(term);
			TopDocs topDocs = searcher.search(query, 1);
			if (topDocs.totalHits == 0)
				return null;
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (isLatestEntry.apply(entry))
					return entry;
			}
			return null;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return null;
		}
	}

	public List<DatasetIndexEntry> getForModelType(ModelType type, String nameFilter,
			Function<DatasetIndexEntry, Boolean> isLatestEntry) {
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
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (isLatestEntry.apply(entry))
					entries.add(entry);
			}
			return entries;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return Collections.emptyList();
		}
	}

	public List<DatasetIndexEntry> getForCategory(String categoryId, String nameFilter,
			Function<DatasetIndexEntry, Boolean> isLatestEntry) {
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
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (isLatestEntry.apply(entry))
					entries.add(entry);
			}
			return entries;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return Collections.emptyList();
		}
	}

	public boolean categoryExists(String categoryId, Function<DatasetIndexEntry, Boolean> isLatestEntry) {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return false;
		if (categoryId == null)
			categoryId = "";
		try {
			Query query = IndexUtil
					.andQuery(new Term("refId", categoryId), new Term("type", ModelType.CATEGORY.name()));
			TopDocs topDocs = searcher.search(query, 1);
			if (topDocs.totalHits == 0)
				return false;
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (isLatestEntry.apply(entry))
					return true;
			}
			return false;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return false;
		}
	}

}
