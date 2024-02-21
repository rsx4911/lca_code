define () ->

	map: (exchanges, dsType) ->
		model = {byProducts: [], producedWaste: [], avoidedProducts: [], usedProducts: [], recycledWaste: [], emissions: [], resources: []}
		if exchanges
			for e in exchanges
				if dsType is 'RESULT'
					e.internalId = e.flow.id
				if e.isInput
					switch e.flow.flowType
						when 'PRODUCT_FLOW'
							model.usedProducts.push e
						when 'WASTE_FLOW'
							model.recycledWaste.push e
						when 'ELEMENTARY_FLOW'
							model.resources.push e
				else
					if e.isQuantitativeReference
						model.refProduct = e
					else if e.isAvoidedProduct
						model.avoidedProducts.push e
					else 
						switch e.flow.flowType
							when 'PRODUCT_FLOW'
								model.byProducts.push e
							when 'WASTE_FLOW'
								model.producedWaste.push e
							when 'ELEMENTARY_FLOW'
								model.emissions.push e
		model.resources = @mapByCategory model.resources, 'Uncategorized resources'
		model.emissions = @mapByCategory model.emissions, 'Uncategorized emissions'
		return model

	mapByCategory: (list, uncategorizedLabel) ->
		map = {}
		map[uncategorizedLabel] = {'': []}
		for e in list
			cat = if e.flow and e.flow.category then e.flow.category.split('/') else []
			if cat.length < 2
				map[uncategorizedLabel][''].push e
			else
				compartment = cat[cat.length - 2]
				subCompartment = cat[cat.length - 1]
				map[compartment] = map[compartment] or {}
				map[compartment][subCompartment] = map[compartment][subCompartment] or []
				map[compartment][subCompartment].push e
		return map					
