define([
				'backbone'
				'cs!utils/Announcements'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
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

	(Backbone, Announcements, Events, Filter, Format, Layers, Model, ModelTypes, Renderer, Status, Router, currentUser, settings, template, repositoriesTemplate, usersTemplate, groupsTemplate, teamsTemplate) ->

		class AdminOverview extends Backbone.View

			className: 'admin-overview multi-box-view'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=clear-index]': 'clearIndex'
				'click [data-action=reindex-repositories]': 'reindexRepositories'
				'click [data-action=reindex-repository]': 'reindexRepository'
				'click [data-action=copy-users-to-clipboard]': 'copyUsersToClipboard'
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new'
				'click [data-action=create-user]': () -> Router.navigate 'administration/user/new'
				'click [data-action=create-group]': () -> Router.navigate 'group/new'
				'click [data-action=create-team]': () -> Router.navigate 'administration/team/new'
				'click [data-action=toggle-maintenance-mode]': 'toggleMaintenanceMode'
				'click [data-action=set-maintenance-message]': (event) -> @set event, 'Maintenance message', 'maintenanceMessage', 'MAINTENANCE_MESSAGE'
				'click [data-action=set-license-agreement-text]': (event) -> @set event, 'License agreement text', 'licenseAgreementText', 'LICENSE_AGREEMENT_TEXT'
				'click [data-action=set-home-title]': (event) -> @set event, 'Home title', 'homeTitle', 'HOME_TITLE'
				'click [data-action=set-home-text]': (event) -> @set event, 'Home text', 'homeText', 'HOME_TEXT'
				'click .repositories-order .glyphicon-upload': (event) -> @changeOrder event, 'REPOSITORIES_ORDER', 'repositoriesOrder', true
				'click .repositories-order .glyphicon-download': (event) -> @changeOrder event, 'REPOSITORIES_ORDER', 'repositoriesOrder', false
				'click .model-types-order .glyphicon-upload': (event) -> @changeOrder event, 'MODEL_TYPES_ORDER', 'modelTypesOrder', true
				'click .model-types-order .glyphicon-download': (event) -> @changeOrder event, 'MODEL_TYPES_ORDER', 'modelTypesOrder', false
				'click .repositories-order [data-action=change-visibility]': (event) -> @changeVisibility event, 'REPOSITORIES_HIDDEN', 'repositoriesHidden'
				'click .model-types-order [data-action=change-visibility]': (event) -> @changeVisibility event, 'MODEL_TYPES_HIDDEN', 'modelTypesHidden'
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
				@setSetting key, JSON.stringify(array.map((element) -> element.id)), () =>
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
				@setSetting key, JSON.stringify(array), () =>
					@serverInfo[field] = array
					target.removeClass 'glyphicon-eye-close glyphicon-eye-open'
					if hide
						target.addClass 'glyphicon-eye-close'
					else
						target.addClass 'glyphicon-eye-open'

			toggleMaintenanceMode: () ->
				wasActive = @serverInfo.maintenanceMode
				@setSetting 'MAINTENANCE_MODE', !wasActive, () ->
					Backbone.history.loadUrl()
					if wasActive
						$('body').removeClass 'maintenance-mode'
					else
						$('body').addClass 'maintenance-mode'

			set: (event, label, field, key) ->
				Events.preventDefault event
				Layers.promptInput
					type: 'textarea'
					label: label
					value: @serverInfo[field]
					callback: (value) =>
						@setSetting key, value, () ->
							Backbone.history.loadUrl()

			setAnnouncement: (event) ->
				Events.preventDefault event
				Layers.promptInput 
					type: 'textarea'
					label: 'Announcement'
					value: @serverInfo.announcementMessage
					callback: (value) =>
						$.ajax
							type: 'PUT'
							url: if value then 'ws/admin/area/announce' else 'ws/admin/area/clearAnnouncement'
							contentType: if value then 'text/plain' else null
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
					data: JSON.stringify({type: 'SERVER_SETTING', key: key, value: value})
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

			copyUsersToClipboard: (event) ->
				text = '"Username"\t"Name"\t"Email"\t"Active"\n'
				for user in @userFilter.data
					text += '"' + user.username + '"\t'
					text += '"' + user.name + '"\t'
					text += '"' + user.email + '"\t'
					text += '"' + (if user.deactivated then 'no' else 'yes') + '"\n'
				Layers.showMessageInLayer
					title: 'User data'
					body: "<span>Press CTRL+C to copy (hidden) text to clipboard</span><textarea id=\"user-data-text\" class=\"pull-right\" style=\"width:0;height:0;border:0;resize:none;outline:0;\">#{text}</textarea>"
					buttons: [
						{text: 'Close', callback: () -> Layers.closeActive()}
					]
				$('#user-data-text').select()				

			initialize: () ->
				@repositoryFilter = new Filter
					container: '#repositories'
					template: repositoriesTemplate
					filterId: 'repository-filter'
					pageSizeId: 'repositories-page-size'
					url: 'ws/repository?'
					beforeRender: (result) =>
						result.isSearchEnabled = settings.is 'SEARCH_AVAILABLE'
						if @serverInfo
							result.reindexingStatus = @serverInfo.reindexingStatus
				@groupFilter = new Filter
					container: '#groups'
					template: groupsTemplate
					filterId: 'group-filter'
					pageSizeId: 'groups-page-size'
					url: 'ws/group?adminArea=true&'
				@userFilter = new Filter
					container: '#users'
					template: usersTemplate
					filterId: 'user-filter'
					pageSizeId: 'users-page-size'
					url: 'ws/usermanager/user?'
					beforeRender: (result) ->
						result.formatDate = Format.date
				@teamFilter = new Filter
					container: '#teams'
					template: teamsTemplate
					filterId: 'team-filter'
					pageSizeId: 'teams-page-size'
					url: 'ws/usermanager/team?'

			render: (renderOptions) ->
				managerType = if currentUser.isAdmin() then 'admin' else if currentUser.isDataManager() then 'datamanager' else 'usermanager'
				$.get "ws/#{managerType}/area/count", (counts) =>
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
					repositoriesOrder = []
					if data.repositoriesOrder
						for repo in data.repositoriesOrder
							repositoriesOrder.push {label: repo, id: repo, hidden: $.inArray(repo, @serverInfo.repositoriesHidden) isnt -1}
					data.repositoriesOrder = repositoriesOrder
					modelTypesOrder = []
					if data.modelTypesOrder
						for type in data.modelTypesOrder
							modelTypesOrder.push {label: ModelTypes.plural(type), id: type, hidden: $.inArray(type, @serverInfo.modelTypesHidden) isnt -1}
					data.modelTypesOrder = modelTypesOrder
				data.repositories = counts.repositories
				data.isAdmin = currentUser.isAdmin()
				data.isDataManager = currentUser.isDataManager()
				data.isUserManager = currentUser.isUserManager()
				data.users = counts.users
				data.groups = counts.groups
				data.teams =  counts.teams
				data.isHomepageEnabled = settings.is 'HOMEPAGE_ENABLED'
				data.isSearchEnabled = settings.is 'SEARCH_AVAILABLE'
				data.maintenanceModeActive = @serverInfo?.maintenanceMode
				@$el.html template data
				Renderer.render @, renderOptions
				if currentUser.isUserManager()
					@userFilter.init()
					@teamFilter.init()
				if currentUser.isDataManager()
					@repositoryFilter.init()
					@groupFilter.init()
					@renderIndexingTasks(data.indexingTasks)

			renderIndexingTasks: (indexingTasks) ->
				unless $('#indexing-status').length
					return
				unless indexingTasks
					@$('#indexing-status').html('')
					return
				html = "<hr><div>Indexing status (#{indexingTasks.length} tasks queued):</div>"
				for task, index in indexingTasks
					html += "<div>* #{task}</div>"
				@$('#indexing-status').html html
				setTimeout () =>
					$.get 'ws/admin/area/serverInfo', (serverInfo) => @renderIndexingTasks(serverInfo.indexingTasks)						
				, 5000

)
