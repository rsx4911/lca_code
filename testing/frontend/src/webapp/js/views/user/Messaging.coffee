define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!models/CurrentUser'
				'templates/views/user/messaging'
			]

	(Backbone, Events, Forms, Model, Renderer, Status, currentUser, template) ->

		class UserMessaging extends Backbone.View

			className: 'user-messaging-view multi-box-view'

			events:
				'change #settings-form': 'saveSettings'
				'click .unblock': 'unblockUser'

			render: (renderOptions) ->
				@$el.html template
					blockedUsers: currentUser.get('settings').blockedUsers
				Renderer.render @, renderOptions
				Forms.fill 'settings-form', currentUser.get('settings')
				@updateSettings()

			saveSettings: (event) ->
				target = $ Events.target event
				setting = target.attr 'id'
				if setting is 'messagingEnabled'
					@updateSettings()
				$.ajax
					type: 'PUT'
					url: 'ws/messaging/settings'
					data: JSON.stringify Forms.toJson 'settings-form'
					contentType: 'application/json'
					success: (settings) ->
						Model.copyFields settings, currentUser.get('settings')

			unblockUser: (event) ->
				target = $ Events.target event, 'button'
				username = target.attr 'data-username'
				$.ajax
					type: 'PUT'
					url: "ws/messaging/unblock/#{username}"
					success: () =>
						target.parent().remove()
						unless @$('.unblock').length
							@$('#blocked-users').append "<span><i>Currently you haven't blocked any users</i></span>"
						newList = []
						for user in currentUser.get('settings').blockedUsers
							unless user.username is username
								newList.push user
						currentUser.get('settings').blockedUsers = newList
						Status.success 'Successfully unblocked user'
					error: (response) -> Status.error response.responseText

			updateSettings: () ->	
				@$('#messagingEnabled').prop 'disabled', false
				@$('#showOnlineStatus').prop 'disabled', false
				@$('#showReadReceipt').prop 'disabled', false
				if !@$('#messagingEnabled').is(':checked')
					@$('#showOnlineStatus').prop 'disabled', true
					@$('#showReadReceipt').prop 'disabled', true

)