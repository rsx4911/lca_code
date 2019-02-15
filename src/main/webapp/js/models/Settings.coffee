define([
				'backbone'
			]

	(Backbone) ->

		class Settings extends Backbone.Collection

			url: 'ws/public/settings'

			getVal: (key) ->
				for model in @models
					if model.get('name') is key
						return model.get 'value'

			setVal: (key, value) ->
				for model in @models
					if model.get('name') is key
						model.set 'value', value

			is: (key) ->
				value = @getVal key
				return value is 'true' or value is true 

			toMap: () ->
				map = {}
				for model in @models
					map[model.get('name')] = model.get 'value'
				return map

			toList: () ->
				list = []
				for model in @models
					list.push model.toJSON()
				return list

		return new Settings()

)