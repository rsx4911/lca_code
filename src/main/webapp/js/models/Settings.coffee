define([
				'backbone'
			]

	(Backbone) ->

		class Settings extends Backbone.Model

			url: 'ws/public/settings'

			getVal: (key) ->
				return @get key

			setVal: (key, value) ->
				@set key, value

			is: (key) ->
				value = @getVal key
				return value is 'true' or value is true 

			toMap: () ->
				return @toJSON()

			toList: () ->
				map = @toJSON()
				list = []
				for key in Object.keys(map)
					list.push({ key: key, value: map[key] })
				return list

			isGladConfigured: () ->
				return !!@getVal('GLAD_URL') and !!@getVal('GLAD_API_KEY') and !!@getVal('GLAD_DATAPROVIDER')

		return new Settings()

)