define([
				'backbone' 
				'cs!app/Controller'
				'cs!app/Router'
				'cs!utils/LocalStorage'
				'cs!utils/Model'
				'cs!models/CurrentUser'
				'cs!app/DynamicDependencies'
			]

	(Backbone, Controller, Router, LocalStorage, Model, currentUser) ->

		initializeErrorHandling = () ->
			$(document).ajaxError (event, response, options, error) ->
				# specific errors must be handled, this only checks if the error is related to a session timeout
				if response.status is 401
					window.location.href = '/login'
				else if response.status is 403
					alert 'This is a restricted action, you do not have permission to execute it'


		initialize: () ->
			initializeErrorHandling()
			Model.fetch currentUser,
				success: () ->
					LocalStorage.initialize()
					Router.initialize()
					Controller.initialize Router
					Backbone.history.start
						pushState: true

				
)