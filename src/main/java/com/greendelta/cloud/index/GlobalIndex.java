package com.greendelta.cloud.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.openlca.core.model.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.greendelta.cloud.service.PagedResult;

public class GlobalIndex {

	private final static Logger log = LoggerFactory.getLogger(GlobalIndex.class);

	public static PagedResult<DatasetIndexEntry> search(List<DatasetIndex> indices, int page, String filter,
			ModelType type) {
		if (indices.size() == 0)
			return new PagedResult<>(page, filter, 0, 0, Collections.emptyList());
		IndexReader[] readers = new IndexReader[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			DatasetIndex index = indices.get(i);
			readers[i] = IndexUtil.getReader(index.directory);
		}
		try {
			IndexSearcher searcher = new IndexSearcher(new MultiReader(readers));
			Term term = null;
			if (Strings.isNullOrEmpty(filter))
				term = new Term("fullPath", "*");
			else
				term = new Term("fullPath", "*" + filter.toLowerCase() + "*");
			Query query = new WildcardQuery(term);
			if (type != null)
				query = IndexUtil.andQuery(query, new TermQuery(new Term("type", type.name())));
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			if (topDocs.totalHits == 0)
				return new PagedResult<>(page, filter, 0, 0, Collections.emptyList());
			List<DatasetIndexEntry> entries = new ArrayList<>();
			int start = (page - 1) * 10;
			int end = start + 10;
			if (end > topDocs.totalHits)
				end = topDocs.totalHits;
			for (int i = start; i < end; i++) {
				int docId = topDocs.scoreDocs[i].doc;
				DatasetIndexEntry entry = ConversionUtil.convert(searcher.doc(docId));
				entries.add(entry);
			}
			return new PagedResult<>(page, filter, topDocs.totalHits, entries.size(), entries);
		} catch (IOException e) {
			log.error("Error during global index search", e);
			return new PagedResult<>(page, filter, 0, 0, Collections.emptyList());
		}
	}

}
