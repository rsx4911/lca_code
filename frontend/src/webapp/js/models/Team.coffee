define([
				'backbone'
			]

	(Backbone) ->

		class Team extends Backbone.Model

			url: () ->
				teamname = @get 'teamname'
				return "ws/usermanager/team/#{teamname}"

			isNew: () ->
				if @get('id')
					return false
				return true

			idAttribute: 'teamname'

)