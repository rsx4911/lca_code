define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Password'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/User'
				'cs!models/CurrentUser'
				'qrcode'
				'templates/views/user/profile'
				'cropper'
			]

	(Backbone, Avatar, Events, Forms, Layers, Model, Password, Renderer, Status, Router, User, currentUser, QRCode, template) ->

		class UserProfile extends Backbone.View

			className: 'profile-view multi-box-view'

			events:
				'submit #user-form': 'saveUser'
				'change #admin, #settings-canCreateGroups': 'updateRights'
				'change #admin, #settings-canCreateRepositories': 'updateRights'
				'submit #password-form': 'savePassword'
				'keydown #settings-maxSize': (event) -> Events.validateNumber event
				'click [data-action=delete-user]': 'deleteUser'
				'click [data-action=generate-password]': 'generatePassword'
				'click [data-action=show-two-factor-auth]': (event) -> @toggleTwoFactorAuthentication ''
				'click [data-action=enable-two-factor-auth]': (event) -> @toggleTwoFactorAuthentication true
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'user', @user.get('username')

			initialize: (options) ->
				if options
					{@user, @adminArea} = options
				unless @user
					@user = new User currentUser.toJSON()

			render: (renderOptions) ->
				if @user.get('username') and !@isOwnUser
					Model.fetch @user, 
						success: () =>
							@doRender renderOptions
				else
					@doRender renderOptions

			doRender: (renderOptions) ->
				user = @user.toJSON()
				@$el.html template
					user: user
					adminArea: @adminArea
				Renderer.render @, renderOptions
				Forms.fill 'user-form', user
				@setMaxSize user.settings?.maxSize
				@updateRights()
				Avatar.initCropper 'user', @user.get('username')

			setMaxSize: (size) ->
				unless size
					return
				if size % 1073741824 is 0
					@$('#settings-maxSize-group #unit').val('1073741824')
					@$('#settings-maxSize').val(size / 1073741824)
				else
					@$('#settings-maxSize-group #unit').val('1048576')
					@$('#settings-maxSize').val(parseInt(size / 1048576))

			saveUser: (event) ->
				Events.preventDefault event
				@user.set Forms.toJson 'user-form'
				settings = @user.get 'settings'
				size = parseInt @$('#settings-maxSize').val()
				if isNaN(size)
					settings.maxSize = 0
				else
					unit = parseInt @$('#settings-maxSize-group #unit').val()
					settings.maxSize = size * unit
				username = @user.get 'username'
				unless username
					Forms.handleError 'user-form', {responseJSON: {field: 'username', message: 'Missing input: Username'}}
					return false
				if @adminArea and !@user.get('id')
						$.ajax
							type: 'POST'
							url: "ws/admin/user/#{username}"
							data: JSON.stringify @user.toJSON()
							contentType: 'application/json'
							success: () => @reload()
							error: (response) -> Forms.handleError 'user-form', response
				else
					Model.save @user, 
						success: () => @reload()
						error: (model, response) -> Forms.handleError 'user-form', response
				return false

			savePassword: (event) ->
				Events.preventDefault event
				$.ajax
					type: 'PUT'
					url: 'ws/user/setpassword/' + @user.get('username')
					data: JSON.stringify Forms.toJson 'password-form'
					contentType: 'application/json'
					success: () => @reload()
					error: (response) -> Forms.handleError 'password-form', response
				return false

			toggleTwoFactorAuthentication: (value) ->
				username = @user.get 'username'
				$.ajax
					type: if value is true or value is false then 'PUT' else 'GET'
					url: "ws/user/twoFactorAuth/#{username}/#{value}"
					success: (response) => 
						Backbone.history.loadUrl()
						if response.enabled
							@showTwoFactorAuthentication response
						else
							Layers.closeActive()
					error: (response) -> Status.error response.responseText

			showTwoFactorAuthentication: (response) ->
				Layers.showMessageInLayer
					title: 'Register Authenticator Device'
					body: '<p>To register your mobile device scan the QR code below in your Authenticator App:</p>
								 <div id="two-auth-link"></div>
								 <div style="text-align:center"><a class="default-link" href="#" id="show-secret">Show secret key</a></div>
								 <div id="two-auth-key" class="well well-sm" style="display:none; text-align:center; margin-top:20px">' + response.key + '</div>'
					buttons: [
						{text: 'Disable', className: 'btn-warning', callback: () => @toggleTwoFactorAuthentication(false)},
						{text: 'Close', className: 'btn-default', callback: Layers.closeActive}
					]
				new QRCode($('#two-auth-link')[0], response.url)
				$('#two-auth-link').removeAttr 'title'
				$('#two-auth-link img').addClass 'center-block'
				$('#show-secret').on 'click', (event) -> 
					Events.preventDefault event
					$('#two-auth-key').show()
					$('#show-secret').remove()

			deleteUser: (event) ->
				username = @user.get 'username'
				isOwnUser = @user.get('id') is currentUser.get('id')
				text = if isOwnUser then 'your own user' else "user #{username}"
				url = if isOwnUser then 'ws/user' else "ws/admin/user/#{username}"
				Layers.askDeleteQuestion text, username, () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: url
						success: () -> 
							Layers.hideProgressIndicator()
							if currentUser.isAdmin() and !isOwnUser
								Router.navigate 'administration/overview'
							else
								window.location.href = 'login'

			reload: () ->
				isOwnUser = @user.get('id') is currentUser.get('id')
				if currentUser.isAdmin() and isOwnUser and !@user.get('admin')
					window.location.href = 'administration/overview'
				else if currentUser.isAdmin() and @adminArea
					Router.navigate 'administration/overview'
				else
					Status.success 'Successfully updated profile'
					Backbone.history.loadUrl()

			updateRights: () ->
				@$('#settings-canCreateGroups').prop 'disabled', false
				@$('#settings-canCreateRepositories').prop 'disabled', false
				if @$('#admin').is(':checked')
					@$('#settings-canCreateGroups').prop 'checked', true
					@$('#settings-canCreateGroups').prop 'disabled', true
				if @$('#settings-canCreateGroups').is(':checked')
					@$('#settings-canCreateRepositories').prop 'checked', true
					@$('#settings-canCreateRepositories').prop 'disabled', true
				if !@$('#settings-canCreateRepositories').is(':checked')
					@$('#settings-noOfRepositories-group').hide()
				else
					@$('#settings-noOfRepositories-group').show()

			generatePassword: () ->
				Layers.showMessageInLayer
					title: 'Strong password generator'
					body: 'The following password is generated client-side and was not sent across the internet<br><br><div id="generated-password"><strong><center>' + Password.generate() + '</center></strong></div>'
					buttons: [
						{text: 'Regenerate', callback: () => @generatePassword()}
						{text: 'Use password', callback: () => @usePassword()}
					]
				@markPassword()

			markPassword: () ->
				elem = $('#generated-password')[0]
				if document.body.createTextRange 
					range = document.body.createTextRange()
					range.moveToElementText elem
					range.select()
				else if window.getSelection 
					selection = window.getSelection()
					range = document.createRange()
					range.selectNodeContents elem
					selection.removeAllRanges()
					selection.addRange range

			usePassword: () ->
				pass = $('#generated-password').text()
				Layers.closeActive()
				$('#password, #password2').val pass

)