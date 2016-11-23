define([
				'backbone'
			]

	(Backbone) ->

		class CurrentUser extends Backbone.Model

			url: '/ws/public'

			isLoggedIn: () ->
				if @get('id')
					return true
				return false

			isAdmin: () ->
				if @get('admin')
					return true
				return false

			isBlocked: (username) ->
				for user in @get('settings').blockedUsers
					if user.username is username
						return true
				return false

		return new CurrentUser()

)