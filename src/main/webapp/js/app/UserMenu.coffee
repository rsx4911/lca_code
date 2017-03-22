define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/Conversations'
				'cs!models/CurrentUser'
				'templates/views/user-menu'
			]

	(Backbone, Events, Layers, LocalStorage, Renderer, Router, conversations, currentUser, template) ->

		class UserMenu extends Backbone.View

			logout: (event) ->
				conversations.closeSocket()
				Events.preventDefault event
				$.ajax
					type: 'POST' 
					url: 'ws/public/logout'
					success: () -> window.location.href = 'login'
					error: () -> window.location.href = 'login'

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

			onSearchKeyUp: (event) ->
				if Events.keyCode(event) isnt 13
					return
				input = $ Events.target event, 'input'
				query = input.val()
				input.val ''
				if query
					Router.navigate "search/query=#{query}"
				else
					Router.navigate 'search'

			events: 
				'click a[href]:not([target=_blank]):not(.logout):not([data-action])': (event) -> Events.followLink event
				'click a.logout': (event) -> @logout event
				'click a.toggle-debug': (event) -> @toggleDebug event
				'click a.toggle-review': (event) -> @toggleReview event
				'click a[data-action=upgrade]': (event) -> @openUpgradeDialog event
				'keyup #global-search': (event) -> @onSearchKeyUp event

			initialize: () ->
				conversations.off null, null, 'usermenu' 
				conversations.on 'newMessage', (conversation, message, isNew) => 
					if isNew and message.to.username is currentUser.get('username')
						@increaseCounter()
				, 'usermenu'
				conversations.on 'markedAsRead', (conversation, total) => 
					@increaseCounter -total
				, 'usermenu'

			increaseCounter: (val = 1) ->
				counter = @$ '#message-count' 
				count = parseInt(counter.text()) + val
				counter.html count
				if count
					@$('#message-icon').addClass 'new-messages' 
					counter.removeClass 'hidden'
				else
					@$('#message-icon').removeClass 'new-messages' 
					counter.addClass 'hidden'
				title = $('title').text()
				if title.indexOf('(') is 0
					title = title.substring title.indexOf(')') + 2
				if count
					title = "(#{count}) #{title}"
				$('title').html title

			render: (renderOptions) ->
				if currentUser.isAdmin()
					$.ajax
						type: 'GET' 
						url: 'ws/admin/area/upgradeAvailable'
						success: (upgradeAvailable) =>
							@doRender renderOptions, upgradeAvailable
				else
					@doRender renderOptions

			doRender: (renderOptions, upgradeAvailable) ->
				@$el.html template 
					isAdmin: currentUser.isAdmin()
					upgradeAvailable: upgradeAvailable is 'true'
					unreadMessages: conversations.getUnreadMessages()
					websocketSupported: (window.WebSocket isnt undefined)
					debugMode: LocalStorage.getValue('debugMode')
					reviewMode: LocalStorage.getValue('reviewMode')
				Renderer.render @, renderOptions
				@$('[data-toggle=tooltip]').tooltip()

			openUpgradeDialog: (event) ->
				Events.preventDefault event
				loc = window.location
				schema = if loc.protocol is 'https' then 'wss' else 'ws'
				host = loc.host
				Layers.showProgressInLayer 
					title: 'Upgrading repositories' 
					url: "#{schema}://#{host}/sockets/admin/upgrade"
					message: 'Some repositories need to be upgraded to be used with the current version.'
					pageReloadOnClose: true

)