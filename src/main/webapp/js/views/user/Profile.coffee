define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/User'
				'cs!models/CurrentUser'
				'templates/views/user/profile'
			]

	(Backbone, Events, Forms, Model, Renderer, Router, User, currentUser, template) ->

		class UserProfile extends Backbone.View

			doRender = (renderOptions) ->
				user = @user.toJSON()
				@$el.html template
					user: user
					isOwnUser: @isOwnUser
				Renderer.render @, renderOptions
				Forms.fill '#user', user

			saveUser = () ->
				@user.set Forms.toJson '#user'
				Model.save @user, 
					success: () => (@_ reload)()

			savePassword = () ->
				$.ajax
					type: 'PUT'
					url: '/ws/admin/user/' + @user.get('username') + '/setpassword'
					data: JSON.stringify Forms.toJson '#password'
					contentType: 'application/json'
					success: () => (@_ reload)()

			deleteUser = (event) ->
				username = $(Events.target event).attr 'data-username'
				new User({id: -1, username: username}).destroy 
					success: () -> Router.navigate 'admin/overview'

			reload = () ->
				if currentUser.isAdmin() and !@isOwnUser
					Router.navigate 'admin/overview'
				else
					Backbone.history.loadUrl()

			isOwnUser = () ->
				return @user.id is currentUser.id 

			events:
				'click [data-action=save-user]': saveUser
				'click [data-action=save-password]': savePassword
				'click [data-action=delete-user]': deleteUser

			initialize: (options) ->
				{@user} = options
				unless @user
					@user = currentUser
					@isOwnUser = true

			render: (renderOptions) ->
				if @user.get('username') and !@isOwnUser
					Model.fetch @user, 
						success: () =>
							(@_ doRender) renderOptions
				else
					(@_ doRender) renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)