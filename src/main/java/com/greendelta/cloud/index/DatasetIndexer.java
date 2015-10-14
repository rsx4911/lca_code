package com.greendelta.cloud.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greendelta.cloud.model.data.DatasetDescriptor;

public class DatasetIndexer {

	private final static Logger log = LoggerFactory
			.getLogger(DatasetIndexer.class);
	private final Directory directory;

	public DatasetIndexer(File indexDirectory) {
		Directory directory = null;
		try {
			directory = FSDirectory.open(indexDirectory.toPath());
		} catch (IOException e) {
			log.error("Error creating dataset indexer", e);
		}
		this.directory = directory;
	}

	public void index(DatasetDescriptor descriptor) {
		index(Collections.singletonList(descriptor));
	}

	public void index(Collection<DatasetDescriptor> descriptors) {
		delete(getIds(descriptors));
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (DatasetDescriptor descriptor : descriptors)
				writer.addDocument(convert(descriptor));
			writer.close();
		} catch (IOException e) {
			log.error("Error indexing dataset descriptors", e);
		}
	}

	private void delete(List<String> refIds) {
		IndexWriter writer = IndexUtil.getWriter(directory, false);
		try {
			for (String refId : refIds) {
				Term term = new Term("refId", refId);
				Query query = new TermQuery(term);
				writer.deleteDocuments(query);
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error deleting dataset descriptors indices", e);
		}
	}

	public DatasetDescriptor get(ModelType type, String refId) {
		List<DatasetDescriptor> result = get(type,
				Collections.singletonList(refId));
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	public List<DatasetDescriptor> get(ModelType type, List<String> refIds) {
		List<DatasetDescriptor> descriptors = new ArrayList<>();
		IndexSearcher searcher = IndexUtil.getSearcher(directory);
		if (searcher == null)
			return Collections.emptyList();
		try {
			for (String refId : refIds) {
				Term term = new Term("refId", refId);
				Query query = new TermQuery(term);
				TopDocs topDocs = searcher.search(query, 1);
				if (topDocs.totalHits == 0)
					continue;
				Document document = searcher.doc(topDocs.scoreDocs[0].doc);
				descriptors.add(convert(document));
			}
			return descriptors;
		} catch (IOException e) {
			log.error("Error retrieving dataset descriptors", e);
			return Collections.emptyList();
		}
	}

	public List<DatasetDescriptor> getAll() {
		List<DatasetDescriptor> descriptors = new ArrayList<>();
		IndexReader reader = IndexUtil.getReader(directory);
		if (reader == null)
			return Collections.emptyList();
		try {
			for (int i = 0; i < reader.maxDoc(); i++)
				descriptors.add(convert(reader.document(i)));
			reader.close();
			return descriptors;
		} catch (IOException e) {
			log.error("Error retrieving all dataset descriptors", e);
			return Collections.emptyList();
		}
	}

	private List<String> getIds(Collection<DatasetDescriptor> descriptors) {
		List<String> ids = new ArrayList<>();
		for (DatasetDescriptor descriptor : descriptors)
			ids.add(descriptor.getRefId());
		return ids;
	}

	private DatasetDescriptor convert(Document document) {
		DatasetDescriptor descriptor = new DatasetDescriptor();
		descriptor.setRefId(document.get("refId"));
		descriptor.setType(ModelType.valueOf(document.get("type")));
		descriptor.setLastChange(Long.parseLong(document.get("lastChange")));
		descriptor.setVersion(document.get("version"));
		descriptor.setName(document.get("name"));
		descriptor.setCategoryRefId(document.get("categoryRefId"));
		String categoryType = document.get("categoryType");
		if (categoryType != null && !categoryType.isEmpty())
			descriptor.setCategoryType(ModelType.valueOf(categoryType));
		descriptor.setFullPath(document.get("fullPath"));
		return descriptor;
	}

	private Document convert(DatasetDescriptor descriptor) {
		Document document = new Document();
		IndexUtil.addField(document, "refId", descriptor.getRefId());
		IndexUtil.addField(document, "type", descriptor.getType().name());
		IndexUtil.addField(document, "lastChange", descriptor.getLastChange());
		IndexUtil.addField(document, "version", descriptor.getVersion());
		IndexUtil.addField(document, "name", descriptor.getName());
		IndexUtil.addField(document, "categoryRefId",
				descriptor.getCategoryRefId());
		if (descriptor.getCategoryType() != null)
			IndexUtil.addField(document, "categoryType", descriptor
					.getCategoryType().name());
		IndexUtil.addField(document, "fullPath", descriptor.getFullPath());
		return document;
	}
}
