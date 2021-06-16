package com.greendelta.collaboration.service.search;

import java.util.List;

import org.openlca.core.model.ModelType;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.RepositoryService;
import com.greendelta.collaboration.service.SettingsService;
import com.greendelta.collaboration.service.user.UserService;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.score.Comparator;
import com.greendelta.search.wrapper.score.Score;

class ScoreService {

	private final RepositoryService repoService;
	private final SettingsService settingsService;
	private final UserService userService;

	@Inject
	ScoreService(RepositoryService repoService, SettingsService settingsService, UserService userService) {
		this.repoService = repoService;
		this.settingsService = settingsService;
		this.userService = userService;
	}

	void apply(SearchQueryBuilder builder) {
		User currentUser = userService.getCurrentUser();
		if (currentUser.id == 0) {
			applyRepositoryOrder(builder);
		}
		applyTypeOrder(builder);
	}

	private void applyRepositoryOrder(SearchQueryBuilder builder) {
		Score score = new Score("repositoryId");
		List<String> repoOrder = repoService.getPublicRepositoryOrder();
		for (int i = 0; i < repoOrder.size(); i++) {
			score.addCase(repoOrder.size() - i + 1, Comparator.EQUALS, "\"" + repoOrder.get(i) + "\"");
		}
		score.addElse(1);
		builder.score(score);
	}

	private void applyTypeOrder(SearchQueryBuilder builder) {
		apply("type", 2, builder);
	}

	private void apply(String field, double factor, SearchQueryBuilder builder) {
		Score score = new Score(field);
		ModelType[] types = settingsService.serverConfig.getModelTypes();
		for (int i = 0; i < types.length; i++) {
			ModelType type = types[i];
			if (type == ModelType.CATEGORY)
				continue;
			score.addCase(0.5 * factor * (types.length - i + 1), Comparator.EQUALS, "\"" + type.name() + "\"");
		}
		score.addElse(1);
		builder.score(score);
	}

}
