package com.greendelta.collaboration.search.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import com.greendelta.collaboration.search.SearchResult;
import com.greendelta.collaboration.search.aggregations.AggregationResult;
import com.greendelta.collaboration.search.aggregations.AggregationResultBuilder;
import com.greendelta.collaboration.search.aggregations.TermEntryBuilder;

/**
 * Query result. Can be serialized through {@link ClientHelper} with results in
 * <code>data</code> and aggregations in <code>aggregations</code>.
 */
class EsResult implements SearchResult {

	final List<Map<String, Object>> data = new ArrayList<>();
	final List<Aggregation> aggregations = new ArrayList<>();
	final ResultInfo resultInfo = new ResultInfo();
	final EsQuery originalQuery;

	EsResult(EsQuery originalQuery) {
		this.originalQuery = originalQuery;
	}

	@Override
	public List<Map<String, Object>> getData() {
		return data;
	}

	@Override
	public List<AggregationResult> getAggregations() {
		List<AggregationResult> results = new ArrayList<>();
		for (Aggregation aggregation : aggregations) {
			AggregationResultBuilder builder = new AggregationResultBuilder();
			builder.name(aggregation.getName()).type(aggregation.getType());
			putSpecificData(aggregation, builder);
			results.add(builder.build());
		}
		return results;
	}

	private void putSpecificData(Aggregation aggregation, AggregationResultBuilder builder) {
		switch (aggregation.getType()) {
		case StringTerms.NAME:
			ParsedStringTerms stringTerms = (ParsedStringTerms) aggregation;
			long totalCount = 0;
			for (Bucket bucket : stringTerms.getBuckets()) {
				TermEntryBuilder entryBuilder = new TermEntryBuilder();
				entryBuilder.key(bucket.getKeyAsString()).count(bucket.getDocCount());
				builder.addEntry(entryBuilder.build());
				totalCount += bucket.getDocCount();
			}
			builder.totalCount(totalCount);
			break;
		}
	}

	@Override
	public ResultInfo getResultInfo() {
		return resultInfo;
	}

	@Override
	public EsQuery getOriginalQuery() {
		return originalQuery;
	}

}
