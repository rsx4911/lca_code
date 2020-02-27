define([
				'cs!app/Navigation'
				'cs!app/UserMenu'
				'cs!utils/Announcements'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Model'
				'cs!views/tasks/ReviewWidget'
				'cs!models/Repository'
				'cs!models/User'
				'cs!models/Group'
				'cs!models/Team'
				'cs!models/Conversations'
				'cs!models/CurrentUser'
				'cs!models/Settings'
				'templates/views/error'
			]
	
	(Navigation, UserMenu, Announcements, Events, Format, Layers, LocalStorage, Model, ReviewWidget, Repository, User, Group, Team, conversations, currentUser, settings, errorTemplate) ->

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
					when 'dashboard' 
						userMenu = []
						if settings.is('DASHBOARD_ACTIVITIES_ENABLED')
							userMenu.push {href: @concatUrl(prefix, 'dashboard/activities'), imageSrc: 'images/activities.png', label: 'Activities', id: 'activities'}
						userMenu.push {href: @concatUrl(prefix, 'dashboard/repositories'), imageSrc: 'images/repository.png', label: 'Repositories', id: 'repositories'}
						userMenu.push {href: @concatUrl(prefix, 'dashboard/groups'), imageSrc: 'images/group.png', label: 'Groups', id: 'groups'}
						return userMenu
					when 'tasks' then return [
						{href: @concatUrl(prefix, 'tasks'), imageSrc: 'images/tasks.png', label: 'Overview', id: 'overview'}
					]
					when 'messaging' then return [
						{href: @concatUrl(prefix, 'messages'), imageSrc: 'images/inbox.png', label: 'Inbox', id: 'inbox'}
					]
					when 'user'
						userMenu = []
						userMenu.push {href: @concatUrl(prefix, 'user/profile'), imageSrc: 'images/profile.png', label: 'Profile', id: 'profile'}
						if settings.is('DASHBOARD_ACTIVITIES_ENABLED') or settings.id('REPOSITORY_ACTIVITIES_ENABLED')
							userMenu.push {href: @concatUrl(prefix, 'user/activities'), imageSrc: 'images/activities.png', label: 'Activities', id: 'activities'}
						if settings.is('MESSAGING_ENABLED')
							userMenu.push {href: @concatUrl(prefix, 'user/messaging'), imageSrc: 'images/inbox.png', label: 'Messaging', id: 'messaging'}
						if settings.is('NOTIFICATIONS_ENABLED')
							userMenu.push {href: @concatUrl(prefix, 'user/notifications'), imageSrc: 'images/notifications.png', label: 'Notifications', id: 'notifications'}
						return userMenu
					when 'group'
						isUserspace = currentUser.isLoggedIn() && currentUser.get('username') is options.urlPrefix
						groupMenu = []
						groupMenu.push {href: @concatUrl(prefix, ''), imageSrc: 'images/group.png', label: 'Group', id: 'group'}
						unless isUserspace
							groupMenu.push {href: @concatUrl(prefix, 'members'), imageSrc: 'images/members.png', label: 'Members', id: 'members'}
						return groupMenu
					when 'repository' 
						repoMenu = []
						repoMenu.push {href: @concatUrl(prefix, ''), imageSrc: 'images/repository.png', label: 'Repository', id: 'repository'}
						if settings.is('REPOSITORY_ACTIVITIES_ENABLED')
							repoMenu.push {href: @concatUrl(prefix, 'activities'), imageSrc: 'images/activities.png', label: 'Activities', id: 'activities'}
						repoMenu.push {href: @concatUrl(prefix, 'datasets'), imageSrc: 'images/dataset.png', label: 'Data sets', id: 'datasets'}
						repoMenu.push {href: @concatUrl(prefix, 'commits'), imageSrc: 'images/commit.png', label: 'Commits', id: 'commits'}
						if settings.is('COMMENTS_ENABLED')
							repoMenu.push {href: @concatUrl(prefix, 'comments'), imageSrc: 'images/comments.png', label: 'Comments', id: 'comments'}
						repoMenu.push {href: @concatUrl(prefix, 'members'), imageSrc: 'images/members.png', label: 'Members', id: 'members'}
						return repoMenu
					when 'admin'
						adminMenu = []
						if currentUser.isUserManager()
							adminMenu.push {href: @concatUrl(prefix, 'administration/overview'), imageSrc: 'images/overview.png', label: 'Overview', id:'overview'}
						if currentUser.isAdmin()
							adminMenu.push {href: @concatUrl(prefix, 'administration/settings'), imageSrc: 'images/settings.png', label: 'Settings', id:'settings'}
						if currentUser.isDataManager()
							adminMenu.push {href: @concatUrl(prefix, 'administration/libraries'), imageSrc: 'images/libraries.png', label: 'Library data sets', id:'libraries'}
						return adminMenu

			initializeNavigation: () ->
				@navigation = new Navigation()
				@navigation.render 
					container: 'nav'
					noAnimation: true

			initializeReviewWidget: () ->
				activeReviewId = LocalStorage.getString "#{currentUser.get('username')}-active-review-task"
				unless activeReviewId
					return
				if @reviewWidget
					@reviewWidget.el.remove()
				@reviewWidget = new ReviewWidget
					reviewId: activeReviewId
				@reviewWidget.render
					container: 'body'
					append: true 
					noAnimation: true

			initializeUserMenu: () ->
				@userMenu = new UserMenu()
				@userMenu.render 
					container: '#user-menu'
					noAnimation: true

			initializeMaintenanceMode: () ->
				notification = $ '#maintenance-notification'
				if !notification.length
					$('body').append '<div id="maintenance-notification"></div>'
					notification = $ '#maintenance-notification'
				notification.html 'The server is currently in maintenance mode'
				if settings.is('MAINTENANCE_MODE')
					$('body').addClass 'maintenance-mode'

			initializeAnnouncements: () ->
				id = settings.getVal 'ANNOUNCEMENT_ID'
				message = settings.getVal 'ANNOUNCEMENT_MESSAGE'
				if message
					Announcements.announce message, id

			registerRoutes: () ->
				@registerRouteRewrites()
				@registerAdminRoutes()
				@registerUserRoutes()

			registerRouteRewrites: () ->
				if currentUser.isLoggedIn()
					@router.registerRouteRewrite 'landingPage', 'dashboard/activities'
					@router.registerRouteRewrite 'dashboardRepositories', 'dashboard/repositories'
					@router.registerRouteRewrite 'userProfile', 'user/profile'
					if currentUser.isDataManager() and !currentUser.isAdmin() and !currentUser.isUserManager()
						@router.registerRouteRewrite 'adminOverview', 'administration/libraries'
					else
						@router.registerRouteRewrite 'adminOverview', 'administration/overview'

			registerAdminRoutes: () ->
				@router.registerAdminRoute 'adminOverview', 'manager', -> @showView 
					view: 'admin/Overview'
					title: 'Admin area - Overview'
					nav:
						type: 'admin'
						active: 'overview'
				@router.registerAdminRoute 'adminUserNew', 'userManager', -> @showView 
					view: 'user/Profile'
					title: 'New profile'
					viewOptions: 
						user: new User()
						adminArea: true
				@router.registerAdminRoute 'adminUserEdit', 'userManager', (username) -> @showView 
					view: 'user/Profile'
					title: "Profile | #{username}"
					viewOptions: 
						user: new User {username: username}
						adminArea: true
				@router.registerAdminRoute 'adminTeamNew', 'userManager', -> @showView 
					view: 'team/Profile'
					title: 'New team'
					viewOptions: 
						team: new Team()
				@router.registerAdminRoute 'adminTeamEdit', 'userManager', (teamname) -> @showView 
					view: 'team/Profile'
					title: "Profile | #{teamname}"
					viewOptions: 
						team: new Team {teamname: teamname}
				@router.registerAdminRoute 'adminLibraries', 'dataManager', -> @showView 
					view: 'admin/Libraries'
					title: 'Admin area - Library data sets'
					nav:
						type: 'admin'
						active: 'libraries'
				@router.registerAdminRoute 'adminSettings', 'admin', -> @showView 
					view: 'admin/Settings'
					title: 'Admin area - Settings'
					nav:
						type: 'admin'
						active: 'settings'
				@router.registerAdminRoute 'adminMaintenance', 'admin', -> @showView 
					view: 'admin/Maintenance'
					title: 'Admin area - Maintenance'
					nav:
						type: 'admin'
						active: 'maintenance'

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
					fullWidth: true
					search: true
					viewOptions: @splitQuery query
				@router.registerUserRoute 'userProfile', -> @showView 
					view: 'user/Profile'
					title: 'Profile' 
					nav: 
						type: 'user'
						active: 'profile'
				@router.registerUserRoute 'userActivities', -> @showView 
					view: 'user/Activities'
					title: 'Activities' 
					nav: 
						type: 'user'
						active: 'activities'
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
				@router.registerUserRoute 'landingPage', -> 
					if currentUser.isLoggedIn()
						@showView 
							view: 'dashboard/Activities'
							title: 'Activities' 
							nav: 
								type: 'dashboard'
								active: 'activities'
					else
						@showView 
							view: 'Home'
							hideSearchBar: true
				@router.registerUserRoute 'dashboardActivities', -> @showView 
					view: 'dashboard/Activities'
					title: 'Activities' 
					nav: 
						type: 'dashboard'
						active: 'activities'
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
				@router.registerUserRoute 'tasks', () -> 
					@showView 
						view: 'tasks/Overview'
						title: 'Tasks'
						nav: 
							type: 'tasks'
							active: 'overview'
						viewOptions:
							userMenu: @userMenu
				@router.registerUserRoute 'reviewManage', (id) -> 
					@showView 
						view: 'tasks/ManageReview'
						title: 'Manage review task'
						viewOptions:
							reviewId: id
							userMenu: @userMenu
				@router.registerUserRoute 'messages', (username) -> 
					unless window.WebSocket
						@router.navigate 'error/404', {trigger: true, replace: true}
						return
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
					title: "#{group}"
					subTitle: 'Members'
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
				@router.registerUserRoute 'repositoryImport', (groupName) -> @showView 
					view: 'repository/Create'
					title: 'Import repository' 
					viewOptions: 
						doImport: true
						groupName: groupName
				@router.registerUserRoute 'repositoryImportJson', (groupName) -> @showView 
					view: 'repository/Create'
					title: 'Import repository'
					viewOptions: 
						doImport: true
						importFormat: 'json-ld'
						groupName: groupName
				@router.registerUserRoute 'repositoryImportExternal', () -> @showView 
					view: 'repository/Create'
					title: 'Import repository'
					viewOptions: 
						doImport: true
						importFormat: 'external'
				@router.registerUserRoute 'repositoryInfo', (group, name) -> 
					unless currentUser.isLoggedIn()
						@router.navigate "#{group}/#{name}/datasets",
							replace: true
						return
					@showView
						view: 'repository/Repository'
						title: "#{group}/#{name}"
						nav: 
							type: 'repository'
							active: 'repository'
							urlPrefix: "#{group}/#{name}"
						viewOptions: 
							repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryActivities', (group, name) ->
					@showView
						view: 'dashboard/Activities'
						title: "#{group}/#{name}"
						subTitle: 'Activities'
						nav: 
							type: 'repository'
							active: 'activities'
							urlPrefix: "#{group}/#{name}"
						viewOptions: 
							repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryDatasets', (group, name, categoryPath, commitId) -> @showView 
					view: 'repository/dataset/Datasets'
					title: "#{group}/#{name}"
					subTitle: 'Data sets'
					nav: 
						type: 'repository'
						active: 'datasets'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						categoryPath: categoryPath
						commitId: commitId
				@router.registerUserRoute 'repositoryDataset', (group, name, type, refId, query) -> 
					params = @splitQuery query
					@checkLicenseAgreement () =>
						@showView 
							view: 'repository/dataset/Dataset'
							title: "#{group}/#{name}"
							subTitle: 'Data sets'
							fullWidth: true
							nav: 
								type: 'repository'
								active: 'datasets'
								urlPrefix: "#{group}/#{name}"
							viewOptions: 
								repository: new Repository({group: group, name: name})
								type: type
								refId: refId
								commitId: params.commitId
								commentPath: params.commentPath
				@router.registerUserRoute 'repositoryCommits', (group, name) -> @showView 
					view: 'repository/commit/Commits'
					title: "#{group}/#{name}"
					subTitle: 'Commits'
					nav: 
						type: 'repository'
						active: 'commits'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryCommit', (group, name, commitId) -> @showView 
					view: 'repository/commit/Commit'
					title: "#{group}/#{name}"
					subTitle: 'Commits'
					nav: 
						type: 'repository'
						active: 'commits'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						commitId: commitId
				@router.registerUserRoute 'repositoryComments', (group, name) -> @showView 
					view: 'repository/Comments'
					title: "#{group}/#{name}"
					subTitle: 'Comments'
					nav: 
						type: 'repository'
						active: 'comments'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryMembers', (group, name) -> @showView 
					view: 'members/Members'
					title: "#{group}/#{name}"
					subTitle: 'Members'
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
				$('a:not([data-route=false]):not([target=_blank])').on 'click', (event) -> Events.followLink event
				if currentUser.isLoggedIn()
					$('body').removeClass 'public-mode'
					@initializeNavigation()
					@initializeReviewWidget()					
				@initializeUserMenu()
				@initializeAnnouncements()
				@initializeMaintenanceMode()
				@registerRoutes()

			splitQuery: (query) ->
				unless query
					return {}
				params = query.split '&'
				result = {}
				for param in params
					param = param.split '='
					if result[param[0]]
						result[param[0]] = [result[param[0]]]
						result[param[0]].push param[1]
					else 
						result[param[0]] = param[1]
				return result

			checkGroupOrRepositoryExists: (options, callback) ->
				if !options.search
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
				else
					callback?()
		
			checkLicenseAgreement: (callback) ->
				licenseAgreementText = settings.getVal 'LICENSE_AGREEMENT_TEXT'
				if !licenseAgreementText or localStorage.getItem('license-info-accepted') is 'true'
					callback()
					return
				Layers.askQuestion
					title: 'Licence Agreement Information'
					question: licenseAgreementText
					answers: ['I Disagree', 'I Agree']
					onAnswer: (index) => 
						Layers.closeActive()
						if index is 1
							localStorage.setItem('license-info-accepted', 'true')
							callback()
						else
							Backbone.history.history.back()

			getDocumentTitle: (value) ->
				unless value
					return 'LCA Collaboration Server'
				unread = 0
				if window.WebSocket and currentUser.isLoggedIn()
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
				$('#global-search-group').show()
				if options?.hideSearchBar
					$('#global-search-group').hide()
				$('#global-search').val options?.viewOptions?.query	
				@checkGroupOrRepositoryExists options, () =>
					$('#main .center').empty()
					if options.fullWidth
						$('#main .full-size').addClass 'full-width'
					else
						$('#main .full-size').removeClass 'full-width'
					title1 = options.title or ''
					title2 = options.title or ''
					if options.title and options.subTitle and currentUser.isLoggedIn()
						title1 += ' - ' + options.subTitle
						title2 += ' | ' + options.subTitle
					if currentUser.isLoggedIn()
						$('#header-title').html title1
					else
						$('#header-title').html '<a href="' + title1 + '">' + title1 + '</a>'
						$('#header-title a').on 'click', (event) -> Events.followLink event
					$('#header-title').attr 'title', title1
					document.title = @getDocumentTitle title2
					if currentUser.isLoggedIn()
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
				if @navigation
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