define([
				'backbone' 
				'cs!app/Controller'
				'cs!app/Router'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!models/CurrentUser'
				'cs!models/Conversations'
				'cs!models/Settings'
				'cs!app/DynamicDependencies'
			]

	(Backbone, Controller, Router, Layers, Model, currentUser, conversations, settings) ->

		getErrorMessage: (response) ->
			json = response.responseJSON
			if json
				if json.message
					return json.message
				if json.error
					repoPath = @getRepositoryPath json.path
					if response.status is 404 and repoPath
						return "No repository '#{repoPath}' found"
					return json.error
			if response.responseText
				return response.responseText
			return null

		getRepositoryPath: (fullPath) ->
			unless fullPath
				return null
			if fullPath.indexOf('/ws/repository/') isnt 0
				return null
			rest = fullPath.substring '/ws/repository/'.length
			if rest.indexOf('/') is -1
				return null
			while rest.indexOf('/') isnt rest.lastIndexOf('/')
				rest = rest.substring rest.indexOf('/') + 1
			return rest

		initializeErrorHandling: () ->
			$(document).ajaxError (event, response, options, error) =>
				switch response.status
					when 401
						unless currentUser.get('inLoginProcess')
							Layers.showLoginLayer()
					when 403
						if currentUser.isLoggedIn()
							localStorage?.setItem?('errorMessage', 'Sorry, but you do not have access to this page.')
						else
							window.location.href = 'login'
							return
					when 406
						localStorage?.setItem?('errorMessage', 'Sorry, the repository schema version is not compatible with the current collaboration server version.')
					else
						message = @getErrorMessage response
						localStorage?.setItem?('errorMessage', message)
				if response.status isnt 400 and response.status isnt 401 and response.status isnt 409
					Router.navigate "error/#{response.status}",
						replace: if options?.type is 'GET' then true else false
			window.onerror = (msg, url, line, col, error) =>
				if window.inErrorHandling
					return
				window.inErrorHandling = true
				$.ajax
					type: 'POST'
					url: 'ws/public/error'
					contentType: 'application/json'
					data: JSON.stringify({ message: msg, stacktrace: error.stack, path: Backbone.history.fragment })
					complete: () -> 
						if localStorage?.getItem?('debugMode') is 'true' or window.debugMode is 'true'
							localStorage?.setItem?('errorMessage', error.responseJSON?.trace or error)
							Router.navigate "error",
								replace: true
						window.inErrorHandling = false

		initializeLongLoading: () ->
			$.ajaxSetup
				beforeSend: (xhr) ->
					if Layers.isProgressShowing()
						return
					time = new Date().getTime()
					check = () ->
						setTimeout () ->
							if xhr.readyState is 4
								Layers.hideProgressIndicator()
							else
								check()
						, 50
					setTimeout () ->
						if xhr.readyState isnt 4
							Layers.showProgressIndicator()
							check()
					, 250


		initialize: () ->
			window.onfocus = () -> 
				window.isActive = true
			window.onblur = () -> 
				window.isActive = false
			$.expr.filters.offscreen = (el) ->
				rect = el.getBoundingClientRect()
				return ((rect.x + rect.width) < 0 || (rect.y + rect.height) < 0 || (rect.x > window.innerWidth || rect.y > window.innerHeight))
			$.fn.extend
				animateCss: (animationName) ->
					animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend'
					$(@).addClass('animated ' + animationName).one animationEnd, () -> 
						$(@).removeClass 'animated ' + animationName
			@initializeErrorHandling()
			@initializeLongLoading()
			$.ajax
				type: 'GET'
				url: 'ws/public/config/userRoutes'
				success: (userRoutes) =>
					@fetchModels () ->
						Router.initialize userRoutes
						Controller.initialize Router
						Backbone.history.start
							pushState: true
							root: $('base').attr('href') or '/'

		fetchModels: (callback) ->
			Model.fetch currentUser, success: () ->
				Model.fetch settings, success: () ->
					if currentUser.isLoggedIn() and settings.is('MESSAGING_ENABLED')
						Model.fetch conversations, success: () ->
							conversations.initSocket callback
					else
						callback()
				
)
