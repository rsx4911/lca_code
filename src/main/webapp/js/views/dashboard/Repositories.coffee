define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'templates/views/dashboard/repositories'
			]

	(Backbone, Events, Renderer, template) ->

		class DashboardRepositories extends Backbone.View

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)