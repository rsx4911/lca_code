define([
				'backbone' 
				'cs!app/Controller'
				'cs!app/Router'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!models/CurrentUser'
				'cs!models/Conversations'
				#'cs!app/DynamicDependencies'
			]

	(Backbone, Controller, Router, Layers, Model, currentUser, conversations) ->

		initializeErrorHandling: () ->
			$(document).ajaxError (event, response, options, error) ->
				switch response.status
					when 401
						unless currentUser.get('inLoginProcess')
							Layers.showLoginLayer()
					when 403
						if currentUser.isLoggedIn()
							localStorage?.setItem?('errorMessage', 'Sorry, but you do not have access to this page.')
						else
							window.location.href = '/login'
							return
					when 406
						localStorage?.setItem?('errorMessage', 'Sorry, the repository schema version is not compatible with the current collaboration server version.')
					else
						localStorage?.setItem?('errorMessage', response.responseText)
				if response.status isnt 400 and response.status isnt 401 and response.status isnt 409
					Router.navigate "error/#{response.status}",
						replace: true
			window.onerror = (msg, url, line, col, error) ->
				if window.inErrorHandling
					return
				window.inErrorHandling = true
				$.ajax
					type: 'POST'
					url: 'ws/public/error'
					contentType: 'text/plain'
					data: error.stack
					complete: () -> 
						if localStorage?.getItem?('debugMode') is 'true' or window.debugMode is 'true'
							localStorage?.setItem?('errorMessage', error.stack)
							Router.navigate "error",
								replace: true
						window.inErrorHandling = false

		initialize: () ->
			window.onfocus = () -> 
				window.isActive = true
			window.onblur = () -> 
				window.isActive = false
			$.fn.extend
				animateCss: (animationName) ->
					animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend'
					$(@).addClass('animated ' + animationName).one animationEnd, () -> 
						$(@).removeClass 'animated ' + animationName
			@initializeErrorHandling()
			@fetchModels () ->
				$.ajax
					type: 'GET'
					url: 'ws/public/config/userRoutes'
					success: (userRoutes) ->
						Router.initialize userRoutes
						Controller.initialize Router
						Backbone.history.start
							pushState: true
							root: $('base').attr('href') or '/'

		fetchModels: (callback) ->
			Model.fetch currentUser, success: () ->
				if currentUser.isLoggedIn()
					Model.fetch conversations, success: () ->
						conversations.initSocket()
						callback()
				else
					callback()
				
)