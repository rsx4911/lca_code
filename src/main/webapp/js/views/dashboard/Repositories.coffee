define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/dashboard/repositories'
				'templates/views/dashboard/repositories-list'
			]

	(Backbone, Events, Filter, Model, Renderer, Roles, Router, currentUser, template, listTemplate) ->

		class DashboardRepositories extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=import-repository]': () -> Router.navigate 'repository/import'
				'click [data-action=import-json]': () -> Router.navigate 'repository/import-json'
				'click [data-action=import-external]': () -> Router.navigate 'repository/import-external'

			initialize: () ->
				@filter = new Filter
					container: '#repositories'
					template: listTemplate
					filterId: 'filter'
					url: 'ws/repository?module=DASHBOARD&'
					beforeRender: (result) =>
						setRole = (r) -> r.role = { name: Roles[r.role].name, description: Roles[r.role].descriptionForGroup} 
						setRole r for r in result.data

			render: (renderOptions) ->
				Model.fetch currentUser, 
					force: true
					success: () =>
						settings = currentUser.get 'settings'
						noOfRepositories = currentUser.get 'noOfRepositories'
						@$el.html template
							canCreateRepositories: currentUser.isAdmin() or (settings and (settings.canCreateRepositories and (!settings.noOfRepositories or settings.noOfRepositories > noOfRepositories)))
						Renderer.render @, renderOptions
						@filter.init()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)