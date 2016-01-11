define([
				'backbone'
			]

	(Backbone) ->

		class User extends Backbone.Model

			url: () ->
				username = @get 'username'
				return "/ws/admin/user/#{username}"

			isNew: () ->
				if @get('id')
					return false
				return true

			idAttribute: 'username'

)