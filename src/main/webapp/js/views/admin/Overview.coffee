define([
				'backbone'
				'cs!utils/Announcements'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/admin/overview'
				'templates/views/admin/overview-repository-list'
				'templates/views/admin/overview-user-list'
				'templates/views/admin/overview-group-list'
				'templates/views/admin/overview-team-list'
			]

	(Backbone, Announcements, Events, Filter, Layers, Model, Renderer, Status, Router, currentUser, template, repositoriesTemplate, usersTemplate, groupsTemplate, teamsTemplate) ->

		class AdminOverview extends Backbone.View

			className: 'admin-overview multi-box-view'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=clear-index]': 'clearIndex'
				'click [data-action=reindex-repositories]': 'reindexRepositories'
				'click [data-action=reindex-repository]': 'reindexRepository'
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=create-user]': () -> Router.navigate 'administration/user/new'
				'click [data-action=create-group]': () -> Router.navigate 'group/new'
				'click [data-action=create-team]': () -> Router.navigate 'administration/team/new'
				'click [data-action=toggle-maintenance-mode]': 'toggleMaintenanceMode'
				'click [data-action=set-maintenance-message]': 'setMaintenanceMessage'
				'click [data-action=refresh-open-web-service-requests]': 'refreshOpenWebServiceRequests'
				'click [data-action=set-announcement]': 'setAnnouncement'
				'click [data-action=set-license-agreement-text]': 'setLicenseAgreementText'

			toggleMaintenanceMode: () ->
				wasActive = @serverInfo.maintenanceModeActive
				@setSetting 'MAINTENANCE_MODE', !wasActive, () ->
					Backbone.history.loadUrl()
					if wasActive
						$('body').removeClass 'maintenance-mode'
					else
						$('body').addClass 'maintenance-mode'

			setMaintenanceMessage: (event) ->
				Events.preventDefault event
				Layers.promptInput 'Maintenance message', 'textarea', @serverInfo.maintenanceMessage, (value) =>
					@setSetting 'MAINTENANCE_MESSAGE', value, () ->
						Backbone.history.loadUrl()

			setLicenseAgreementText: (event) ->
				Events.preventDefault event
				Layers.promptInput 'License agreement text', 'textarea', @serverInfo.licenseAgreementText, (value) =>
					@setSetting 'LICENSE_AGREEMENT_TEXT', value, () ->
						Backbone.history.loadUrl()

			refreshOpenWebServiceRequests: (event) ->
				Events.preventDefault event
				$.ajax
					type: 'GET'
					url: 'ws/admin/area/serverInfo'
					success: (serverInfo) =>
						$('#open-web-service-requests').html serverInfo.openWebServiceRequests

			setAnnouncement: (event) ->
				Events.preventDefault event
				announcement = @serverInfo.announcement
				Layers.promptInput 'Announcement', 'textarea', announcement, (value) =>
					$.ajax
						type: 'PUT'
						url: 'ws/admin/area/announce'
						data: value
						success: () => 
							Backbone.history.loadUrl()
							if value
								Announcements.announce value
							else
								Announcements.clear()						
				
			setSetting: (key, value, callback) ->
				$.ajax
					type: 'PUT'
					url: 'ws/admin/area/settings'
					contentType: 'application/json'
					data: JSON.stringify({key: key, value: value})
					success: callback

			clearIndex: () ->
				Layers.askQuestion
					title: 'Reindex repositories'
					question: 'Do you really want to clear the search index? This action can not be undone.'
					type: 'danger'
					answers: ['Cancel', 'Confirm']
					onAnswer: (answer) =>
						if answer isnt 1
							return
						Layers.showProgressIndicator 'Clearing index'
						$.ajax
							type: 'PUT'
							url: 'ws/admin/area/clearIndex'
							success: () ->
								Layers.hideProgressIndicator()
								Status.success 'Successfully cleared index'

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

			reindexRepository: (event) ->
				target = $ Events.target event
				group = target.attr 'data-group'
				repository = target.attr 'data-repository'
				Layers.askQuestion
					title: 'Reindex repository'
					question: "Do you really want to reindex repository #{group}/#{repository} ? This may take a while, depending on the amount and size of the repository."
					type: 'danger'
					answers: ['Cancel', 'Confirm']
					onAnswer: (answer) =>
						if answer isnt 1
							return
						Layers.showProgressIndicator 'Indexing'
						$.ajax
							type: 'PUT'
							url: "ws/admin/area/reindex/#{group}/#{repository}"
							success: () ->
								Layers.hideProgressIndicator()
								Status.success 'Successfully reindexed repository'

			initialize: () ->
				@repositoryFilter = new Filter
					container: '#repositories'
					template: repositoriesTemplate
					filterId: 'repository-filter'
					pageSizeId: 'repositories-page-size'
					url: 'ws/repository?'
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
					url: 'ws/group?'
				@teamFilter = new Filter
					container: '#teams'
					template: teamsTemplate
					filterId: 'team-filter'
					pageSizeId: 'teams-page-size'
					url: 'ws/team?'

			render: (renderOptions) ->
				$.get 'ws/usermanager/area/count', (counts) =>
					if currentUser.isAdmin()
						$.get 'ws/admin/area/serverInfo', (serverInfo) =>
							@serverInfo = serverInfo
							@doRender renderOptions, counts
					else
						@doRender renderOptions, counts

			doRender: (renderOptions, counts) ->
				data = 
					repositories: counts.repositories
					isAdmin: currentUser.isAdmin()
					users: counts.users
					groups: counts.groups
					teams: counts.teams
				if currentUser.isAdmin()
					data.maintenanceModeActive = @serverInfo.maintenanceModeActive
					data.openWebServiceRequests = @serverInfo.openWebServiceRequests
					data.announcement = @serverInfo.announcement
					data.maintenanceMessage = @serverInfo.maintenanceMessage
					data.licenseAgreementText = @serverInfo.licenseAgreementText
				@$el.html template data
				Renderer.render @, renderOptions
				@repositoryFilter.init()
				@userFilter.init()
				@groupFilter.init()
				@teamFilter.init()

)
