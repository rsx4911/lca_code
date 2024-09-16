define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/dashboard/repositories'
				'templates/views/dashboard/repositories-list'
			]

	(Backbone, Events, Filter, Format, Model, Renderer, Roles, Router, currentUser, template, listTemplate) ->

		class DashboardRepositories extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=import-repository]': () -> Router.navigate 'repository/import'
				'click [data-action=import-json]': () -> Router.navigate 'repository/import-json'
				'click [data-action=import-external]': () -> Router.navigate 'repository/import-external'

			updateCount: (repo) ->
				repoId = "#{repo.group}/#{repo.name}"
				$.get "ws/repository/count/#{repoId}", (count) =>
					$('.dataset-count-container[data-repo-id="' + repoId + '"]').html "#{count.datasets} #{if count.datasets is 1 then 'data set' else 'data sets' }"

			initialize: () ->
				@filter = new Filter
					container: '#repositories'
					template: listTemplate
					filterId: 'filter'
					url: 'ws/repository?module=DASHBOARD&'
					beforeRender: (result) =>
						result.formatDate = Format.date
						setRole = (r) ->
							role = Roles[r.role]
							if role
								r.role = { name: Roles[r.role].name, description: Roles[r.role].descriptionForRepository} 
							else
								r.role = undefined
						setRole r for r in result.data
					afterRender: (result) =>
						setTimeout () =>
							for repo in result.data
								@updateCount repo
						, 10
			

			render: (renderOptions) ->
				Model.fetch currentUser, 
					force: true
					success: () =>
						settings = currentUser.get 'settings'
						noOfRepositories = currentUser.get 'noOfRepositories'
						@$el.html template
							canCreateRepositories: currentUser.isAdmin() or (settings and (settings.canCreateRepositories and (!settings.noOfRepositories or settings.noOfRepositories > noOfRepositories)))
						Renderer.render @, renderOptions
						@filter.init (result) =>
							if result.resultInfo.totalCount is 0
								@$('#repositories').append('<div class="no-content-message">No repositories found</div>')

)