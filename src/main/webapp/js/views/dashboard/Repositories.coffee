define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/dashboard/repositories'
				'templates/views/dashboard/repositories-list'
			]

	(Backbone, Events, Filter, Model, Renderer, Router, currentUser, template, listTemplate) ->

		class DashboardRepositories extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=import-repository]': () -> Router.navigate 'repository/import'

			initialize: () ->
				@filter = new Filter
					container: '#repositories'
					template: listTemplate
					filterId: 'filter'
					url: 'ws/repository?'

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