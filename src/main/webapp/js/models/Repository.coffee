define([
				'backbone'
			]

	(Backbone) ->

		class Repository extends Backbone.Model

			url: () ->
				group = @get 'group'
				name = @get 'name'
				return "ws/repository/#{group}/#{name}"

			isNew: () ->
				isNew = @get 'isNew'
				if isNew
					return true
				return false

			getPath: () ->

			idAttribute: 'name'

)