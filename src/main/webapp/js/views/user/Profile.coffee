define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/User'
				'cs!models/CurrentUser'
				'templates/views/user/profile'
			]

	(Backbone, Events, Forms, Model, Renderer, Status, Router, User, currentUser, template) ->

		class UserProfile extends Backbone.View

			doRender: (renderOptions) ->
				user = @user.toJSON()
				@$el.html template
					user: user
					adminArea: @adminArea
				Renderer.render @, renderOptions
				Forms.fill 'user', user

			saveUser: () ->
				@user.set Forms.toJson 'user'
				unless @user.get('username')
					Forms.handleError 'user', {responseJSON: {field: 'username', message: 'Missing input: Username'}}
					return false
				Model.save @user, 
					success: @reload
					error: (model, response) -> Forms.handleError 'user', response
				return false

			savePassword: () ->
				$.ajax
					type: 'PUT'
					url: '/ws/admin/user/' + @user.get('username') + '/setpassword'
					data: JSON.stringify Forms.toJson 'password'
					contentType: 'application/json'
					success: @reload
					error: (response) -> Forms.handleError 'password', response
				return false

			deleteUser: (event) ->
				@user.destroy 
					success: () -> Router.navigate 'admin/overview'

			reload: () ->
				if currentUser.isAdmin() and @adminArea
					Router.navigate 'admin/overview'
				else
					Status.success 'Successfully updated profile'
					Backbone.history.loadUrl()

			events:
				'submit #user': @saveUser
				'submit #password': @savePassword
				'click [data-action=delete-user]': @deleteUser

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

)