define([
				'backbone'
			]

	(Backbone) ->

		class Group extends Backbone.Model

			url: () ->
				name = @get 'name'
				return "ws/group/#{name}"

			isNew: () ->
				isNew = @get 'isNew'
				if isNew
					return true
				return false

			idAttribute: 'name'

)