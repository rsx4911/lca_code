define([
				'cs!app/Navigation'
				'cs!app/UserMenu'
				'cs!utils/Events'
				'cs!utils/Layouts'
				'cs!utils/Model'
				'cs!models/Repository'
				'cs!models/User'
				'cs!models/Group'
				'cs!models/Team'
				'templates/views/403'
				'templates/views/404'
			]
	
	(Navigation, UserMenu, Events, Layouts, Model, Repository, User, Group, Team, template403, template404) ->

		Controller = () ->

		Controller:: = (() ->

			getNav: (options) ->
				unless options
					return
				type = options.type
				prefix = if options.urlPrefix then "/#{options.urlPrefix}" else ''
				if type is 'group'
					prefix = if options.urlPrefix then "/groups/#{options.urlPrefix}" else '/groups'
				# the ids are used in Navigation to identify which menu item is currently active
				# they need only to be unique within 'type'
				switch type
					when 'dashboard' then return [
						{href: "#{prefix}/dashboard/repositories", imageSrc: '/images/repository.png', label: 'Repositories', id: 'repositories'}
						{href: "#{prefix}/dashboard/groups", imageSrc: '/images/group.png', label: 'Groups', id: 'groups'}
					]
					when 'user' then return [
						{href: "#{prefix}/user/profile", imageSrc: '/images/profile.png', label: 'Profile', id: 'profile'}
						{href: "#{prefix}/user/notifications", imageSrc: '/images/notifications.png', label: 'Notifications', id: 'notifications'}
					]
					when 'group' then return [
						{href: "#{prefix}", imageSrc: '/images/group.png', label: 'Group', id: 'group'}
						{href: "#{prefix}/members", imageSrc: '/images/members.png', label: 'Members', id: 'members'}
					]
					when 'repository' then return [
						{href: "#{prefix}", imageSrc: '/images/repository.png', label: 'Repository', id: 'repository'}
						{href: "#{prefix}/datasets", imageSrc: '/images/dataset.png', label: 'Data sets', id: 'datasets'}
						{href: "#{prefix}/commits", imageSrc: '/images/commit.png', label: 'Commits', id: 'commits'}
						{href: "#{prefix}/members", imageSrc: '/images/members.png', label: 'Members', id: 'members'}
					]
					when 'admin' then return [
						{href: "#{prefix}/administration/overview", imageSrc: '/images/overview.png', label: 'Overview', id:'overview'}
					]

			initializeNavigation: () ->
				@navigation = new Navigation()
				@navigation.render 
					container: 'nav'

			initializeUserMenu: () ->
				new UserMenu().render 
					container: '#user-menu'

			registerRoutes: () ->
				@registerRouteRewrites()
				@registerAdminRoutes()
				@registerUserRoutes()

			registerRouteRewrites: () ->
				@router.registerRouteRewrite 'dashboardRepositories', '/dashboard/repositories'
				@router.registerRouteRewrite 'userProfile', '/user/profile'
				@router.registerRouteRewrite 'adminOverview', '/administration/overview'

			registerAdminRoutes: () ->
				@router.registerAdminRoute 'adminOverview', -> @showView 
					view: 'admin/Overview'
					title: 'Admin area'
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

			registerUserRoutes: () ->
				@router.registerUserRoute 'notFound', -> @show404()
				@router.registerUserRoute 'noAccess', -> @show403()
				@router.registerUserRoute 'userProfile', -> @showView 
					view: 'user/Profile'
					title: 'Profile' 
					nav: 
						type: 'user'
						active: 'profile'
				@router.registerUserRoute 'search', (query) => @showView 
					view: 'search/Results'
					title: 'Search' 
					viewOptions: @splitQuery query
				@router.registerUserRoute 'userNotifications', -> @showView 
					view: 'user/Notifications'
					title: 'Notifications' 
					nav: 
						type: 'user'
						active: 'notifications'
				@router.registerUserRoute 'dashboardRepositories', -> 
					@showView 
						view: 'dashboard/Repositories'
						title: 'Repositories' 
						nav: 
							type: 'dashboard'
							active: 'repositories'
				@router.registerUserRoute 'dashboardGroups', -> 
					@showView 
						view: 'dashboard/Groups'
						title: 'Groups' 
						nav: 
							type: 'dashboard'
							active: 'groups'
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
				$('#main').empty();
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
				if value.indexOf('|') is -1 
					return "#{value} | LCA Cloud"
				split = value.split '|'
				reversed = 'LCA Cloud'
				for v in split
					reversed = "#{v} | #{reversed}"
				return reversed

			showView: (options) ->
				@checkGroupOrRepositoryExists options, () =>
					$('#main').empty()
					$('#header-title').html options.title.replace('|', '-')
					document.title = @getDocumentTitle options.title
					if typeof options.nav is 'string'
						options.nav = {type: options.nav}
					@navigation.setItems @getNav(options.nav), options.nav?.active, options.viewOptions?.repository?.toJSON(),
					Layouts.renderViewInLayout 'full-size',
						viewOptions: options.viewOptions
						views:
							center: options.view

			show404: () ->
				$('#header-title').empty()
				@navigation.setItems []
				$('#main').html template404()

			show403: () ->
				$('#header-title').empty()
				@navigation.setItems []
				$('#main').html template403()

		)()

)