define([
				'backbone'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!models/Conversation'
				'cs!models/Conversations'
				'cs!models/CurrentUser'
				'templates/views/messages/messages'
				'templates/views/messages/conversation'
				'templates/views/messages/message'
			]

	(Backbone, Data, Events, Format, Layers, Renderer, Conversation, conversations, currentUser, template, conversationTemplate, messageTemplate) ->

		class MessagesView extends Backbone.View

			className: 'messages-view'

			events:
				'click a[data-action=show-message]': (event) -> @onConversationClicked event
				'click [data-action=start-new-conversation]': (event) -> @openSelection event
				'keypress #conversation-input input': (event) -> @sendMessage event

			render: (renderOptions) ->
				@$el.html template
					selected: @conversation?.get('recipient')
				Renderer.render @, renderOptions
				for conversation in conversations.models
					element = @addConversation conversation, element
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
					formatDate: Format.dayOrTime
				if prepend
					messages.prepend content
					messages.scrollTop messages.scrollTop() + $("##{message.id}").outerHeight(true)
				else 
					messages.append content
					if message.from.username is currentUser.get('username') or wasAtBottom
						@scrollDown()
					if wasAtBottom
						@conversation.markAsRead()

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
				$(window).on 'resize.messages', (event) =>
					if $('#conversation, #conversations').length is 0
						$(window).off 'resize.messages'
						return
					@updateHeight()
				@updateHeight()
				@$('#conversation-messages').off 'scroll.messages'
				@$('#conversation-messages').on 'scroll.messages', (event) =>
					container = @$('#conversation-messages')
					if @isAtBottom()
						@conversation?.markAsRead()
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
				conversations.on 'markedAsRead', (conversation) => 
					@rerenderConversation conversation, true
				, 'messages'

			rerenderConversation: (conversation, keepPosition) ->
				recipient = conversation.get 'recipient'
				element = @$ "#conversations [data-type=#{recipient.type}][data-id=#{recipient.id}]"
				prev = element.prev()
				element.remove()
				@addConversation conversation, (if keepPosition then prev else null)
				@$("#conversations [data-id]").hide()
				setTimeout () -> # TODO fix this, have to add little timeout, otherwise badge will not be repositioned
					@$("#conversations [data-id]").show()
				, 2

			updateHeight: () ->
				height = $(window).height() - 220
				$('#conversation-messages').css 'height', height - 41
				$('#conversations').css 'height', height

			addConversation: (conversation, afterElement) ->
				message = conversation.findNewestMessage()
				recipient = conversation.get 'recipient'
				recipient2 = @conversation?.get 'recipient'
				newDate = message?.date or Number.MAX_VALUE
				content = conversationTemplate
					recipient: recipient
					message: message
					formatDate: Format.dayOrTime
					unreadMessages: conversation.get('unreadMessages')
					selected: recipient2 and recipient2.type is recipient.type and recipient2.id is recipient.id
				container = @$('#conversations .list-container')
				if afterElement and afterElement.attr 'data-type'
					afterElement.after content
				else
					container.prepend content
				return @$("#conversations [data-type=#{recipient.type}][data-id=#{recipient.id}]")

			activateConversation: (conversation) ->
				@conversation = conversation
				recipient = conversation.get 'recipient'
				@$('.list-entry.active').removeClass 'active'
				@$("[data-type=#{recipient.type}][data-type=#{recipient.id}] .list-entry").addClass 'active'
				@$('#next-message').prop 'disabled', false
				@$('.header-box .username').html recipient.name
				@$('.header-box .avatar').attr 'src', "/ws/#{recipient.type}/avatar/#{recipient.id}"
				@$('#conversation-messages').empty()
				for message in conversation.get('messages')
					@renderMessage message
				if conversation.get('messages').length is 1
					conversation.loadPrevious()

			scrollDown: () ->
				@$('#conversation-messages').scrollTop @$('#conversation-messages').prop 'scrollHeight'

			sendMessage: (event) ->
				unless @conversation
					return
				keyCode = Events.keyCode event
				if keyCode isnt 13
					return
				input = @$ '#next-message'
				text = input.val()
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
				existingUsers = []
				existingTeams = []
				existingUsers.push {username: currentUser.get('username')}
				for conversation in conversations.models
					if conversation.get('recipient').type is 'user'
						existingUsers.push {username: conversation.get('recipient').id}
					else if conversation.get('recipient').type is 'team'
						existingTeams.push {teamname: conversation.get('recipient').id}
				Data.getUsersAndTeams (users, teams) =>
					users = Data.usersToOptions users, existingUsers, true 
					teams = Data.teamsToOptions teams, existingTeams, true 
					Layers.showTemplateInLayer
						template: 'messages/select-user'
						title: "Start new conversation"
						model: {users: users, teams: teams}
						buttons: [{id: 'select-user', className: 'btn-primary', text: 'Select', callback: () => @onSelection()}]

			onSelection: () ->
				selection = $ '#name option:selected'
				type = selection.attr 'data-group-id'
				recipient = {type: type, id: selection.val(), username: selection.val(), name: selection.text()}
				conversations.add new Conversation {messages: [], unreadMessages: 0, recipient: recipient}
				Layers.closeActive()

)