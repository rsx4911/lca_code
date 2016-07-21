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
					when 403 then Router.navigate '/403',
						replace: true
					when 404 then Router.navigate '/404',
						replace: true
					# treat unsupported schema as not existing
					when 406 then Router.navigate '/404',
						replace: true

		initialize: () ->
			$.fn.extend
				animateCss: (animationName) ->
					animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend'
					$(@).addClass('animated ' + animationName).one animationEnd, () -> 
						$(@).removeClass 'animated ' + animationName
			@initializeErrorHandling()
			Model.fetch currentUser,
				success: () ->
					Router.initialize()
					Controller.initialize Router
					Backbone.history.start
						pushState: true

				
)