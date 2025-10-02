define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/FeedScroll'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'cs!models/Settings'
				'templates/views/dashboard/activities'
				'templates/views/dashboard/activity'
				'templates/views/repository/commit/commit-info'
			]

	(Backbone, Events, FeedScroll, Format, Forms, Model, Renderer, currentUser, settings, template, resultTemplate, infoTemplate) ->

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
					commentsEnabled: settings.is('COMMENTS_ENABLED')
					tasksEnabled: settings.is('TASKS_ENABLED')
					repository: if @repository then @repository.toJSON() else null
				Renderer.render @, renderOptions
				userSettings = currentUser.get 'settings'
				if !userSettings
					userSettings = {}
				if !userSettings.showCommitActivities and !userSettings.showCommentActivities and !userSettings.showTaskActivities
					@updateUserSettings {}, () =>
						Forms.fill 'activities-config', userSettings
						@initFeed()
				else
					Forms.fill 'activities-config', userSettings
					@initFeed()


			changeFeedSetting: () ->
				@updateUserSettings Forms.toJson('activities-config'), () =>
					@feed.destroy()
					@initFeed()

			updateUserSettings: (userSettings, callback) ->
				if !userSettings.showCommitActivities and !userSettings.showCommentActivities and !userSettings.showTaskActivities
					userSettings = { showCommitActivities: true, showCommentActivities: settings.is('COMMENTS_ENABLED'), showTaskActivities: settings.is('TASKS_ENABLED') }
					@$('.abc-checkbox input').prop 'checked', true
				$.ajax
					type: 'PUT'
					url: 'ws/activities/userSettings'
					data: JSON.stringify userSettings
					contentType: 'application/json'
					success: (userSettings) =>
						Model.copyFields userSettings, currentUser.get('settings')
						if callback
							callback()

			initFeed: () ->
				userSettings = currentUser.get('settings')
				@feed = new FeedScroll({
					url: () => 
						url = "ws/activities?&showCommitActivities=#{userSettings.showCommitActivities}&showCommentActivities=#{userSettings.showCommentActivities}&showTaskActivities=#{userSettings.showTaskActivities}&"
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