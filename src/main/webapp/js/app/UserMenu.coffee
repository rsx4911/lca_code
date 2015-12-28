define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/user-menu'
			]

	(Backbone, Events, Renderer, currentUser, template) ->

		class UserMenu extends Backbone.View

			logout = (event) ->
				Events.preventDefault event
				$.ajax
					type: 'POST' 
					url: '/ws/public/logout'
					success: () ->
						window.location.href = '/login'

			events: 
				'click a[href]:not([target=_blank]):not(.logout)': (event) -> Events.followLink event
				'click a.logout': logout

			render: (renderOptions) ->
				@$el.html template 
					isAdmin: currentUser.isAdmin()
				Renderer.render @, renderOptions
				@$('[data-toggle=tooltip]').tooltip()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)