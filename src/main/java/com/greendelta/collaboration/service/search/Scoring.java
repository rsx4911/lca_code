package com.greendelta.collaboration.service.search;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.score.Comparator;
import com.greendelta.search.wrapper.score.Score;

class Scoring {

	static void apply(SearchQueryBuilder builder) {
		applyType(builder);
		applyMostRecent(builder);
	}

	private static void applyType(SearchQueryBuilder builder) {
		apply("type", 2, builder);
		apply("categoryType", 1, builder);
	}

	private static void applyMostRecent(SearchQueryBuilder builder) {
		Score score = new Score("mostRecent");
		score.addCase(1.01, Comparator.EQUALS, true);
		score.addElse(1);
		builder.score(score);
	}

	private static void apply(String field, double factor, SearchQueryBuilder builder) {
		Score score = new Score(field);
		double typeCount = ModelTypes.SORTED.length;
		for (int i = 0; i < typeCount; i++) {
			ModelType type = ModelTypes.SORTED[i];
			score.addCase(0.5 * factor * (factor * typeCount - i), Comparator.EQUALS, "\"" + type.name() + "\"");
		}
		score.addElse(1);
		builder.score(score);
	}

}
