define () ->

	sortBy: (collection, attribute, options) ->
		return collection.sort (e1, e2) =>
			v1 = @getValue e1, attribute
			v2 = @getValue e2, attribute	
			if options?.valueModifier
				v1 = options.valueModifier v1
				v2 = options.valueModifier v2
			direction = 1
			if options?.direction is 'descending'
				direction = -1
			if v1 < v2
				return -1 * direction
			if v1 > v2
				return 1 * direction
			return 0

	getValue: (element, attribute) ->
		if attribute.indexOf('.') isnt -1
			field = attribute.substring(0, attribute.indexOf('.'))
			suffix = attribute.substring(attribute.indexOf('.') + 1)
			element = element[field]
			return @getValue(element, suffix)
		return element[attribute]
