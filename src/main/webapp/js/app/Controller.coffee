define([
				'cs!app/Navigation'
				'cs!app/UserMenu'
				'cs!utils/Events'
				'cs!utils/Layouts'
				'cs!models/User'
				'cs!models/Repository'
			]
	
	(Navigation, UserMenu, Events, Layouts, User, Repository) ->

		Controller = () ->

		Controller:: = (() ->

			getNav: (options) ->
				unless options
					return
				type = options.type
				prefix = if options.urlPrefix then "/#{options.urlPrefix}" else ''
				suffix = if options.urlSuffix then "/#{options.urlSuffix}" else ''
				switch type
					when 'dashboard' then return [
						{href: "#{prefix}/dashboard/repositories#{suffix}", imageSrc: '/images/repository.png', label: 'Repositories'}
					]
					when 'user' then return [
						{href: "#{prefix}/user/profile#{suffix}", imageSrc: '/images/profile.png', label: 'Profile'}
					]
					when 'repository' then return [
						{href: "#{prefix}/repository#{suffix}", imageSrc: '/images/repository.png', label: 'Repository'}
						{href: "#{prefix}/repository/datasets#{suffix}", imageSrc: '/images/dataset.png', label: 'Data sets'}
						{href: "#{prefix}/repository/commits#{suffix}", imageSrc: '/images/commit.png', label: 'Commits'}
					]
					when 'admin' then return [
						{href: "#{prefix}/admin/overview#{suffix}", imageSrc: '/images/overview.png', label: 'Overview'}
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
				@router.registerRouteRewrite 'defaultAction', '/dashboard/repositories'
				@router.registerRouteRewrite 'dashboardRepositories', '/dashboard/repositories'
				@router.registerRouteRewrite 'userProfile', '/user/profile'
				@router.registerRouteRewrite 'adminOverview', '/admin/overview'

			registerAdminRoutes: () ->
				@router.registerAdminRoute 'adminOverview', -> @showView 
					view: 'admin/Overview'
					title: 'Admin area'
					nav: 'admin'
				@router.registerAdminRoute 'adminUserNew', -> @showView 
					view: 'user/Profile'
					title: 'Admin area'
					viewOptions: 
						user: new User()
						adminArea: true
				@router.registerAdminRoute 'adminUserEdit', (username) -> @showView 
					view: 'user/Profile'
					title: 'Admin area'
					nav: 
						type: 'user'
						urlPrefix: 'admin'
						urlSuffix: username
					viewOptions: 
						user: new User {username: username}
						adminArea: true
				@router.registerAdminRoute 'adminRepositoryNew', -> @showView 
					view: 'repository/Create'
					title: 'New repository'
					viewOptions: 
						adminArea: true
				@router.registerAdminRoute 'adminRepositoryInfo', (group, name) -> @showView 
					view: 'repository/Info'
					title: "#{group}/#{name}"
					nav: 
						type: 'repository'
						urlPrefix: 'admin'
						urlSuffix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						adminArea: true

			registerUserRoutes: () ->
				@router.registerUserRoute 'userProfile', -> @showView 
					view: 'user/Profile'
					title: 'User area'
					nav: 'user'
				@router.registerUserRoute 'dashboardRepositories', -> 
					@showView 
						view: 'dashboard/Repositories'
						title: 'Repositories' 
						nav: 'dashboard'
				@router.registerUserRoute 'repositoryNew', -> @showView 
					view: 'repository/Create'
					title: 'New repository' 
				@router.registerUserRoute 'repositoryInfo', (group, name) -> @showView 
					view: 'repository/Info'
					title: "#{group}/#{name}"
					nav: 
						type: 'repository'
						urlSuffix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryDatasets', (group, name, categoryId) -> @showView 
					view: 'repository/Datasets'
					title: "#{group}/#{name} - Data sets"
					nav: 
						type: 'repository'
						urlSuffix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						categoryId: categoryId
				@router.registerUserRoute 'repositoryDataset', (group, name, type, refId, commitId) -> @showView 
					view: 'repository/Dataset'
					title: "#{group}/#{name} - Data sets"
					nav: 
						type: 'repository'
						urlSuffix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						type: type
						refId: refId
						commitId: commitId
				@router.registerUserRoute 'repositoryCommits', (group, name) -> @showView 
					view: 'repository/Commits'
					title: "#{group}/#{name} - Commits"
					nav: 
						type: 'repository'
						urlSuffix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryCommit', (group, name, commitId) -> @showView 
					view: 'repository/Commit'
					title: "#{group}/#{name} - Commits"
					nav: 
						type: 'repository'
						urlSuffix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						commitId: commitId

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

			showView: (options) ->
				$('#main').empty()
				$('#header-title').html options.title
				if typeof options.nav is 'string'
					options.nav = {type: options.nav}
				@navigation.setItems @getNav options.nav
				Layouts.renderViewInLayout 'full-size',
					viewOptions: options.viewOptions
					views:
						center: options.view

		)()

)