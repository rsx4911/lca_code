package com.greendelta.collaboration.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;

class IndexUtil {

	private final static Logger log = LoggerFactory.getLogger(IndexUtil.class);
	private final static Gson mapper = new Gson();

	public static void addTextField(Document document, String name, String value) {
		if (value == null)
			value = "";
		IndexableField field = new TextField(name, value, Store.YES);
		document.add(field);
	}

	public static void addField(Document document, String name, String value) {
		if (value == null)
			value = "";
		IndexableField field = new StringField(name, value, Store.YES);
		document.add(field);
	}

	public static void addField(Document document, String name, Enum<?> value) {
		String sValue = null;
		if (value == null)
			sValue = "";
		else
			sValue = value.name();
		IndexableField field = new StringField(name, sValue, Store.YES);
		document.add(field);
	}

	public static void addField(Document document, String name, long value) {
		IndexableField field = new LongField(name, value, Store.YES);
		document.add(field);
	}

	public static void addField(Document document, String name, List<?> value) {
		if (value == null)
			return;
		String json = mapper.toJson(value);
		addField(document, name, json);
	}

	public static Query andQuery(Term term1, Term term2) {
		Query query1 = new TermQuery(term1);
		Query query2 = new TermQuery(term2);
		return andQuery(query1, query2);
	}

	public static Query andQuery(Query query1, Query query2) {
		BooleanQuery query = new BooleanQuery();
		query.add(query1, BooleanClause.Occur.MUST);
		query.add(query2, BooleanClause.Occur.MUST);
		return query;
	}

	public static Query andNotQuery(Query query1, Query query2) {
		BooleanQuery query = new BooleanQuery();
		query.add(query1, BooleanClause.Occur.MUST);
		query.add(query2, BooleanClause.Occur.MUST_NOT);
		return query;
	}
	
	public static Query orQuery(Term term1, Term term2) {
		Query query1 = new TermQuery(term1);
		Query query2 = new TermQuery(term2);
		return orQuery(query1, query2);
	}

	public static Query orQuery(Query query1, Query query2) {
		BooleanQuery query = new BooleanQuery();
		query.add(query1, BooleanClause.Occur.SHOULD);
		query.add(query2, BooleanClause.Occur.SHOULD);
		return query;
	}

	public static Query wildcardQuery(String field, String filter) {
		Term term = null;
		if (Strings.isNullOrEmpty(filter))
			term = new Term(field, "*");
		else
			term = new Term(field, "*" + filter.toLowerCase() + "*");
		return new WildcardQuery(term);
	}

	public static IndexWriter getWriter(Directory directory, boolean create) {
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		config.setCommitOnClose(true);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try {
			return new IndexWriter(directory, config);
		} catch (IOException e) {
			log.error("Error creating commit index writer", e);
			return null;
		}
	}

	public static IndexReader getReader(Directory directory) {
		try {
			return DirectoryReader.open(directory);
		} catch (IOException e) {
			if (!(e instanceof IndexNotFoundException))
				log.error("Error creating commit index reader", e);
			return null;
		}
	}

	public static IndexSearcher getSearcher(Directory directory) {
		IndexReader reader = getReader(directory);
		if (reader == null)
			return null;
		return new IndexSearcher(reader);
	}

}
