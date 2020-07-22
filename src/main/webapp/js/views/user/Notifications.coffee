define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Notifications'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/user/notifications'
			]

	(Backbone, Events, Notifications, Renderer, currentUser, template) ->

		class UserNotifications extends Backbone.View

			className: 'notifications-view multi-box-view'

			events: 
				'click a[href]': (event) -> Events.followLink event
				'click .check-all': (event) -> @setGroupState event, true
				'click .uncheck-all': (event) -> @setGroupState event, false
				'change input[type=checkbox]': 'changeNotificationState'

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/notifications'
					success: (enabled) =>
						notifications = Notifications.getAll()
						for notification in notifications
							notification.enabled = ($.inArray(notification.id, enabled) > -1)
						@$el.html template
							notifications: notifications
							isUserManager: currentUser.isUserManager()
						Renderer.render @, renderOptions

			changeNotificationState: (event) ->
				target = $ Events.target event
				notification = target.attr 'id'
				type = if target.is(':checked') then 'enable' else 'disable'
				$.ajax
					type: 'PUT'
					url: "ws/notifications/#{type}/#{notification}"

			setGroupState: (event, enable) ->
				target = $ Events.target event, 'img'
				group = target.attr 'data-group'
				list = ''
				for notification in Notifications.getAll()
					if notification.group is group
						checkbox = @$ "##{notification.id}"
						checkbox.prop 'checked', enable
						if list
							list += ','
						list += notification.id
				type = if enable then 'enable' else 'disable'
				$.ajax
					type: 'PUT'
					url: "ws/notifications/#{type}/#{list}"

)