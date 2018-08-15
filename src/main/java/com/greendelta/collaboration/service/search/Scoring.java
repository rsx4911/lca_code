package com.greendelta.collaboration.service.search;

import java.util.Calendar;

import org.openlca.core.model.ModelType;

import com.greendelta.collaboration.util.ModelTypes;
import com.greendelta.search.wrapper.LinearDecayFunction;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.score.Comparator;
import com.greendelta.search.wrapper.score.Score;

class Scoring {

	static void applyType(SearchQueryBuilder builder) {
		Score score = new Score("type");
		double typeCount = ModelTypes.SORTED.length;
		for (int i = 0; i < typeCount; i++) {
			ModelType type = ModelTypes.SORTED[i];
			score.addCase(2 - (i / typeCount), Comparator.EQUALS, "\"" + type.name() + "\"");
		}
		score.addElse(1);
		builder.score(score);
	}

	static void applyCommitTimestamp(SearchQueryBuilder builder) {
		builder.score(new LinearDecayFunction("commitTimestamp", Calendar.getInstance().getTimeInMillis(), "86400000"));
	}

}
