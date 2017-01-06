define([
				'backbone' 
				'cs!app/Controller'
				'cs!app/Router'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!models/CurrentUser'
				'cs!models/Conversations'
				'cs!app/DynamicDependencies'
			]

	(Backbone, Controller, Router, Layers, Model, currentUser, conversations) ->

		initializeErrorHandling: () ->
			$(document).ajaxError (event, response, options, error) ->
				switch response.status
					when 401
						unless currentUser.get('inLoginProcess')
							Layers.showLoginLayer()
					when 403 then Router.navigate '/403',
						replace: true
					when 404 then Router.navigate '/404',
						replace: true
					# treat unsupported schema as not existing
					when 406 then Router.navigate '/404',
						replace: true

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
			Model.fetchAll [currentUser, conversations], () ->
				conversations.initSocket()
				Router.initialize()
				Controller.initialize Router
				Backbone.history.start
					pushState: true

				
)