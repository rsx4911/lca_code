define([
				'cs!app/Navigation'
				'cs!app/UserMenu'
				'cs!utils/Events'
				'cs!utils/Layouts'
				'cs!models/User'
				'cs!models/Repository'
				'templates/views/404'
			]
	
	(Navigation, UserMenu, Events, Layouts, User, Repository, template404) ->

		Controller = () ->

		Controller:: = (() ->

			getNav: (options) ->
				unless options
					return
				type = options.type
				prefix = if options.urlPrefix then "/#{options.urlPrefix}" else ''
				# the ids are used in Navigation to identify which menu item is currently active
				# they need only to be unique within 'type'
				switch type
					when 'dashboard' then return [
						{href: "#{prefix}/dashboard/repositories", imageSrc: '/images/repository.png', label: 'Repositories', id: 'repositories'}
					]
					when 'user' then return [
						{href: "#{prefix}/user/profile", imageSrc: '/images/profile.png', label: 'Profile', id: 'profile'}
					]
					when 'repository' then return [
						{href: "#{prefix}", imageSrc: '/images/repository.png', label: 'Info', id: 'info'}
						{href: "#{prefix}/datasets", imageSrc: '/images/dataset.png', label: 'Data sets', id: 'datasets'}
						{href: "#{prefix}/commits", imageSrc: '/images/commit.png', label: 'Commits', id: 'commits'}
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
					title: 'Admin area - New profile'
					viewOptions: 
						user: new User()
						adminArea: true
				@router.registerAdminRoute 'adminUserEdit', (username) -> @showView 
					view: 'user/Profile'
					title: "Admin area - Profile '#{username}'"
					viewOptions: 
						user: new User {username: username}
						adminArea: true
				@router.registerAdminRoute 'adminRepositoryNew', -> @showView 
					view: 'repository/Create'
					title: 'Admin area - New repository'
					viewOptions: 
						adminArea: true
				@router.registerAdminRoute 'adminRepositoryInfo', (group, name) -> @showView 
					view: 'repository/Info'
					title: "Admin area - Repository '#{group}/#{name}'"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						adminArea: true

			registerUserRoutes: () ->
				@router.registerUserRoute 'notFound', -> @show404()
				@router.registerUserRoute 'userProfile', -> @showView 
					view: 'user/Profile'
					title: 'Profile'
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
						active: 'info'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryDatasets', (group, name, categoryId) -> @showView 
					view: 'repository/Datasets'
					title: "#{group}/#{name} - Data sets"
					nav: 
						type: 'repository'
						active: 'datasets'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
						categoryId: categoryId
				@router.registerUserRoute 'repositoryDataset', (group, name, type, refId, commitId) -> @showView 
					view: 'repository/Dataset'
					title: "#{group}/#{name} - Data sets"
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
					title: "#{group}/#{name} - Commits"
					nav: 
						type: 'repository'
						active: 'commits'
						urlPrefix: "#{group}/#{name}"
					viewOptions: 
						repository: new Repository({group: group, name: name})
				@router.registerUserRoute 'repositoryCommit', (group, name, commitId) -> @showView 
					view: 'repository/Commit'
					title: "#{group}/#{name} - Commits"
					nav: 
						type: 'repository'
						active: 'commits'
						urlPrefix: "#{group}/#{name}"
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
				@navigation.setItems @getNav(options.nav), options.nav?.active
				Layouts.renderViewInLayout 'full-size',
					viewOptions: options.viewOptions
					views:
						center: options.view

			show404: () ->
				console.log 123
				$('#header-title').empty()
				@navigation.setItems []
				$('#main').html template404()

		)()

)