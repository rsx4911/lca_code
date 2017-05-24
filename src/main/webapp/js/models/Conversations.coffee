define([
				'backbone'
				'cs!models/Conversation'
				'cs!models/CurrentUser'
			]

	(Backbone, Conversation, currentUser) ->

		class Conversations extends Backbone.Collection

			url: 'ws/messaging'

			model: Conversation

			comparator: (c1, c2) ->
				d1 = c1.findNewestMessage()?.date or Number.MAX_VALUE
				d2 = c2.findNewestMessage()?.date or Number.MAX_VALUE
				return d1 < d2

			initSocket: () ->
				unless window.WebSocket and currentUser.isLoggedIn()
					return
				loc = window.location
				schema = if loc.protocol is 'https' or loc.protocol is 'https:' then 'wss' else 'ws'
				base = $('base').attr('href') or '/'
				@socket = new WebSocket "#{schema}://#{loc.host}#{base}sockets/messaging"
				@socket.onmessage = (msg) =>
					data = JSON.parse msg.data
					if data.type is 'NEW_MESSAGE'
						@handleNewMessage data.data
					else if data.type is 'CONNECTED'
						@handleConnected data.data
					else if data.type is 'DISCONNECTED'
						@handleDisconnected data.data
					else if data.type is 'MESSAGE_READ'
						@handleMessageRead data.data
					else if data.type is 'IS_ONLINE'
						@handleIsOnline data.data

			handleNewMessage: (message) ->
				type = if message.team then 'team' else 'user'
				id = if type is 'team' then message.team.teamname else if message.from.username is currentUser.get('username') then message.to.username else message.from.username
				conversation = @getFor type, id
				unless conversation
					recipient = {type: type, id: id, username: id, name: (if message.type is 'team' then message.team.name else message.to.name)}
					conversation = new Conversation {messages: [], unreadMessages: 0, recipient: recipient, online: true}
					@add conversation
				conversation.get('messages').push message
				if message.to.username is currentUser.get('username')
					conversation.set 'unreadMessages', parseInt(conversation.get('unreadMessages')) + 1
				@trigger 'newMessage', conversation, message, true

			handleConnected: (username) ->
				conversation = @getFor 'user', username
				if conversation
					conversation.set 'online', true
					@trigger 'connected', conversation 

			handleDisconnected: (username) ->
				conversation = @getFor 'user', username
				if conversation
					conversation.set 'online', false
					@trigger 'disconnected', conversation

			handleMessageRead: (username) ->
				conversation = @getFor 'user', username
				if conversation
					for message in conversation.get('messages')
						if message.read
							continue
						# exact time is set on server, this is only for display purposes until page is reloaded
						message.read = new Date().getTime() 
					@trigger 'messageRead', conversation

			handleIsOnline: (username) ->
				conversation = @getFor 'user', username
				if conversation
					conversation.set 'online', true
					@trigger 'connected', conversation 

			closeSocket: (callback) ->
				unless window.WebSocket
					return
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

			markAsRead: (conversation) ->
				conversation.markAsRead()
				@socket.send JSON.stringify {type: 'MESSAGE_READ', data: JSON.stringify(conversation.get('recipient'))}

			pingUser: (conversation) ->
				@socket.send JSON.stringify {type: 'IS_ONLINE', data: JSON.stringify(conversation.get('recipient'))}

		return new Conversations()

)