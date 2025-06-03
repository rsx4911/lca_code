define([
				'backbone'
				'qrcode'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Password'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/User'
				'cs!models/CurrentUser'
				'templates/views/user/profile'
				'cropper'
			]

	(Backbone, QRCode, Avatar, Events, Format, Forms, Layers, Model, Password, Renderer, Status, Router, User, currentUser, template) ->

		class UserProfile extends Backbone.View

			className: 'profile-view multi-box-view'

			events:
				'submit #user-form': 'saveUser'
				'change #admin, #settings-admin': 'updateRights'
				'change #admin, #settings-canCreateGroups': 'updateRights'
				'change #admin, #settings-canCreateRepositories': 'updateRights'
				'submit #password-form': 'savePassword'
				'keydown #settings-maxSize': (event) -> Events.validateNumber event
				'click [data-action=delete-user]': 'deleteUser'
				'click [data-action=toggle-active]': 'toggleActive'
				'click [data-action=generate-password]': 'generatePassword'
				'click [data-action=show-two-factor-auth]': (event) -> @toggleTwoFactorAuthentication ''
				'click [data-action=enable-two-factor-auth]': (event) -> @toggleTwoFactorAuthentication 'true'
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
				if user.settings?.activeUntil
					user.settings.activeUntil = Format.moment user.settings.activeUntil, 'YYYY-MM-DD'
				@$el.html template
					user: user
					isAdmin: currentUser.isAdmin()
					isUserManager: currentUser.isUserManager()
					isDataManager: currentUser.isDataManager()
					isLibraryManager: currentUser.isLibraryManager()
					adminArea: @adminArea
					formatDate: Format.date
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
				activeUntil = @$('#settings-activeUntil').val()
				if @adminArea and !@user.get('id')
						$.ajax
							type: 'POST'
							url: "ws/usermanager/user/#{username}"
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
				url = "ws/user/twoFactorAuth/#{username}"
				if value
					url += "/#{value}"
				$.ajax
					type: if value is 'true' or value is 'false' then 'PUT' else 'GET'
					url: url
					success: (response) => 
						if response.enabled
							@showTwoFactorAuthentication response
						else
							Layers.closeActive()
						@user.set 'twoFactorAuth', response.enabled
						currentUser.set 'twoFactorAuth', response.enabled
						Backbone.history.loadUrl()
					error: (response) -> Status.error response.responseText

			showTwoFactorAuthentication: (response) ->
				Layers.showMessageInLayer
					title: 'Register Authenticator Device'
					body: '<p>To register your mobile device scan the QR code below in your Authenticator App:</p>
								 <canvas id="two-auth-link" class="center-block"></canvas>
								 <div style="text-align:center"><a class="default-link" href="#" id="show-secret">Show secret key</a></div>
								 <div id="two-auth-key" class="well well-sm" style="display:none; text-align:center; margin-top:20px">' + response.key + '</div>'
					buttons: [
						{text: 'Disable', className: 'btn-warning', callback: () => @toggleTwoFactorAuthentication('false')},
						{text: 'Close', className: 'btn-default', callback: Layers.closeActive}
					]
				QRCode.toCanvas($('#two-auth-link')[0], response.url)
				$('#show-secret').on 'click', (event) -> 
					Events.preventDefault event
					$('#two-auth-key').show()
					$('#show-secret').remove()

			deleteUser: (event) ->
				username = @user.get 'username'
				isOwnUser = @user.get('id') is currentUser.get('id')
				text = if isOwnUser then 'your own user' else "user #{username}"
				url = if isOwnUser then 'ws/user' else "ws/usermanager/user/#{username}"
				Layers.askDeleteQuestion text, username, () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: url
						success: () -> 
							Layers.hideProgressIndicator()
							if currentUser.isUserManager() and !isOwnUser
								Router.navigate 'administration/overview'
							else
								window.location.href = 'login'

			toggleActive: (event) ->
				if @user.get('deactivated') is true
					@$('#settings-activeUntil').val('')
				else
					@$('#settings-activeUntil').val Format.moment new Date().setDate(new Date().getDate() - 1) , 'YYYY-MM-DD'
				@saveUser event

			reload: () ->
				isOwnUser = @user.get('id') is currentUser.get('id')
				if currentUser.isUserManager() and isOwnUser
					window.location.href = 'administration/overview'
				else if currentUser.isUserManager() and @adminArea
					Router.navigate 'administration/overview'
				else
					Status.success 'Successfully updated profile'
					Backbone.history.loadUrl()

			updateRights: () ->
				@$('#settings-canCreateGroups').prop 'disabled', false
				@$('#settings-canCreateRepositories').prop 'disabled', false
				@$('#settings-userManager').prop 'disabled', false
				@$('#settings-dataManager').prop 'disabled', false
				@$('#settings-libraryManager').prop 'disabled', false
				if @$('#settings-admin').is(':checked')
					@$('#settings-canCreateGroups').prop 'checked', true
					@$('#settings-canCreateGroups').prop 'disabled', true
					@$('#settings-userManager').prop 'checked', true
					@$('#settings-userManager').prop 'disabled', true
					@$('#settings-dataManager').prop 'checked', true
					@$('#settings-libraryManager').prop 'disabled', true
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