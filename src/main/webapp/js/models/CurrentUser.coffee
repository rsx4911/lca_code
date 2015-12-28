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

		return new CurrentUser()

)