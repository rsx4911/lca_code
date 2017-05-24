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
				callback.apply context, args

			rewriteIfNecessary: (route) ->
				fragment = @routeRewrites[route]
				if fragment and Backbone.history.fragment isnt fragment
					@router.navigate fragment,
						trigger: false
						replace: true

			constructor: Router

			initialize: (userRoutes) ->
				AppRouter = Backbone.Router.extend
					routes: Routes
				@userRoutes = userRoutes
				@userRoutes.push ''
				@routeRewrites = {}
				@router = new AppRouter

			registerRouteRewrite: (route, fragment) ->
				@routeRewrites[route] = fragment

			registerUserRoute: (route, callback) ->
				wrappedCallback = () =>
					if !currentUser.isLoggedIn() and $.inArray(Backbone.history.fragment, @userRoutes) isnt -1
						window.location.href = 'login'
						return
					@rewriteIfNecessary route
					@checkAccess route, callback, @routeContext, null, arguments 
				@router.on "route:#{route}", wrappedCallback

			registerAdminRoute: (route, callback) ->
				wrappedCallback = () =>
					@rewriteIfNecessary route
					@checkAccess route, callback, @routeContext, 'admin', arguments 
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