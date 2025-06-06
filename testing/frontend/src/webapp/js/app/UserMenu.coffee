define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Renderer'
				'cs!models/Conversations'
				'cs!models/CurrentUser'
				'cs!models/Settings'
				'templates/views/user-menu'
			]

	(Backbone, Events, Layers, LocalStorage, Renderer, conversations, currentUser, settings, template) ->

		class UserMenu extends Backbone.View

			logout: (event) ->
				conversations.closeSocket()
				Events.preventDefault event
				$.ajax
					type: 'POST' 
					url: 'ws/public/logout'
					success: () -> window.location.href = ''
					error: () -> window.location.href = ''

			toggleDebug: (event) ->
				Events.preventDefault event
				debugMode = LocalStorage.toggleValue 'debugMode'
				icon = $ '.toggle-debug .glyphicon'
				if debugMode
					icon.removeClass 'glyphicon-eye-close'
					icon.addClass 'glyphicon-eye-open'
					icon.attr 'title', 'Debugging on'
				else
					icon.removeClass 'glyphicon-eye-open'
					icon.addClass 'glyphicon-eye-close'
					icon.attr 'title', 'Debugging off'
				icon.tooltip('fixTitle').tooltip 'show'

			toggleReview: (event) ->
				Events.preventDefault event
				reviewMode = LocalStorage.toggleValue 'reviewMode'
				icon = $ '.toggle-review .glyphicon'
				if reviewMode
					icon.attr 'title', 'Review mode on'
					icon.attr 'data-active', 'data-active'
				else
					icon.attr 'title', 'Review mode off'
					icon.removeAttr 'data-active'
				icon.tooltip('fixTitle').tooltip 'show'
				Backbone.history.loadUrl()

			events: 
				'click a[href]:not([target=_blank]):not(.login):not(.logout):not([data-action])': (event) -> Events.followLink event
				'click a.logout': (event) -> @logout event
				'click a.toggle-debug': (event) -> @toggleDebug event
				'click a.toggle-review': (event) -> @toggleReview event
				'click a[data-action=upgrade]': (event) -> @openUpgradeDialog event

			initialize: (options) ->
				@separateSearch = options.separateSearch
				conversations.off null, null, 'usermenu' 
				conversations.on 'newMessage', (conversation, message, isNew) => 
					if isNew and message.to.username is currentUser.get('username')
						@increaseCounter()
				, 'usermenu'
				conversations.on 'markedAsRead', (conversation, total) => 
					@increaseCounter -total
				, 'usermenu'

			increaseCounter: (val = 1) ->
				counter = @$ '.message-count' 
				count = parseInt($(counter[0]).text()) + val
				counter.html count
				if count
					@$('.message-icon').addClass 'new-messages' 
					counter.removeClass 'hidden'
				else
					@$('.message-icon').removeClass 'new-messages' 
					counter.addClass 'hidden'
				title = $('title').text()
				if title.indexOf('(') is 0
					title = title.substring title.indexOf(')') + 2
				if count
					title = "(#{count}) #{title}"
				$('title').html title

			updateNoOfTasks: (noOfTasks) ->
				@$('.task-count').removeClass 'hidden'
				@$('.task-count').html noOfTasks

			render: (renderOptions) ->
				@$el.html template 
					isAdmin: currentUser.isAdmin()
					isUserManager: currentUser.isUserManager()
					isDataManager: currentUser.isDataManager()
					isLibraryManager: currentUser.isLibraryManager()
					unreadMessages: conversations.getUnreadMessages()
					websocketSupported: (window.WebSocket isnt undefined)
					debugMode: LocalStorage.getValue('debugMode')
					reviewMode: LocalStorage.getValue('reviewMode')
					activeTasks: currentUser.get('noOfTasks')
					isPublic: !currentUser.isLoggedIn()
					settings: settings.toMap()
				if (@separateSearch)
					@$el.css 'display', 'inline'
				Renderer.render @, renderOptions
				@$('[data-toggle=tooltip]').tooltip()

)