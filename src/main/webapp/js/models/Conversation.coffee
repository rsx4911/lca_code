define([
				'backbone'
				'cs!models/CurrentUser'
			]

	(Backbone, currentUser) ->

		class Conversation extends Backbone.Model
			
			initialize: () ->
				unless @get('messages')
					@set 'messages', []

			loadPrevious: () ->
				if @loading or @allLoaded
					return
				@loading = true
				recipient = @get 'recipient'
				subpath = "#{recipient.type}/#{recipient.id}"
				oldestMessage = @findOldestMessage()
				query = if oldestMessage then "?before=#{oldestMessage.date}" else ''
				$.ajax
					type: 'GET'
					url: "ws/messaging/#{subpath}#{query}"
					success: (messages) =>
						@loading = false
						if !messages or !messages.length
							@allLoaded = true
							return 
						messages.sort (m1, m2) -> return m2.date - m1.date
						for message in messages
							@get('messages').unshift message
							@trigger 'newMessage', @, message, false

			findOldestMessage: () ->
				oldest = null
				for message in @get('messages')
					if !oldest or oldest.date > message.date
						oldest = message
				return oldest

			findNewestMessage: () ->
				newest = null
				for message in @get('messages')
					if !newest or newest .date < message.date
						newest = message
				return newest

			markAsRead: () ->
				if parseInt(@get('unreadMessages')) is 0
					return false
				recipient = @get 'recipient'
				subpath = "#{recipient.type}/#{recipient.id}"
				for message in @get('messages')
					if message.to.username is currentUser.get('username')
						message.unread = false
				total = @get 'unreadMessages'
				@set 'unreadMessages', 0
				@trigger 'markedAsRead', @, total
				return true

)