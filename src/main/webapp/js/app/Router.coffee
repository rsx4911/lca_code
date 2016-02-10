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

			initialize: () ->
				AppRouter = Backbone.Router.extend
					routes: Routes
				@routeRewrites = {}
				@router = new AppRouter

			registerRouteRewrite: (route, fragment) ->
				@routeRewrites[route] = fragment

			registerUserRoute: (route, callback) ->
				wrappedCallback = () =>
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
				@router.navigate route, 
					trigger: true

		)()
				
)