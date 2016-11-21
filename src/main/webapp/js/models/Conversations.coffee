define([
				'backbone'
				'cs!models/Conversation'
				'cs!models/CurrentUser'
			]

	(Backbone, Conversation, currentUser) ->

		class Conversations extends Backbone.Collection

			url: '/ws/messages'

			model: Conversation

			comparator: (c1, c2) ->
				d1 = c1.findNewestMessage()?.date or Number.MAX_VALUE
				d2 = c2.findNewestMessage()?.date or Number.MAX_VALUE
				return d1 < d2

			initSocket: () ->
				loc = window.location
				schema = if loc.protocol is 'https' then 'wss' else 'ws'
				@socket = new WebSocket "#{schema}://#{loc.host}/sockets/messages"
				@socket.onmessage = (msg) =>
					data = JSON.parse msg.data
					if data.type is 'NEW_MESSAGE'
						message = data.data
						type = if message.team then 'team' else 'user'
						id = if type is 'team' then message.team.teamname else if message.from.username is currentUser.get('username') then message.to.username else message.from.username
						conversation = @getFor type, id
						conversation.get('messages').push message
						if message.to.username is currentUser.get('username')
							conversation.set 'unreadMessages', parseInt(conversation.get('unreadMessages')) + 1
						@trigger 'newMessage', conversation, message, true

			closeSocket: (callback) ->
				@socket.onclose = () ->
					callback?()
				@socket.close()

			sendMessage: (to, text) ->
				@socket.send JSON.stringify {type: 'NEW_MESSAGE', data: JSON.stringify(to: to, text: text)}

			getFor: (type, id) ->
				for conversation in @models
					if conversation.get('recipient').type is type
						if conversation.get('recipient').id is id
							return conversation
				return null

			getUnreadMessages: () ->
				total = 0
				for conversation in @models
					total += conversation.get('unreadMessages')
				return total

		return new Conversations()

)