define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/navigation'
			]

	(Backbone, Events, Renderer, currentUser, template) ->

		class Navigation extends Backbone.View

			doRender: (items) ->
				@$el.html template
					username: currentUser.get 'username'
					name: currentUser.get 'name'
					items: items
					path: window.location.pathname

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event

			render: (renderOptions) ->
				@doRender()
				Renderer.render @, renderOptions

			setItems: (items) ->
				@doRender items

)