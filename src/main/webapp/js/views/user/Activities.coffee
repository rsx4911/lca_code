define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/user/activities'
			]

	(Backbone, Events, Forms, Model, Renderer, currentUser, template) ->

		class UserActivities extends Backbone.View

			className: 'user-activities-view multi-box-view'

			events:
				'change #settings-form': 'saveSettings'

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions
				Forms.fill 'settings-form', currentUser.get('settings')

			saveSettings: (event) ->
				target = $ Events.target event
				setting = target.attr 'id'
				$.ajax
					type: 'PUT'
					url: 'ws/activities/settings'
					data: JSON.stringify Forms.toJson 'settings-form'
					contentType: 'application/json'
					success: (settings) ->
						Model.copyFields settings, currentUser.get('settings')

)