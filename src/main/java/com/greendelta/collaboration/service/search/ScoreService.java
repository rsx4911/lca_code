package com.greendelta.collaboration.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.collaboration.util.Aggregations;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.score.Comparator;
import com.greendelta.search.wrapper.score.Score;

@Service
class ScoreService {

	private final RepositoryService repoService;
	private final SettingsService settings;
	private final UserService userService;

	@Autowired
	ScoreService(RepositoryService repoService, SettingsService settings, UserService userService) {
		this.repoService = repoService;
		this.settings = settings;
		this.userService = userService;
	}

	void applyTo(SearchQueryBuilder builder) {
		var currentUser = userService.getCurrentUser();
		if (currentUser.isAnonymous()) {
			applyRepositoryOrder(builder);
		}
		applyTypeOrder(builder);
	}

	private void applyRepositoryOrder(SearchQueryBuilder builder) {
		var score = new Score(Aggregations.REPOSITORY.field);
		var repoOrder = repoService.getPublicRepositoryOrder();
		for (var i = 0; i < repoOrder.size(); i++) {
			score.addCase(repoOrder.size() - i + 1, Comparator.EQUALS, "\"" + repoOrder.get(i) + "\"");
		}
		score.addElse(1);
		builder.score(score);
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
