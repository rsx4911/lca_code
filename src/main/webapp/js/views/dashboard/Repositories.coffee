define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/dashboard/repositories'
				'templates/views/dashboard/repositories-list'
			]

	(Backbone, Events, Filter, Renderer, Router, currentUser, template, listTemplate) ->

		class DashboardRepositories extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'

			initialize: () ->
				@filter = new Filter
					container: '#repositories'
					template: listTemplate
					filterId: 'filter'
					url: (page, filter) -> "/ws/repository?page=#{page}&filter=#{filter}"

			render: (renderOptions) ->
				@$el.html template
					canCreateRepositories: (currentUser.get('canCreateRepositories') or currentUser.get('admin'))
				Renderer.render @, renderOptions
				@filter.init()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)