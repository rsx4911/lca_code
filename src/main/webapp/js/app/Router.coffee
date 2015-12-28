define([
				'backbone'
				'cs!app/Routes' 
				'cs!utils/Layers' 
				'cs!models/CurrentUser'
			]

	(Backbone, Routes, Layers, currentUser) ->

		Router = () ->

		Router:: = (() ->

			# private

			checkAccess = (route, callback, context, restrictedTo, args) ->
				if restrictedTo is 'admin' and !currentUser.isAdmin()
					alert 'This is a restricted area, you do not have permission to enter it'
					return @navigate ''
				callback.apply context, args

			# public

			constructor: Router

			initialize: () ->
				AppRouter = Backbone.Router.extend
					routes: Routes
				@router = new AppRouter

			registerRedirect: (route, url, restrictedTo) ->
				wrappedCallback = () =>
					(@_ checkAccess) route, (=> @navigate url), @, restrictedTo
				@router.on "route:#{route}", wrappedCallback

			registerUserRoute: (route, callback) ->
				wrappedCallback = () =>
					(@_ checkAccess) route, callback, @routeContext, null, arguments 
				@router.on "route:#{route}", wrappedCallback

			registerAdminRoute: (route, callback) ->
				wrappedCallback = () =>
					(@_ checkAccess) route, callback, @routeContext, 'admin', arguments 
				@router.on "route:#{route}", wrappedCallback

			navigate: (route, options) ->
				if options?.forceTrigger and Backbone.history.fragment is route
					Backbone.history.loadUrl route
				@router.navigate route, 
					trigger: true

			_: (callback) ->
				() =>
					callback.apply @, arguments

		)()
				
)