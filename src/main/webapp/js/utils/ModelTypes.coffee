define () ->

	PROJECT: 'Projects'
	PRODUCT_SYSTEM: 'Product systems'
	IMPACT_METHOD: 'Impact methods'
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
	CATEGORY: 'Category'
	DQ_SYSTEM: 'Data quality systems'

	ordinal: (type) ->
		for key, index in Object.keys(@)
			if key is type
				return index
		return -1