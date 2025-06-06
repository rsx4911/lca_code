define([
				'backbone'
				'cs!models/CurrentUser'
			]

	(Backbone, currentUser) ->

		class Repository extends Backbone.Model

			url: () ->
				group = @get 'group'
				name = @get 'name'
				if currentUser.isLoggedIn()
					return "ws/repository/#{group}/#{name}"
				return "ws/public/repository/#{group}/#{name}"

			isNew: () ->
				isNew = @get 'isNew'
				if isNew
					return true
				return false

			getPath: () ->

			idAttribute: 'name'

)