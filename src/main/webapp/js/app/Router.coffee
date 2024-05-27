define([
				'backbone'
				'cs!app/Routes' 
				'cs!utils/Layers' 
				'cs!models/CurrentUser'
			]

	(Backbone, Routes, Layers, currentUser) ->

		Router = () ->

		Router:: = (() ->

			checkAccess: (route, callback, context, restrictedTo, args) ->
				if restrictedTo is 'admin' and !currentUser.isAdmin()
					alert 'This is a restricted area, you do not have permission to enter it'
					return @navigate ''
				if restrictedTo is 'userManager' and !currentUser.isUserManager()
					alert 'This is a restricted area, you do not have permission to enter it'
					return @navigate ''
				if restrictedTo is 'dataManager' and !currentUser.isDataManager()
					alert 'This is a restricted area, you do not have permission to enter it'
					return @navigate ''
				if restrictedTo is 'libraryManager' and !currentUser.isLibraryManager()
					alert 'This is a restricted area, you do not have permission to enter it'
					return @navigate ''
				if restrictedTo is 'manager' and !currentUser.isDataManager() and !currentUser.isUserManager() and !currentUser.isLibraryManager()
					alert 'This is a restricted area, you do not have permission to enter it'
					return @navigate ''
				callback.apply context, args

			rewriteIfNecessary: (route) ->
				fragment = @routeRewrites[route]
				if !fragment or Backbone.history.fragment is fragment
					return false
				@router.navigate fragment,
					trigger: true
					replace: true
				return true

			constructor: Router

			initialize: (userRoutes) ->
				AppRouter = Backbone.Router.extend
					routes: Routes.init()
				@userRoutes = userRoutes
				@routeRewrites = {}
				@router = new AppRouter

			registerRouteRewrite: (route, fragment) ->
				@routeRewrites[route] = fragment

			registerUserRoute: (route, callback) ->
				wrappedCallback = () =>
					if !currentUser.isLoggedIn() and $.inArray(Backbone.history.fragment, @userRoutes) isnt -1
						window.location.href = 'login'
						return
					if @rewriteIfNecessary route
						return
					@checkAccess route, callback, @routeContext, null, arguments 
				@router.on "route:#{route}", wrappedCallback

			registerAdminRoute: (route, type, callback) ->
				wrappedCallback = () =>
					if @rewriteIfNecessary route
						return
					@checkAccess route, callback, @routeContext, type, arguments 
				@router.on "route:#{route}", wrappedCallback

			navigate: (route, options) ->
				if options?.forceTrigger and Backbone.history.fragment is route
					Backbone.history.loadUrl route
				trigger = true
				if options?.trigger is false
					trigger = false
				@router.navigate route, 
					trigger: trigger
					replace: options?.replace

		)()
				
)