define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/login-layer'
			]

	(Backbone, Events, Renderer, currentUser, template) ->

		class LoginLayer extends Backbone.View

			events: 
				'submit #login': 'login'

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions

			login: () ->
				Events.preventDefault event
				currentUser.set 'inLoginProcess', true
				data = 
					username: @$('#username').val()
					password: @$('#password').val()
				if @$('#token').is(':visible')
					data.token = @$('#token').val()
				$.ajax
					type: 'POST'
					url: 'ws/public/login'
					contentType: 'application/json'
					data: JSON.stringify(data) 
					success: (response) =>
						if response is 'tokenRequired'
							@$('#username-group').addClass 'hidden'
							@$('#password-group').addClass 'hidden'
							@$('#token-group').removeClass 'hidden'
							@$('#login-general-message').hide()
							@$('#token').focus()
						else
							window.location.href = ''
							currentUser.unset 'inLoginProcess'
					error: (response) =>
						@$('#login-general-message').show()
						@$('#login-general-message .message').html response.responseText
						@$('#token').val ''
				return false

)