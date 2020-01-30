define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/FeedScroll'
				'cs!utils/Format'
				'cs!utils/Renderer'
				'templates/views/dashboard/activities'
				'templates/views/dashboard/activity'
			]

	(Backbone, Events, FeedScroll, Format, Renderer, template, resultTemplate) ->

		class DashboardActivities extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions
				new FeedScroll({
					url: 'ws/history/activities'
					container: '#activity-feed'
					template: resultTemplate
					pageSize: 5
					extendModel: (model) =>
						model.formatDate = Format.date
						model.formatTime = Format.time
				}).init()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)