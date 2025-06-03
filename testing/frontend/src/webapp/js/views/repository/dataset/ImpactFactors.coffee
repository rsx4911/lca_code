define () ->

	map: (impactFactors) ->
		map = {}
		uncategorizedLabel = 'Uncategorized factors'
		map[uncategorizedLabel] = {'': []}
		if impactFactors
			for f in impactFactors
				unless f.flow 
					continue
				cat = if f.flow.category then f.flow.category.split('/') else []
				if cat.length < 2
					map[uncategorizedLabel][''].push f
				else
					compartment = cat[cat.length - 2]
					subCompartment = cat[cat.length - 1]
					map[compartment] = map[compartment] or {}
					map[compartment][subCompartment] = map[compartment][subCompartment] or []
					map[compartment][subCompartment].push f
		return map
