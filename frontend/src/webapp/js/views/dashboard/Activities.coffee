define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/FeedScroll'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/dashboard/activities'
				'templates/views/dashboard/activity'
				'templates/views/repository/commit/commit-info'
			]

	(Backbone, Events, FeedScroll, Format, Forms, Model, Renderer, currentUser, template, resultTemplate, infoTemplate) ->

		class DashboardActivities extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'change .abc-checkbox input': 'changeFeedSetting'

			initialize: (options) ->
				if options
					{@repository} = options

			render: (renderOptions) ->
				@$el.html template
					repository: if @repository then @repository.toJSON() else null
				Renderer.render @, renderOptions
				settings = currentUser.get 'settings'
				if !settings
					settings = {}
				if !settings.showCommitActivities and !settings.showCommentActivities and !settings.showTaskActivities
					@updateSettings {}, () =>
						Forms.fill 'activities-config', settings
						@initFeed()
				else
					Forms.fill 'activities-config', settings
					@initFeed()


			changeFeedSetting: () ->
				@updateSettings Forms.toJson('activities-config'), () =>
					@feed.destroy()
					@initFeed()

			updateSettings: (settings, callback) ->
				if !settings.showCommitActivities and !settings.showCommentActivities and !settings.showTaskActivities
					settings = { showCommitActivities: true, showCommentActivities: true, showTaskActivities: true }
					@$('.abc-checkbox input').prop 'checked', true
				$.ajax
					type: 'PUT'
					url: 'ws/activities/settings'
					data: JSON.stringify settings
					contentType: 'application/json'
					success: (settings) =>
						Model.copyFields settings, currentUser.get('settings')
						if callback
							callback()

			initFeed: () ->
				settings = currentUser.get('settings')
				@feed = new FeedScroll({
					url: () => 
						url = "ws/activities?&showCommitActivities=#{settings.showCommitActivities}&showCommentActivities=#{settings.showCommentActivities}&showTaskActivities=#{settings.showTaskActivities}&"
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
					afterRender: (entry) =>
						if entry.type is 'COMMIT'
							setTimeout () =>
								$.get "ws/public/history/count/#{entry.repositoryPath}/#{entry.id}", (count) =>
									$(".commit-info-container[data-commit-id=#{count.id}]").html infoTemplate count
							, 10
					onEmpty: () =>
						@$('#activity-feed').append('<div class="no-content-message">No activities found</div>')
				})
				@feed.init()

)