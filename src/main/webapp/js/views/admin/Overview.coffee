define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/User'
				'cs!models/CurrentUser'
				'templates/views/admin/overview'
				'templates/views/admin/overview-repository-list'
				'templates/views/admin/overview-user-list'
				'templates/views/admin/overview-group-list'
				'templates/views/admin/overview-team-list'
			]

	(Backbone, Events, Filter, Layers, Model, Renderer, Status, Router, User, currentUser, template, repositoriesTemplate, usersTemplate, groupsTemplate, teamsTemplate) ->

		class AdminOverview extends Backbone.View

			className: 'admin-overview content-box'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=reindex-repositories]': 'reindexRepositories'
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=create-user]': () -> Router.navigate 'administration/user/new'
				'click [data-action=create-group]': () -> Router.navigate 'group/new'
				'click [data-action=create-team]': () -> Router.navigate 'administration/team/new'

			reindexRepositories: () ->
				Layers.askQuestion
					title: 'Reindex repositories'
					question: 'Do you really want to reindex all repositories? This may take a while, depending on the amount and size of the repositories.'
					type: 'danger'
					answers: ['Cancel', 'Confirm']
					onAnswer: (answer) =>
						if answer isnt 1
							return
						Layers.showProgressIndicator 'Indexing'
						$.ajax
							type: 'PUT'
							url: 'ws/admin/area/reindex'
							success: () ->
								Layers.hideProgressIndicator()
								Status.success 'Successfully reindexed repositories'

			initialize: () ->
				@repositoryFilter = new Filter
					container: '#repositories'
					template: repositoriesTemplate
					filterId: 'repository-filter'
					pageSizeId: 'repositories-page-size'
					url: 'ws/repository?adminArea=true&'
				@userFilter = new Filter
					container: '#users'
					template: usersTemplate
					filterId: 'user-filter'
					pageSizeId: 'users-page-size'
					url: 'ws/user?'
				@groupFilter = new Filter
					container: '#groups'
					template: groupsTemplate
					filterId: 'group-filter'
					pageSizeId: 'groups-page-size'
					url: 'ws/group?adminArea=true&'
				@teamFilter = new Filter
					container: '#teams'
					template: teamsTemplate
					filterId: 'team-filter'
					pageSizeId: 'teams-page-size'
					url: 'ws/team?'

			render: (renderOptions) ->
				$.get 'ws/manager/area/count', (result) =>
					@$el.html template
						repositories: result.repositories
						isAdmin: currentUser.isAdmin()
						users: result.users
						groups: result.groups
						teams: result.teams
					Renderer.render @, renderOptions
					@repositoryFilter.init()
					@userFilter.init()
					@groupFilter.init()
					@teamFilter.init()

)