define () ->

	map: (impactFactors) ->
		map = {}
		uncategorizedLabel = 'Uncategorized factors'
		map[uncategorizedLabel] = {'': []}
		for f in impactFactors
			cat = f.flow.category or []
			if cat.length < 2
				map[uncategorizedLabel][''].push f
			else
				compartment = cat[cat.length - 2]
				subCompartment = cat[cat.length - 1]
				map[compartment] = map[compartment] or {}
				map[compartment][subCompartment] = map[compartment][subCompartment] or []
				map[compartment][subCompartment].push f
		return map
