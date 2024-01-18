package com.greendelta.collaboration.service.search;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.score.Comparator;
import com.greendelta.search.wrapper.score.Score;

@Service
class ScoreService {

	private final SettingsService settings;

	ScoreService(SettingsService settings) {
		this.settings = settings;
	}

	void applyTo(SearchQueryBuilder builder) {
		applyTypeOrder(builder);
	}

	private void applyTypeOrder(SearchQueryBuilder builder) {
		var score = new Score(Aggregations.MODEL_TYPE.field);
		var types = settings.serverConfig.getModelTypes();
		for (var i = 0; i < types.length; i++) {
			score.addCase(types.length - i + 1, Comparator.EQUALS, "\"" + types[i].name() + "\"");
		}
		score.addElse(1);
		builder.score(score);
	}

}
