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
				'templates/views/user/profile'
			]

	(Backbone, Avatar, Events, Forms, Layers, Model, Password, Renderer, Status, Router, User, currentUser, template) ->

		class UserProfile extends Backbone.View

			className: 'profile-view multi-box-view'

			events:
				'submit #user-form': (event) -> @saveUser event
				'change #admin, #canCreateGroups': (event) -> @updateRights()
				'submit #password-form': (event) -> @savePassword event
				'click [data-action=delete-user]': (event) -> @deleteUser event
				'click [data-action=generate-password]': (event) -> @generatePassword()
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'user', @user.get('username')

			initialize: (options) ->
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
					isOwnUser: (user.id is currentUser.get('id'))
				Renderer.render @, renderOptions
				Forms.fill 'user-form', user
				@updateRights()

			saveUser: (event) ->
				Events.preventDefault event
				@user.set Forms.toJson 'user-form'
				username = @user.get 'username'
				unless username
					Forms.handleError 'user-form', {responseJSON: {field: 'username', message: 'Missing input: Username'}}
					return false
				if @adminArea and !@user.get('id')
						$.ajax
							type: 'POST'
							url: "/ws/admin/user/#{username}"
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
					url: '/ws/user/setpassword/' + @user.get('username')
					data: JSON.stringify Forms.toJson 'password-form'
					contentType: 'application/json'
					success: () => @reload()
					error: (response) -> Forms.handleError 'password-form', response
				return false

			deleteUser: (event) ->
				username = @user.get 'username'
				Layers.askDeleteQuestion "user #{username}", username, () =>
					$.ajax
						type: 'DELETE'
						url: "/ws/admin/user/#{username}"
						success: () -> Router.navigate 'administration/overview'

			reload: () ->
				if currentUser.isAdmin() and @adminArea
					Router.navigate 'administration/overview'
				else
					Status.success 'Successfully updated profile'
					Backbone.history.loadUrl()

			updateRights: () ->
				@$('#canCreateGroups').prop 'disabled', false
				@$('#canCreateRepositories').prop 'disabled', false
				if @$('#admin').is(':checked')
					@$('#canCreateGroups').prop 'checked', true
					@$('#canCreateGroups').prop 'disabled', true
				if @$('#canCreateGroups').is(':checked')
					@$('#canCreateRepositories').prop 'checked', true
					@$('#canCreateRepositories').prop 'disabled', true

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