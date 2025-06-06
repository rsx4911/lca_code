define () ->

	PROJECT: 'Projects'
	PRODUCT_SYSTEM: 'Product systems'
	IMPACT_METHOD: 'Impact methods'
	IMPACT_CATEGORY: 'Impact categories'
	PROCESS: 'Processes'
	FLOW: 'Flows'
	FLOW_PROPERTY: 'Flow properties'
	UNIT_GROUP: 'Unit groups'
	ACTOR: 'Actors'
	SOURCE: 'Sources'
	LOCATION: 'Locations'
	SOCIAL_INDICATOR: 'Social indicators'
	CURRENCY: 'Currencies'
	PARAMETER: 'Parameters'
	CATEGORY: 'Categories'
	DQ_SYSTEM: 'Data quality systems'
	EPD: 'EPDs'
	RESULT: 'Results'
	LIBRARY: 'Libraries'

	ordinal: (type) ->
		for key, index in Object.keys(@)
			if key is type
				return index
		return -1

	singular: (type) ->
		if type is 'DQ_SYSTEM'
			return 'Data quality system'
		text = type.replace('_', ' ').toLowerCase()
		text = text[0].toUpperCase() + text.substring(1, text.length)
		return text

	plural: (type) ->
		return @[type]
