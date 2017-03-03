define([
				'cs!app/Navigation'
				'cs!app/UserMenu'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!models/Repository'
				'cs!models/User'
				'cs!models/Group'
				'cs!models/Team'
				'cs!models/Conversations'
				'cs!models/CurrentUser'
				'templates/views/error'
			]
	
	(Navigation, UserMenu, Events, Layers, Model, Repository, User, Group, Team, conversations, currentUser, errorTemplate) ->

		Controller = () ->

		Controller:: = (() ->

			concatUrl: (prefix, part) ->
				if !prefix and !part
					return ''
				if !prefix
					return part
				if !part
					return prefix
				return "#{prefix}/#{part}"


			getNav: (options) ->
				unless options
					return
				type = options.type
				prefix = if options.urlPrefix then "#{options.urlPrefix}" else ''
				if type is 'group'
					prefix = if options.urlPrefix then "groups/#{options.urlPrefix}" else 'groups'
				# the ids are used in Navigation to identify which menu item is currently active
				# they need only to be unique within 'type'
				switch type
					when 'dashboard' then return [
						{href: @concatUrl(prefix, 'dashboard/repositories'), imageSrc: 'images/repository.png', label: 'Repositories', id: 'repositories'}
						{href: @concatUrl(prefix, 'dashboard/groups'), imageSrc: 'images/group.png', label: 'Groups', id: 'groups'}
					]
					when 'messaging' then return [
						{href: @concatUrl(prefix, 'messages'), imageSrc: 'images/inbox.png', label: 'Inbox', id: 'inbox'}
					]
					when 'user' then return [
						{href: @concatUrl(prefix, 'user/profile'), imageSrc: 'images/profile.png', label: 'Profile', id: 'profile'}
						{href: @concatUrl(prefix, 'user/messaging'), imageSrc: 'images/inbox.png', label: 'Messaging', id: 'messaging'}
						{href: @concatUrl(prefix, 'user/notifications'), imageSrc: 'images/notifications.png', label: 'Notifications', id: 'notifications'}
					]
					when 'group' then return [
						{href: @concatUrl(prefix, ''), imageSrc: 'images/group.png', label: 'Group', id: 'group'}
						{href: @concatUrl(prefix, 'members'), imageSrc: 'images/members.png', label: 'Members', id: 'members'}
					]
					when 'repository' then return [
						{href: @concatUrl(prefix, ''), imageSrc: 'images/repository.png', label: 'Repository', id: 'repository'}
						{href: @concatUrl(prefix, 'datasets'), imageSrc: 'images/dataset.png', label: 'Data sets', id: 'datasets'}
						{href: @concatUrl(prefix, 'commits'), imageSrc: 'images/commit.png', label: 'Commits', id: 'commits'}
						{href: @concatUrl(prefix, 'members'), imageSrc: 'images/members.png', label: 'Members', id: 'members'}
					]
					when 'admin' then return [
						{href: @concatUrl(prefix, 'administration/overview'), imageSrc: 'images/overview.png', label: 'Overview', id:'overview'}
						{href: @concatUrl(prefix, 'administration/libraries'), imageSrc: 'images/libraries.png', label: 'Library data sets', id:'libraries'}
					]

			initializeNavigation: () ->
				@navigation = new Navigation()
				@navigation.render 
					container: 'nav'
					noAnimation: true

			initializeUserMenu: () ->
				@userMenu = new UserMenu().render 
					container: '#user-menu'
					noAnimation: true

			registerRoutes: () ->
				@registerRouteRewrites()
				@registerAdminRoutes()
				@registerUserRoutes()

			registerRouteRewrites: () ->
				@router.registerRouteRewrite 'dashboardRepositories', 'dashboard/repositories'
				@router.registerRouteRewrite 'userProfile', 'user/profile'
				@router.registerRouteRewrite 'adminOverview', 'administration/overview'

			registerAdminRoutes: () ->
				@router.registerAdminRoute 'adminOverview', -> @showView 
					view: 'admin/Overview'
					title: 'Admin area - Overview'
					nav:
						type: 'admin'
						active: 'overview'
				@router.registerAdminRoute 'adminUserNew', -> @showView 
					view: 'user/Profile'
					title: 'New profile'
					viewOptions: 
						user: new User()
						adminArea: true
				@router.registerAdminRoute 'adminUserEdit', (username) -> @showView 
					view: 'user/Profile'
					title: "Profile | #{username}"
					viewOptions: 
						user: new User {username: username}
						adminArea: true
				@router.registerAdminRoute 'adminTeamNew', -> @showView 
					view: 'team/Profile'
					title: 'New team'
					viewOptions: 
						team: new Team()
				@router.registerAdminRoute 'adminTeamEdit', (teamname) -> @showView 
					view: 'team/Profile'
					title: "Profile | #{teamname}"
					viewOptions: 
						team: new Team {teamname: teamname}
				@router.registerAdminRoute 'adminLibraries', -> @showView 
					view: 'admin/Libraries'
					title: 'Admin area - Library data sets'
					nav:
						type: 'admin'
						active: 'libraries'

			registerUserRoutes: () ->
				@router.registerUserRoute 'notFound', -> @showError 404
				@router.registerUserRoute 'error', (statuscode) ->
					if statuscode
						@showError parseInt statuscode
					else
						@showError()
				@router.registerUserRoute 'search', (query) => @showView 
					view: 'search/Results'
					title: 'Search' 
					viewOptions: @splitQuery query
				@router.registerUserRoute 'userProfile', -> @showView 
					view: 'user/Profile'
					title: 'Profile' 
					nav: 
						type: 'user'
						active: 'profile'
				@router.registerUserRoute 'userMessaging', -> @showView 
					view: 'user/Messaging'
					title: 'Messaging' 
					nav: 
						type: 'user'
						active: 'messaging'
				@router.registerUserRoute 'userNotifications', -> @showView 
					view: 'user/Notifications'
					title: 'Notifications' 
					nav: 
						type: 'user'
						active: 'notifications'
				@router.registerUserRoute 'dashboardRepositories', -> @showView 
					view: 'dashboard/Repositories'
					title: 'Repositories' 
					nav: 
						type: 'dashboard'
						active: 'repositories'
				@router.registerUserRoute 'dashboardGroups', -> @showView 
					view: 'dashboard/Groups'
					title: 'Groups' 
					nav: 
						type: 'dashboard'
						active: 'groups'
				@router.registerUserRoute 'messages', (username) -> 
					unless window.WebSocket
						@router.navigate 'error/404', {trigger: true, replace: true}
					@showView 
						view: 'messaging/Messages'
						title: 'Messages' 
						nav: 
							type: 'messaging'
							active: 'inbox'
						viewOptions: 
							username: username
				@router.registerUserRoute 'groupNew', -> @showView 
					view: 'group/Create'
					title: 'New group' 
				@router.registerUserRoute 'groupInfo', (group) -> @showView 
					view: 'group/Group'
					title: group
					nav: 
						type: 'group'
						active: 'group'
						urlPrefix: group
					viewOptions: 
						group: new Group({name: group})
				@router.registerUserRoute 'groupMembers', (group) -> @showView 
					view: 'members/Members'
					title: "#{group} | Members"
					nav: 
						type: 'group'
						active: 'members'
						urlPrefix: group
					viewOptions: 
						group: new Group({name: group})
				@router.registerUserRoute 'repositoryNew', (groupName) -> @showView 
					view: 'repository/Create'
					title: 'New repository' 
					viewOptions: 
						groupName: groupName
				@router.registerUserRoute 'repositoryInfo', (group, name) -> @showView 
					view: 'repository/Repository'
					title: "#{group}/#{name}"
					nav: 
						type: 'repository'
						active: 'repository'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryDatasets', (group, name, categoryId) -> @showView 
					view: 'repository/Datasets'
					title: "#{group}/#{name} | Data sets"
					nav: 
						type: 'repository'
						active: 'datasets'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						categoryId: categoryId
				@router.registerUserRoute 'repositoryDataset', (group, name, type, refId, commitId) -> @showView 
					view: 'repository/Dataset'
					title: "#{group}/#{name} | Data sets"
					nav: 
						type: 'repository'
						active: 'datasets'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						type: type
						refId: refId
						commitId: commitId
				@router.registerUserRoute 'repositoryCommits', (group, name) -> @showView 
					view: 'repository/Commits'
					title: "#{group}/#{name} | Commits"
					nav: 
						type: 'repository'
						active: 'commits'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryCommit', (group, name, commitId) -> @showView 
					view: 'repository/Commit'
					title: "#{group}/#{name} | Commits"
					nav: 
						type: 'repository'
						active: 'commits'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						commitId: commitId
				@router.registerUserRoute 'repositoryMembers', (group, name) -> @showView 
					view: 'members/Members'
					title: "#{group}/#{name} | Members"
					nav: 
						type: 'repository'
						active: 'members'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
			constructor: Controller

			initialize: (router) ->
				@router = router
				router.routeContext = @
				Events.setRouter router
				$('#main .center').empty();
				$('a').on 'click', (event) -> Events.followLink event
				@initializeNavigation()
				@initializeUserMenu()
				@registerRoutes()

			splitQuery: (query) ->
				unless query
					return {}
				params = query.split '&'
				result = {}
				for param in params
					param = param.split '='
					result[param[0]] = param[1]
				return result

			checkGroupOrRepositoryExists: (options, callback) ->
				if options.viewOptions?.repository
					Model.fetch options.viewOptions.repository,
						force: true
						success: callback
				else if options.viewOptions?.group
					Model.fetch options.viewOptions.group,
						force: true
						success: callback
				else
					callback?()

			getDocumentTitle: (value) ->
				unread = 0
				if window.WebSocket
					unread = conversations.getUnreadMessages()
				if value.indexOf('|') is -1 
					if unread
						return "(#{unread}) #{value} | LCA Collaboration Server"
					else
						return "#{value} | LCA Collaboration Server"
				split = value.split '|'
				reversed = 'LCA Collaboration Server'
				for v in split
					reversed = "#{v} | #{reversed}"
				if unread
					return "(#{unread}) #{reversed}"
				return reversed

			showView: (options) ->
				@checkGroupOrRepositoryExists options, () =>
					$('#main .center').empty()
					$('#header-title').html options.title.replace('|', '-')
					$('#header-title').attr 'title', options.title.replace('|', '-')
					document.title = @getDocumentTitle options.title
					if typeof options.nav is 'string'
						options.nav = {type: options.nav}
					@navigation.setItems options.nav?.type, @getNav(options.nav), options.nav?.active, options.viewOptions?.repository?.toJSON(),
					require ["cs!views/#{options.view}"], (View) =>
						view = new View options.viewOptions
						view.render
							container: '#main .center'

			showError: (statuscode) ->
				Layers.hideProgressIndicator()
				$('#header-title').empty()
				@navigation.setItems []
				message = localStorage?.getItem?('errorMessage')
				localStorage?.removeItem?('errorMessage')
				isStacktrace = (!statuscode or statuscode is 500) and currentUser.isAdmin()
				unless message
					message = if statuscode is 404 then 'Sorry, the page your were looking for could not be found.' else 'Unexpected error'
					isStacktrace = false
				if isStacktrace
					message = @toStacktrace message
				$('#main .center').html errorTemplate
					imageSrc: (if statuscode is 403 then 'images/403.png' else 'images/404.png')
					stacktrace: isStacktrace
					statuscode: statuscode
					errorMessage: message
				$('.select-text').on 'click', (e) ->
					text = $('.error-message')[0]
					if document.body.createTextRange
						range = document.body.createTextRange()
						range.moveToElementText text
						range.select()
					else if window.getSelection
						selection = window.getSelection()
						range = document.createRange()
						range.selectNodeContents text
						selection.removeAllRanges()
						selection.addRange range

			toStacktrace: (message) ->
				message = message.replace /\n/g, '<br> &nbsp; &nbsp; '
				return message

		)()

)