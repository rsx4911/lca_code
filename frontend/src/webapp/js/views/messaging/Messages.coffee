define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!models/Conversation'
				'cs!models/Conversations'
				'cs!models/CurrentUser'
				'templates/views/messaging/messages'
				'templates/views/messaging/conversation'
				'templates/views/messaging/message'
			]

	(Backbone, Events, Format, Layers, Renderer, Status, Conversation, conversations, currentUser, template, conversationTemplate, messageTemplate) ->

		class MessagesView extends Backbone.View

			className: 'messages-view'

			events:
				'click a[data-action=show-message]': 'onConversationClicked'
				'click [data-action=start-new-conversation]': 'openSelection'
				'click .block': 'blockUser'
				'click .unblock': 'unblockUser'
				'keypress #conversation-input input': (event) -> @sendMessage event, true
				'click #conversation-input .glyphicon-send': (event) -> @sendMessage event, false

			render: (renderOptions) ->
				@$el.html template
					selected: @conversation?.get('recipient')
					isBlocked: @isBlocked(@conversation)
				Renderer.render @, renderOptions
				for conversation in conversations.models
					element = @addConversation conversation, element
					conversations.pingUser conversation
				@initResizeListener()
				@initConversationListener()
				unless @hasScrollBar()
					@conversation?.loadPrevious()


			renderMessage: (message, prepend) ->
				messages = @$ '#conversation-messages'
				wasAtBottom = @isAtBottom()
				content = messageTemplate
					message: message
					username: currentUser.get('username')
					formatDateOrTime: Format.dateOrTime
					formatTimeOrDate: Format.timeOrDate
				if prepend
					messages.prepend content
					messages.scrollTop messages.scrollTop() + $("##{message.id}").outerHeight(true)
				else 
					messages.append content
					if message.from.username is currentUser.get('username') or wasAtBottom
						@scrollDown()
					if wasAtBottom and messages.is(':visible') and window.isActive
						conversations.markAsRead @conversation

			isAtBottom: () ->
				container = @$('#conversation-messages')
				if container[0].scrollHeight <= container[0].clientHeight
					return true
				if container.scrollTop() is container.prop('scrollHeight') - container.innerHeight()
					return true
				if container.prop('scrollHeight') <= container.outerHeight()
					return true
				return false

			hasScrollBar: () ->
				container = @$('#conversation-messages')
				if container[0].scrollHeight <= container[0].clientHeight
					return true
				return false

			initResizeListener: () ->
				# remove previous listeners
				$(window).off 'resize.messages'
				$(window).off 'focus.messages'
				$(window).on 'focus.messages', () =>
					if @$('#conversation-messages').is(':visible') and @isAtBottom() and @conversation
						conversations.markAsRead @conversation
				$(window).on 'resize.messages', (event) =>
					if $('#conversation, #conversations').length is 0
						$(window).off 'resize.messages'
						return
					@updateHeight()
				@updateHeight()
				@$('#conversation-messages').off 'scroll.messages'
				@$('#conversation-messages').on 'scroll.messages', (event) =>
					container = @$ '#conversation-messages'
					if @isAtBottom() and @conversation and container.is(':visible') and window.isActive
						conversations.markAsRead @conversation
					if container.scrollTop()
						return
					@conversation?.loadPrevious()

			initConversationListener: () ->
				conversations.off null, null, 'messages'
				conversations.on 'add', (conversation) => 
					@addConversation conversation
					@activateConversation conversation
				, 'messages'
				conversations.on 'newMessage', (conversation, message, isNew) => 
					recipient = conversation.get 'recipient' 
					recipient2 = @conversation?.get 'recipient'
					if isNew
						@rerenderConversation conversation
					if recipient2 and recipient2.type is recipient.type and recipient2.id is recipient.id
						@renderMessage message, !isNew
				, 'messages'
				conversations.on 'connected', (conversation) => 
					id = conversation.get('recipient').id
					@$("#conversations [data-type=user][data-id=#{id}] .online-indicator").show()
				conversations.on 'disconnected', (conversation) => 
					id = conversation.get('recipient').id
					@$("#conversations [data-type=user][data-id=#{id}] .online-indicator").hide()
				conversations.on 'markedAsRead', (conversation) => 
					@rerenderConversation conversation, true
				, 'messages'
				conversations.on 'messageRead', (conversation) => 
					@rerenderConversation conversation, true
					@$('#conversation-messages .read-indicator').addClass 'is-read'
				, 'messages'

			rerenderConversation: (conversation, keepPosition) ->
				recipient = conversation.get 'recipient'
				element = @$ "#conversations [data-type=#{recipient.type}][data-id=#{recipient.id}]"
				prev = element.prev()
				element.remove()
				@addConversation conversation, (if keepPosition then prev else null)
				@$("#conversations [data-id]").hide()
				setTimeout () -> # TODO fix this, had to add little timeout, otherwise badge will not be repositioned
					@$("#conversations [data-id]").show()
				, 2

			updateHeight: () ->
				height = $(window).height() - 220
				$('#conversation-messages').css 'height', height - 41
				$('#conversations').css 'height', height

			isBlocked: (conversation) ->
				if !conversation or !conversation.get('recipient') or conversation.get('recipient').type is 'team'
					return false
				return currentUser.isBlocked(conversation.get('recipient').id) or conversation.get('blocked')

			addConversation: (conversation, afterElement) ->
				message = conversation.findNewestMessage()
				recipient = conversation.get 'recipient'
				recipient2 = @conversation?.get 'recipient'
				newDate = message?.date or Number.MAX_VALUE
				content = conversationTemplate
					recipient: recipient
					message: message
					formatTimeOrDate: Format.timeOrDate
					unreadMessages: conversation.get('unreadMessages')
					selected: recipient2 and recipient2.type is recipient.type and recipient2.id is recipient.id
					isBlocked: @isBlocked(conversation) 
				container = @$('#conversations .list-container')
				if afterElement and afterElement.attr 'data-type'
					afterElement.after content
				else
					container.prepend content
				return @$("#conversations [data-type=#{recipient.type}][data-id=#{recipient.id}]")

			activateConversation: (conversation) ->
				@conversation = conversation
				recipient = conversation.get 'recipient'
				sendButton = @$ '#conversation-input .glyphicon-send'
				if currentUser.isBlocked recipient.id
					@$('.block').hide()
					@$('.unblock').show()
				else
					@$('.block').show()
					@$('.unblock').hide()
				if @isBlocked(conversation)
					sendButton.hide()
					@$('#next-message').attr 'placeholder', ''
				else
					sendButton.show()
					@$('#next-message').attr 'placeholder', 'Type your message here...'
				@$('.list-entry.active').removeClass 'active'
				@$("[data-type=#{recipient.type}][data-id=#{recipient.id}] .list-entry").addClass 'active'
				@$('#next-message').prop 'disabled', !!@isBlocked(conversation)
				@$('#next-message').val ''
				@$('.header-box .username').html recipient.name
				@$('.header-box .avatar').attr 'src', "ws/#{recipient.type}/avatar/#{recipient.id}"
				@$('#conversation-messages').empty()
				for message in conversation.get('messages')
					@renderMessage message
				if conversation.get('messages').length is 1
					conversation.loadPrevious()
				conversations.markAsRead conversation

			scrollDown: () ->
				@$('#conversation-messages').scrollTop @$('#conversation-messages').prop 'scrollHeight'

			sendMessage: (event, fromInput) ->
				unless @conversation
					return
				if fromInput
					keyCode = Events.keyCode event
					if keyCode isnt 13
						return
				input = @$ '#next-message'
				text = input.val()
				unless text
					return
				input.val ''				
				conversations.sendMessage @conversation.get('recipient'), text

			onConversationClicked: (event) ->
				Events.preventDefault event
				target = $ Events.target event, 'a'
				type = target.attr 'data-type'
				id = target.attr 'data-id'
				@activateConversation conversations.getFor type, id
				@scrollDown()

			openSelection: () ->				
				Layers.selectUser
					title: 'Start new conversation'
					module: 'messaging'
					excludeSelf: true
					callback: (selection) => 
						conversation = conversations.getFor selection.type, selection.id
						if conversation
							@activateConversation conversation
						else
							recipient = {type: selection.type, id: selection.id, username: selection.id, name: selection.displayName}
							conversation = new Conversation {messages: [], unreadMessages: 0, recipient: recipient}
							conversations.add conversation
							conversations.pingUser conversation

			blockUser: () ->
				unless @conversation
					return
				recipient = @conversation.get('recipient')
				if currentUser.isBlocked recipient.username
					return
				$.ajax
					type: 'PUT'
					url: "ws/messaging/block/#{recipient.username}"
					success: () =>
						currentUser.get('settings').blockedUsers.push {name: recipient.name, username: recipient.username}
						@$('.block').hide()
						@$('.unblock').show()
						@rerenderConversation @conversation, true
						Status.success 'Successfully blocked user'

			unblockUser: () ->
				unless @conversation
					return
				recipient = @conversation.get('recipient')
				unless currentUser.isBlocked recipient.username
					return
				$.ajax
					type: 'PUT'
					url: "ws/messaging/unblock/#{recipient.username}"
					success: () =>
						currentUser.get('settings').blockedUsers = currentUser.get('settings').blockedUsers.filter (u) ->
							u.username isnt recipient.username
						@$('.unblock').hide()
						@$('.block').show()
						@rerenderConversation @conversation, true
						Status.success 'Successfully unblocked user'

)