define([
				'backbone'
				'sockjs'
				'stomp'
				'cs!models/Conversation'
				'cs!models/CurrentUser'
			]

	(Backbone, SockJS, Stomp, Conversation, currentUser) ->

		class Conversations extends Backbone.Collection

			url: 'ws/messaging'

			model: Conversation

			comparator: (c1, c2) ->
				d1 = c1.findNewestMessage()?.date or Number.MAX_VALUE
				d2 = c2.findNewestMessage()?.date or Number.MAX_VALUE
				return d1 < d2

			initSocket: (callback) ->
				sock = new SockJS '/stomp'
				@client = Stomp.over sock
				@client.debug = () =>
				@client.connect {}, (frame) =>
					session = @getSession()
					@client.subscribe "/user/#{session}/new-message", (payload) =>
						@handleNewMessage JSON.parse payload.body
					@client.subscribe "/user/#{session}/connected", (payload) =>
						@setOnline payload.body, true
					@client.subscribe "/user/#{session}/disconnected", (payload) =>
						@setOnline payload.body, false
					@client.subscribe "/user/#{session}/mark-as-read", (payload) =>
						@handleMessageRead payload.body
					window.onbeforeunload = () => @closeSocket()
					callback?()

			getSession: () -> 
				url = @client.ws._transport.url;
				url = url.substring 0, url.lastIndexOf '/'
				return url.substring(url.lastIndexOf('/') + 1)


			handleNewMessage: (message) ->
				type = if message.team then 'team' else 'user'
				id = if type is 'team' then message.team.teamname else if message.from.username is currentUser.get('username') then message.to.username else message.from.username
				conversation = @getFor type, id
				unless conversation
					recipient = {type: type, id: id, username: id, name: (if type is 'team' then message.team.name else message.to.name)}
					conversation = new Conversation {messages: [], unreadMessages: 0, recipient: recipient, online: true}
					@add conversation
				conversation.get('messages').push message
				if message.to.username is currentUser.get('username')
					conversation.set 'unreadMessages', parseInt(conversation.get('unreadMessages')) + 1
				@trigger 'newMessage', conversation, message, true

			setOnline: (username, value) ->
				conversation = @getFor 'user', username
				if conversation
					conversation.set 'online', value
					if value
						@trigger 'connected', conversation 
					else
						@trigger 'disconnected', conversation 

			handleMessageRead: (username) ->
				conversation = @getFor 'user', username
				if conversation
					for message in conversation.get('messages')
						if message.readDate
							continue
						# exact time is set on server, this is only for display purposes until page is reloaded
						message.readDate = new Date().getTime() 
					@trigger 'messageRead', conversation

			closeSocket: (callback) ->
				unless @client
					return
				@client.onDisconnect = () ->
					callback?()
				@client.disconnect()

			sendMessage: (to, text) ->
				@client.send '/sockets/new-message', {}, JSON.stringify ({ to:  to, text: text })

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
				if conversation.markAsRead()
					@client.send '/sockets/mark-as-read', {}, JSON.stringify conversation.get('recipient')

			pingUser: (conversation) ->
				@client.send '/sockets/is-online', {}, JSON.stringify conversation.get('recipient')

		return new Conversations()

)