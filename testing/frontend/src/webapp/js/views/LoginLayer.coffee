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
				'keydown #login input': 'submit' 

			render: (renderOptions) ->
				@$el.html template()
				@$('h3').remove()
				Renderer.render @, renderOptions
				$.ajax
					type: 'GET'
					url: 'ws/public/auth-providers'
					success: (providers) ->
						if !providers?.length
							return
						$('.auth-provider-container').append '<div class="separator">or</div>'
						providers.forEach (provider) ->
							$('.auth-provider-container').append('<a class="btn btn-block btn-default" href="oauth2/authorization/' + provider.id + '">Continue with ' + provider.name + '</a>')

			submit: (event) ->
				target = $ Events.target event
				key = Events.keyCode event
				if key is 13
					@login()

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
							currentUser.unset 'inLoginProcess'
							window.location.reload()
					error: (response) =>
						@$('#login-general-message').show()
						@$('#login-general-message .message').html response.responseText
						@$('#token').val ''
				return false

)