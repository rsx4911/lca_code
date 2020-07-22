define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/FeedScroll'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/dashboard/activities'
				'templates/views/dashboard/activity'
			]

	(Backbone, Events, FeedScroll, Format, Forms, Renderer, currentUser, template, resultTemplate) ->

		class DashboardActivities extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'change .abc-checkbox input': 'rerender'

			initialize: (options) ->
				if options
					{@repository} = options

			render: (renderOptions) ->
				@$el.html template
					repository: if @repository then @repository.toJSON() else null
				Renderer.render @, renderOptions
				Forms.fill 'activities-config', currentUser.get 'settings'
				@initFeed()

			rerender: () ->
				@feed.destroy()
				@initFeed()

			initFeed: () ->
				@feed = new FeedScroll({
					url: () => 
						config = Forms.toJson 'activities-config'
						url = "ws/activities?&showCommitActivities=#{config.showCommitActivities}&showCommentActivities=#{config.showCommentActivities}&showTaskActivities=#{config.showTaskActivities}&"
						if @repository
							url += "repositoryPath=#{@repository.get('group')}/#{@repository.get('name')}&"
						return url
					container: '#activity-feed'
					template: resultTemplate
					pageSize: 5
					extendModel: (model) =>
						model.formatDate = Format.date
						model.formatTime = Format.time
						model.showRepositoryPath = !@repository
					onEmpty: () =>
						@$('#activity-feed').append('<div class="no-content-message">No activities found</div>')
				})
				@feed.init()

)