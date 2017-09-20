package com.greendelta.collaboration.search.elasticsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

import com.greendelta.collaboration.search.SearchClient;
import com.greendelta.collaboration.search.SearchQuery;
import com.greendelta.collaboration.search.SearchResult;

public class EsClient implements SearchClient {

	private static final String INDEX_NAME = "datasets";
	private static final String INDEX_TYPE = "datasets";

	private final Client client;
	private final String settings;
	private final String mapping;

	public EsClient(Client client, String settings, String mapping) {
		this.client = client;
		this.settings = settings;
		this.mapping = mapping;
	}

	@Override
	public SearchResult search(SearchQuery searchQuery) {
		return EsSearch.search(searchQuery, client, INDEX_NAME);
	}

	@Override
	public void initialize() {
		boolean exists = client.admin().indices().prepareExists(INDEX_NAME).execute().actionGet().isExists();
		if (exists)
			return;
		CreateIndexRequest createRequest = new CreateIndexRequest(INDEX_NAME).settings(Settings.builder()
				.loadFromSource(settings, XContentType.JSON).put("number_of_shards", 1));
		client.admin().indices().create(createRequest).actionGet();
		PutMappingRequest mappingRequest = Requests.putMappingRequest(INDEX_NAME).type(INDEX_TYPE)
				.source(mapping, XContentType.JSON);
		client.admin().indices().putMapping(mappingRequest).actionGet();
	}

	@Override
	public void index(String id, Map<String, Object> content) {
		IndexRequest request = client.prepareIndex(INDEX_NAME, INDEX_TYPE, id).setOpType(OpType.INDEX)
				.setSource(content).request();
		client.index(request).actionGet();
	}

	@Override
	public void remove(String id) {
		client.prepareDelete(INDEX_NAME, INDEX_TYPE, id).execute().actionGet();
	}

	@Override
	public Map<String, Object> get(String id) {
		GetResponse response = client.prepareGet(INDEX_NAME, INDEX_TYPE, id).execute().actionGet();
		if (response == null)
			return null;
		Map<String, Object> source = response.getSource();
		if (source == null || source.isEmpty())
			return null;
		return source;
	}

	@Override
	public List<Map<String, Object>> get(Set<String> ids) {
		MultiGetResponse response = client.prepareMultiGet().add(INDEX_NAME, INDEX_TYPE, ids).execute().actionGet();
		if (response == null)
			return null;
		List<Map<String, Object>> results = new ArrayList<>();
		Iterator<MultiGetItemResponse> it = response.iterator();
		while (it.hasNext()) {
			GetResponse resp = it.next().getResponse();
			if (resp == null)
				continue;
			Map<String, Object> source = resp.getSource();
			if (source == null || source.isEmpty())
				continue;
			results.add(source);
		}
		return results;
	}

	@Override
	public void clear() {
		DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
		client.admin().indices().delete(request).actionGet();
	}

}
