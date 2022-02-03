define([
				'backbone'
				'cs!utils/Announcements'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'cs!models/Settings'
				'templates/views/admin/overview'
				'templates/views/admin/overview-repository-list'
				'templates/views/admin/overview-user-list'
				'templates/views/admin/overview-group-list'
				'templates/views/admin/overview-team-list'
			]

	(Backbone, Announcements, Events, Filter, Layers, Model, ModelTypes, Renderer, Status, Router, currentUser, settings, template, repositoriesTemplate, usersTemplate, groupsTemplate, teamsTemplate) ->

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
				'click [data-action=set-maintenance-message]': (event) -> @set event, 'Maintenance message', 'maintenanceMessage', 'MAINTENANCE_MESSAGE'
				'click [data-action=set-license-agreement-text]': (event) -> @set event, 'License agreement text', 'licenseAgreementText', 'LICENSE_AGREEMENT_TEXT'
				'click [data-action=set-home-title]': (event) -> @set event, 'Home title', 'homeTitle', 'HOME_TITLE'
				'click [data-action=set-home-text]': (event) -> @set event, 'Home text', 'homeText', 'HOME_TEXT'
				'click .repositories-order .glyphicon-upload': (event) -> @changeOrder event, 'REPOSITORIES_ORDER', 'orderedRepositories', true
				'click .repositories-order .glyphicon-download': (event) -> @changeOrder event, 'REPOSITORIES_ORDER', 'orderedRepositories', false
				'click .model-types-order .glyphicon-upload': (event) -> @changeOrder event, 'MODEL_TYPES_ORDER', 'orderedModelTypes', true
				'click .model-types-order .glyphicon-download': (event) -> @changeOrder event, 'MODEL_TYPES_ORDER', 'orderedModelTypes', false
				'click .repositories-order [data-action=change-visibility]': (event) -> @changeVisibility event, 'REPOSITORIES_HIDDEN', 'hiddenRepositories'
				'click .model-types-order [data-action=change-visibility]': (event) -> @changeVisibility event, 'MODEL_TYPES_HIDDEN', 'hiddenModelTypes'
				'click [data-action=refresh-open-web-service-requests]': 'refreshOpenWebServiceRequests'
				'click [data-action=set-announcement]': 'setAnnouncement'

			changeOrder: (event, key, field, up) ->
				target = $ Events.target event, 'li'
				index = $('li', target.parent()).index(target)
				array = []	
				for val, i in @serverInfo[field]
					if up
						if i < index - 1 or i > index
							array.push val
						if i is index 
							array.push @serverInfo[field][index]
							array.push @serverInfo[field][index - 1]
					else
						if i < index or i > index + 1
							array.push val
						if i is index 
							array.push @serverInfo[field][index + 1]
							array.push @serverInfo[field][index]
				@setSetting key, @join(array, ';', (element) -> element.id), () =>
					@serverInfo[field] = array
					sibling = if up then target.prev() else target.next()
					target.remove()
					if up
						target.insertBefore sibling
					else
						target.insertAfter sibling

			join: (array, separator, toValue) ->
				joined = ''
				for elem in array
					if joined
						joined += separator
					joined += toValue elem
				return joined

			changeVisibility: (event, key, field) ->
				target = $ Events.target event
				li = $ Events.target event, 'li'
				value = li.attr 'data-id'
				hide = target.hasClass 'glyphicon-eye-open'
				array = []
				for val in @serverInfo[field]
					if hide or val isnt value
						array.push val
				if hide
					array.push value
					li.attr 'data-hidden', true
				else
					li.removeAttr 'data-hidden'
				@setSetting key, array.join(';'), () =>
					@serverInfo[field] = array
					target.removeClass 'glyphicon-eye-close glyphicon-eye-open'
					if hide
						target.addClass 'glyphicon-eye-close'
					else
						target.addClass 'glyphicon-eye-open'

			toggleMaintenanceMode: () ->
				wasActive = @serverInfo.maintenanceModeActive
				@setSetting 'MAINTENANCE_MODE', !wasActive, () ->
					Backbone.history.loadUrl()
					if wasActive
						$('body').removeClass 'maintenance-mode'
					else
						$('body').addClass 'maintenance-mode'

			set: (event, label, field, key) ->
				Events.preventDefault event
				Layers.promptInput label, 'textarea', @serverInfo[field], (value) =>
					@setSetting key, value, () ->
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
						$.ajax
							type: 'PUT'
							url: 'ws/admin/area/clearIndex'
							success: () -> Backbone.history.loadUrl()

			reindexRepositories: () ->
				Layers.askQuestion
					title: 'Reindex repositories'
					question: 'Do you really want to reindex all repositories? This may take a while, depending on the amount and size of the repositories.'
					type: 'danger'
					answers: ['Cancel', 'Confirm']
					onAnswer: (answer) =>
						if answer isnt 1
							return
						$.ajax
							type: 'PUT'
							url: 'ws/admin/area/reindex'
							success: () -> Backbone.history.loadUrl()

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
						$.ajax
							type: 'PUT'
							url: "ws/admin/area/reindex/#{group}/#{repository}"
							success: () -> Backbone.history.loadUrl()

			initialize: () ->
				@repositoryFilter = new Filter
					container: '#repositories'
					template: repositoriesTemplate
					filterId: 'repository-filter'
					pageSizeId: 'repositories-page-size'
					url: 'ws/repository?'
					beforeRender: (result) =>
						result.reindexingStatus = @serverInfo.reindexingStatus			
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
				data = {}
				if currentUser.isAdmin()
					data = @serverInfo
					orderedRepositories = []
					for repo in data.orderedRepositories
						orderedRepositories.push {label: repo, id: repo, hidden: $.inArray(repo, @serverInfo.hiddenRepositories) isnt -1}
					data.orderedRepositories = orderedRepositories
					orderedModelTypes = []
					for type in data.orderedModelTypes
						orderedModelTypes.push {label: ModelTypes.plural(type), id: type, hidden: $.inArray(type, @serverInfo.hiddenModelTypes) isnt -1}
					data.orderedModelTypes = orderedModelTypes
				data.repositories = counts.repositories
				data.isAdmin = currentUser.isAdmin()
				data.users = counts.users
				data.groups = counts.groups
				data.teams =  counts.teams
				data.isHomepageEnabled = settings.is 'HOMEPAGE_ENABLED'
				@$el.html template data
				Renderer.render @, renderOptions
				@repositoryFilter.init()
				@userFilter.init()
				@groupFilter.init()
				@teamFilter.init()

)
