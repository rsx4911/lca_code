define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/user-menu'
			]

	(Backbone, Events, Renderer, Router, currentUser, template) ->

		class UserMenu extends Backbone.View

			logout: (event) ->
				Events.preventDefault event
				$.ajax
					type: 'POST' 
					url: '/ws/public/logout'
					success: () -> window.location.href = '/login'
					error: () -> window.location.href = '/login'

			onSearchKeyUp: (event) ->
				if Events.keyCode(event) isnt 13
					return
				input = $ Events.target event, 'input'
				query = input.val()
				input.val ''
				Router.navigate "/search/#{query}"

			events: 
				'click a[href]:not([target=_blank]):not(.logout)': (event) -> Events.followLink event
				'click a.logout': (event) -> @logout event
				'keyup #global-search': (event) -> @onSearchKeyUp event

			render: (renderOptions) ->
				@$el.html template 
					isAdmin: currentUser.isAdmin()
				Renderer.render @, renderOptions
				@$('[data-toggle=tooltip]').tooltip()

)