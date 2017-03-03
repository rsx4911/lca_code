define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/User'
				'templates/views/admin/overview'
				'templates/views/admin/overview-repository-list'
				'templates/views/admin/overview-user-list'
				'templates/views/admin/overview-group-list'
				'templates/views/admin/overview-team-list'
			]

	(Backbone, Events, Filter, Model, Renderer, Router, User, template, repositoriesTemplate, usersTemplate, groupsTemplate, teamsTemplate) ->

		class AdminOverview extends Backbone.View

			className: 'admin-overview content-box'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=create-user]': () -> Router.navigate 'administration/user/new'
				'click [data-action=create-group]': () -> Router.navigate 'group/new'
				'click [data-action=create-team]': () -> Router.navigate 'administration/team/new'

			initialize: () ->
				@repositoryFilter = new Filter
					container: '#repositories'
					template: repositoriesTemplate
					filterId: 'repository-filter'
					url: (page, filter) -> "ws/repository?page=#{page}&filter=#{filter}&adminArea=true"
				@userFilter = new Filter
					container: '#users'
					template: usersTemplate
					filterId: 'user-filter'
					url: (page, filter) -> "ws/user?page=#{page}&filter=#{filter}"
				@groupFilter = new Filter
					container: '#groups'
					template: groupsTemplate
					filterId: 'group-filter'
					url: (page, filter) -> "ws/group?page=#{page}&filter=#{filter}&adminArea=true"
				@teamFilter = new Filter
					container: '#teams'
					template: teamsTemplate
					filterId: 'team-filter'
					url: (page, filter) -> "ws/team?page=#{page}&filter=#{filter}"

			render: (renderOptions) ->
				$.get 'ws/admin/area/count', (result) =>
					@$el.html template
						repositories: result.repositories
						users: result.users
						groups: result.groups
						teams: result.teams
					Renderer.render @, renderOptions
					@repositoryFilter.init()
					@userFilter.init()
					@groupFilter.init()
					@teamFilter.init()

)