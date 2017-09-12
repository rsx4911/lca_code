package com.greendelta.collaboration.search.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.StopWatch;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.greendelta.collaboration.search.SearchClient;
import com.greendelta.collaboration.search.SearchResult;
import com.greendelta.collaboration.search.SearchResult.ResultInfo;

class EsClient implements SearchClient<EsIndex, EsQuery> {

	@Inject
	private Client client;

	@Override
	public SearchResult search(EsIndex index, EsQuery searchQuery) {
		try {
			SearchRequestBuilder request = client.prepareSearch(index.name);
			setupPaging(request, searchQuery);
			setupSorting(request, searchQuery);
			setupQuery(request, searchQuery);
			return search(request, searchQuery);
		} catch (Exception e) {
			Logger log = LoggerFactory.getLogger(getClass());
			log.error("Error searching index " + index.name, e);
			return new EsResult(searchQuery);
		}
	}

	private void setupPaging(SearchRequestBuilder request, EsQuery searchQuery) {
		int start = searchQuery.getPage() * searchQuery.getPageSize();
		if (start > 0)
			request.setFrom(start);
		if (searchQuery.getPageSize() > 0)
			request.setSize(searchQuery.getPageSize());
		else
			request.setSize(EsQuery.MAX_PAGE_SIZE);
	}

	private void setupSorting(SearchRequestBuilder request, EsQuery searchQuery) {
		for (Entry<String, SortOrder> entry : searchQuery.getSortBy().entrySet())
			request.addSort(entry.getKey(), entry.getValue());
	}

	private void setupQuery(SearchRequestBuilder request, EsQuery searchQuery) {
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		setupParameters(query, searchQuery.getParameters());
		setupAggregations(request, searchQuery, query);
		if (query.hasClauses())
			request.setQuery(query);
		else
			request.setQuery(QueryBuilders.matchAllQuery());
	}

	private void setupParameters(BoolQueryBuilder query, List<EsParameter> parameters) {
		for (EsParameter parameter : parameters) {
			BoolQueryBuilder q = parameter.toQuery();
			if (q == null)
				continue;
			query.must(q);
		}
	}

	private void setupAggregations(SearchRequestBuilder request, EsQuery searchQuery, BoolQueryBuilder query) {
		Map<String, EsAggregation> aggregations = new HashMap<>();
		for (EsAggregation aggregation : searchQuery.getAggregations()) {
			request.addAggregation(aggregation.getAggregation());
			aggregations.put(aggregation.getName().toLowerCase(), aggregation);
		}
		for (EsParameter filter : searchQuery.getFilters()) {
			EsAggregation facet = aggregations.get(filter.name.toLowerCase());
			if (facet == null)
				continue;
			BoolQueryBuilder q = filter.toQuery(facet);
			if (q != null)
				query.must(q);
		}
	}

	private EsResult search(SearchRequestBuilder request, EsQuery searchQuery) {
		StopWatch watch = new StopWatch();
		watch.start();
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		EsResult result = new EsResult(searchQuery);
		for (SearchHit hit : hits)
			result.data.add(hit.getSource());
		result.aggregations.addAll(response.getAggregations().asList());
		long totalHits = response.getHits().getTotalHits();
		watch.stop();
		long searchTime = watch.totalTime().getMillis();
		extendResultInfo(result.resultInfo, totalHits, searchTime, searchQuery);
		return result;
	}

	private void extendResultInfo(ResultInfo info, long totalHits, long searchTime, EsQuery searchQuery) {
		info.totalCount = totalHits;
		info.searchMillis = searchTime;
		info.currentPage = searchQuery.getPage();
		info.pageSize = searchQuery.getPageSize();
		long totalCount = info.totalCount;
		if (searchQuery.getPageSize() != 0) {
			int pageCount = (int) totalCount / searchQuery.getPageSize();
			if ((totalCount % searchQuery.getPageSize()) != 0)
				pageCount = 1 + pageCount;
			info.pageCount = pageCount;
		}
	}

	@Override
	public void initialize(EsIndex index) {
		CreateIndexRequest createRequest = new CreateIndexRequest(index.name).settings(Settings.builder()
				.loadFromSource(index.data.get("settings"), XContentType.JSON).put("number_of_shards", 1));
		client.admin().indices().create(createRequest).actionGet();
		PutMappingRequest mappingRequest = Requests.putMappingRequest(index.name).type(index.type)
				.source(index.data.get("mapping"), XContentType.JSON);
		client.admin().indices().putMapping(mappingRequest).actionGet();
	}

	@Override
	public void index(String id, Map<String, Object> content, EsIndex index) {
		IndexRequest request = client.prepareIndex(index.name, index.type, id).setOpType(OpType.INDEX)
				.setSource(content).request();
		client.index(request).actionGet();
	}

	@Override
	public void remove(String id, EsIndex index) {
		client.prepareDelete(index.name, index.type, id).execute().actionGet();
	}

	@Override
	public Map<String, Object> get(String id, EsIndex index) {
		GetResponse response = client.prepareGet(index.name, index.type, id).execute().actionGet();
		if (response == null)
			return null;
		Map<String, Object> source = response.getSource();
		if (source == null || source.isEmpty())
			return null;
		return source;
	}

	@Override
	public List<Map<String, Object>> get(Set<String> ids, EsIndex index) {
		MultiGetResponse response = client.prepareMultiGet().add(index.name, index.type, ids).execute().actionGet();
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
	public void clear(EsIndex index) {
		DeleteIndexRequest request = new DeleteIndexRequest(index.name);
		client.admin().indices().delete(request).actionGet();
	}

}
