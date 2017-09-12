package com.greendelta.collaboration.search.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.greendelta.collaboration.search.SearchParameter;
import com.greendelta.collaboration.search.SearchParameterValue;
import com.greendelta.collaboration.search.SearchParameterValue.Type;

class EsParameter extends SearchParameter {

	EsParameter(String name, Conjunction type) {
		super(name, type);
	}

	BoolQueryBuilder toQuery() {
		return toQuery(null);
	}

	BoolQueryBuilder toQuery(EsAggregation aggregation) {
		if (values.isEmpty())
			return null;
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		boolean isRelevant = false;
		for (SearchParameterValue value : values) {
			if (value.value.length() < 3)
				continue;
			isRelevant = true;
			QueryBuilder inner = null;
			if (aggregation == null)
				if (value.type == Type.PHRASE)
					inner = matchPhraseQuery(name, "\"" + value.value + "\"");
				else
					inner = wildcardQuery(name, value.value.toLowerCase());
			else
				inner = aggregation.getQuery(value.value);
			if (type == Conjunction.AND)
				query.must(inner);
			else if (type == Conjunction.OR)
				query.should(inner);
		}
		if (!isRelevant)
			return null;
		return query;
	}


}
