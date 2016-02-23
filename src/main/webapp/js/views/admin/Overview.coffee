define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/User'
				'templates/views/admin/overview'
				'templates/views/admin/overview-user-list'
				'templates/views/admin/overview-repository-list'
			]

	(Backbone, Events, Filter, Model, Renderer, Router, User, template, usersTemplate, repositoriesTemplate) ->

		class AdminOverview extends Backbone.View

			className: 'admin-overview two-columns content-box'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=create-user]': () -> Router.navigate 'administration/user/new'
				'click [data-action=create-repository]': () -> Router.navigate 'administration/repository/new'

			initialize: () ->
				@userFilter = new Filter
					container: '#users'
					template: usersTemplate
					filterId: 'user-filter'
					url: (page, filter) -> "/ws/admin/user?page=#{page}&filter=#{filter}"
				@repositoryFilter = new Filter
					container: '#repositories'
					template: repositoriesTemplate
					filterId: 'repository-filter'
					url: (page, filter) -> "/ws/repository?page=#{page}&filter=#{filter}&adminArea=true"

			render: (renderOptions) ->
				$.get '/ws/admin/area/count', (result) =>
					@$el.html template
						users: result.users
						repositories: result.repositories
					Renderer.render @, renderOptions
					@userFilter.init()
					@repositoryFilter.init()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)