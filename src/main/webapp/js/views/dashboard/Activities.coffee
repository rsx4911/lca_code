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

			initialize: (options) ->
				if options
					{@repository} = options

			render: (renderOptions) ->
				@$el.html template
					repository: if @repository then @repository.toJSON() else null
				Renderer.render @, renderOptions
				url = 'ws/activities?'
				if @repository
					url += "repositoryPath=#{@repository.get('group')}/#{@repository.get('name')}&"
				new FeedScroll({
					url: url
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