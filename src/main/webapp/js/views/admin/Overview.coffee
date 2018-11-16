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

			className: 'admin-overview multi-box-view'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=reindex-repositories]': 'reindexRepositories'
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=create-user]': () -> Router.navigate 'administration/user/new'
				'click [data-action=create-group]': () -> Router.navigate 'group/new'
				'click [data-action=create-team]': () -> Router.navigate 'administration/team/new'
				'click .toggle-maintenance-mode': 'toggleMaintenanceMode'


			toggleMaintenanceMode: () ->
				wasActive = @maintenanceModeActive
				$.ajax
					type: 'PUT'
					url: 'ws/admin/area/settings'
					contentType: 'application/json'
					data: JSON.stringify({key: 'MAINTENANCE_MODE', value: !@maintenanceModeActive})
					success: () -> 
						Backbone.history.loadUrl()
						if wasActive
							$('body').removeClass 'maintenance-mode'
						else
							$('body').addClass 'maintenance-mode'

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
				$.get 'ws/usermanager/area/count', (counts) =>
					if currentUser.isAdmin()
						$.get 'ws/settings/MAINTENANCE_MODE', (maintenanceModeActive) =>
							@maintenanceModeActive = maintenanceModeActive is true or maintenanceModeActive is 'true'
							@doRender renderOptions, counts
					else
						@doRender renderOptions, counts

			doRender: (renderOptions, counts) ->
				@$el.html template
					repositories: counts.repositories
					isAdmin: currentUser.isAdmin()
					users: counts.users
					groups: counts.groups
					teams: counts.teams
					maintenanceModeActive: @maintenanceModeActive
				Renderer.render @, renderOptions
				@repositoryFilter.init()
				@userFilter.init()
				@groupFilter.init()
				@teamFilter.init()

)