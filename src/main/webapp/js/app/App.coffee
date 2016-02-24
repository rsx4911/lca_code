define([
				'backbone' 
				'cs!app/Controller'
				'cs!app/Router'
				'cs!utils/Model'
				'cs!models/CurrentUser'
				'cs!app/DynamicDependencies'
			]

	(Backbone, Controller, Router, Model, currentUser) ->

		initializeErrorHandling: () ->
			$(document).ajaxError (event, response, options, error) ->
				switch response.status
					when 401 then window.location.href = '/login'
					when 403 then 'This is a restricted action, you do not have permission to execute it'
					when 404 then Router.navigate '/404',
						trigger: false
						replace: true

		initialize: () ->
			@initializeErrorHandling()
			Model.fetch currentUser,
				success: () ->
					Router.initialize()
					Controller.initialize Router
					Backbone.history.start
						pushState: true

				
)