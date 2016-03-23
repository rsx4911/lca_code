define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/User'
				'cs!models/CurrentUser'
				'templates/views/user/profile'
			]

	(Backbone, Avatar, Events, Forms, Layers, Model, Renderer, Status, Router, User, currentUser, template) ->

		class UserProfile extends Backbone.View

			className: 'profile-view multi-box-view'

			events:
				'submit #user-form': (event) -> @saveUser event
				'submit #password-form': (event) -> @savePassword event
				'click [data-action=delete-user]': (event) -> @deleteUser event
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
				Renderer.render @, renderOptions
				Forms.fill 'user-form', user

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
					dataType: 'text'
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

)