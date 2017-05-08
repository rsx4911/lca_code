package com.greendelta.collaboration.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
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
import org.openlca.util.KeyGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.greendelta.collaboration.service.HistoryService;
import com.greendelta.collaboration.service.Repository;

public class DatasetIndex {

	private final static Logger log = LoggerFactory.getLogger(DatasetIndex.class);
	private HistoryService historyService;
	final Directory directory;
	final Repository repo;
	final File indexDir;

	public DatasetIndex(Repository repo, File indexDirectory) {
		this.indexDir = indexDirectory;
		Directory directory = null;
		try {
			directory = FSDirectory.open(indexDirectory.toPath());
		} catch (IOException e) {
			log.error("Error creating dataset indexer", e);
		}
		this.directory = directory;
		this.repo = repo;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
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

	public void clone(DatasetIndex index, List<Commit> commits) {
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (Commit commit : commits) {
				List<DatasetIndexEntry> entries = index.getAll(commit.id);
				for (DatasetIndexEntry entry : entries) {
					writer.addDocument(ConversionUtil.convert(entry, repo.toId()));
				}
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error indexing dataset identifiers", e);
		}
	}

	public List<DatasetIndexEntry> getAll() {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return new ArrayList<>();
		List<DatasetIndexEntry> all = new ArrayList<>();
		try {
			MatchAllDocsQuery query = new MatchAllDocsQuery();
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return new ArrayList<>();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				all.add(entry);
			}
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
		}
		return all;
	}

	private List<DatasetIndexEntry> getAll(String commitId) {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return new ArrayList<>();
		List<DatasetIndexEntry> all = new ArrayList<>();
		try {
			Query query = new TermQuery(new Term("commitId", commitId));
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return new ArrayList<>();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				all.add(entry);
			}
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
		}
		return all;
	}

	public void updateCategoryRefIds() {
		List<DatasetIndexEntry> entries = getAll(ModelType.CATEGORY);
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (DatasetIndexEntry e : entries) {
				String newRefId = KeyGen.get((e.categoryType.name() + "/" + e.fullPath).split("/"));
				if (e.refId.equals(newRefId))
					continue;
				DatasetIndexEntry updated = new DatasetIndexEntry(e.type, newRefId, e.name, e.categoryType,
						e.categoryRefId, e.commitId, e.commitMessage, e.fullPath, e.lastUpdate, e.repositoryId);
				writer.updateDocument(new Term("refId", e.refId), ConversionUtil.convert(updated));
				List<DatasetIndexEntry> elements = getForCategory(e.refId, null);
				for (DatasetIndexEntry el : elements) {
					updated = new DatasetIndexEntry(el.type, el.refId, el.name, el.categoryType, newRefId, el.commitId,
							el.commitMessage, el.fullPath, el.lastUpdate, el.repositoryId);
					writer.updateDocument(new Term("refId", el.refId), ConversionUtil.convert(updated));
				}
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error indexing dataset identifiers", e);
		}
	}

	public DatasetIndexEntry getForId(String refId, String commitId) {
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return null;
		try {
			Term term1 = new Term("refId", refId);
			Term term2 = new Term("commitId", commitId);
			Query query = IndexUtil.andQuery(term1, term2);
			TopDocs topDocs = searcher.search(query, 1);
			if (topDocs.totalHits == 0)
				return null;
			return ConversionUtil.convert(searcher.doc(topDocs.scoreDocs[0].doc));
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return null;
		}
	}

	public List<DatasetIndexEntry> getAll(ModelType type) {
		List<DatasetIndexEntry> entries = new ArrayList<>();
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return Collections.emptyList();
		try {
			Query query = new TermQuery(new Term("type", type.name()));
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return Collections.emptyList();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				entries.add(entry);
			}
			return entries;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return Collections.emptyList();
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
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (historyService == null || historyService.isLastCommit(entry))
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
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (historyService == null || historyService.isLastCommit(entry))
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
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return false;
			for (ScoreDoc doc : topDocs.scoreDocs) {
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(doc.doc));
				if (historyService == null || historyService.isLastCommit(entry))
					return true;
			}
			return false;
		} catch (IOException e) {
			log.error("Error retrieving dataset identifiers", e);
			return false;
		}
	}

	public void updateRepoId() {
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (DatasetIndexEntry e : getAll()) {
				writer.updateDocument(new Term("refId", e.refId), ConversionUtil.convert(e, repo.toId()));
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error indexing dataset identifiers", e);
		}
	}

	public void close() {
		try {
			directory.close();
		} catch (IOException e) {
			log.error("Error closing index directory", e);
		}
	}

}
