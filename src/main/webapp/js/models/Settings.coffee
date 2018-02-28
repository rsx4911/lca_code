define([
				'backbone'
			]

	(Backbone) ->

		class Settings extends Backbone.Collection

			url: 'ws/settings'

			get: (key) ->
				for model in @models
					if model.get('name') is key
						return model.get('value')

			is: (key) ->
				value = @get(key)
				return value is 'true' or value is true 

			toMap: () ->
				map = {}
				for model in @models
					map[model.get('name')] = model.get('value')
				return map

			toList: () ->
				list = []
				for model in @models
					list.push model.toJSON()
				return list

		return new Settings()

)