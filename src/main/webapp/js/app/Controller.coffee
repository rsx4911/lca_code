define([
				'cs!app/Navigation'
				'cs!app/UserMenu'
				'cs!utils/Events'
				'cs!utils/Layouts'
				'cs!models/User'
			]
	
	(Navigation, UserMenu, Events, Layouts, User) ->

		Controller = () ->

		Controller:: = (() ->

			# private 

			getNav = (options) ->
				unless options
					return
				type = options.type
				prefix = if options.urlPrefix then "/#{options.urlPrefix}" else ''
				suffix = if options.urlSuffix then "/#{options.urlSuffix}" else ''
				switch type
					when 'user' then return [
						{href: "#{prefix}/user/profile#{suffix}", imageSrc: '/images/profile.png', label: 'Profile'}
					]
					when 'dashboard' then return [
						{href: "#{prefix}/dashboard/projects#{suffix}", imageSrc: '/images/projects.png', label: 'Projects'}
					]
					when 'admin' then return [
						{href: "#{prefix}/admin/overview#{suffix}", imageSrc: '/images/overview.png', label: 'Overview'}
					]

			initializeNavigation = () ->
				@navigation = new Navigation()
				@navigation.render 
					container: 'nav'

			initializeUserMenu = () ->
				new UserMenu().render 
					container: '#user-menu'

			registerRoutes = () ->
				(@_ registerRedirects)()
				(@_ registerAdminRoutes)()
				(@_ registerUserRoutes)()

			registerRedirects = () ->
				@router.registerRedirect 'defaultAction', '/dashboard/projects'
				@router.registerRedirect 'user', '/user/profile'
				@router.registerRedirect 'dashboard', '/dashboard/projects'
				@router.registerRedirect 'admin', '/admin/overview', 'admin'

			registerAdminRoutes = () ->
				@router.registerAdminRoute 'adminOverview', -> @showView 
					view: 'admin/Overview'
					title: 'Admin area'
					nav: 'admin'
				@router.registerAdminRoute 'adminUserNew', -> @showView 
					view: 'user/Profile'
					title: 'Admin area'
					viewOptions: 
						user: new User()
				@router.registerAdminRoute 'adminUserEdit', (username) -> @showView 
					view: 'user/Profile'
					title: 'Admin area'
					nav: 
						type: 'user'
						urlPrefix: 'admin'
						urlSuffix: username
					viewOptions: 
						user: new User {username: username}

			registerUserRoutes = () ->
				@router.registerUserRoute 'userProfile', -> @showView 
					view: 'user/Profile'
					title: 'User area'
					nav: 'user'
				@router.registerUserRoute 'dashboardProjects', -> @showView 
					view: 'dashboard/Projects'
					title: 'Projects' 
					nav: 'dashboard'

			# public

			constructor: Controller

			initialize: (router) ->
				@router = router
				router.routeContext = @
				Events.setRouter router
				$('#main').empty();
				$('a').on 'click', (event) -> Events.followLink event
				(@_ initializeNavigation)()
				(@_ initializeUserMenu)()
				(@_ registerRoutes)()

			showView: (options) ->
				$('#main').empty()
				$('#header-title').html options.title
				if typeof options.nav is 'string'
					options.nav = {type: options.nav}
				@navigation.setItems getNav options.nav
				Layouts.renderViewInLayout 'full-size',
					viewOptions: options.options
					views:
						center: options.view

			_: (callback) ->
				() =>
					callback.apply @, arguments

		)()

)