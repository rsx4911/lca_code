package com.greendelta.cloud.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.openlca.cloud.model.data.FileReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class IndexUtil {

	private final static Logger log = LoggerFactory.getLogger(IndexUtil.class);
	private final static Gson mapper = new Gson();

	public static void addField(Document document, String name, String value) {
		if (value == null)
			return;
		IndexableField field = new StringField(name, value, Store.YES);
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

	public static List<FileReference> parseFileReferences(String value)
			throws IOException {
		return mapper.fromJson(value, new TypeToken<List<FileReference>>() {
		}.getType());
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

}
