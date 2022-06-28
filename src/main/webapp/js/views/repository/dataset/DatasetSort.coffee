define () ->

	sortByName: (list, subfield) ->
		unless list
			return
		list.sort (e1, e2) ->
			if subfield
				e1 = e1[subfield]
				e2 = e2[subfield]
			name1 = e1.name
			name2 = e2.name
			if name1.toLowerCase() < name2.toLowerCase()
				return -1
			if name2.toLowerCase() < name1.toLowerCase()
				return 1
			return 0

	projectVariants: (dataset) ->
		@sortByName dataset.variants

	impactCategories: (dataset) ->
		@sortByName dataset.impactCategories

	impactFactors: (dataset) ->
		@sortByName dataset.impactFactors, 'flow'

	nwSets: (dataset) ->
		@sortByName dataset.nwSets

	nwFactors: (dataset) ->
		@sortByName dataset.factors, 'impactCategory'

	exchanges: (dataset, field) ->
		exchanges = dataset[field]
		unless exchanges
			return
		exchanges.sort (e1, e2) ->
			if e1.flow.flowType is 'PRODUCT_FLOW' and e2.flow.flowType isnt 'PRODUCT_FLOW'
				return -1
			if e1.flow.flowType isnt 'PRODUCT_FLOW' and e2.flow.flowType is 'PRODUCT_FLOW'
				return 1
			if e1.flow.flowType is 'WASTE_FLOW' and e2.flow.flowType isnt 'WASTE_FLOW'
				return -1
			if e1.flow.flowType isnt 'WASTE_FLOW' and e2.flow.flowType is 'WASTE_FLOW'
				return 1
			if e1.flow.name.toLowerCase() < e2.flow.name.toLowerCase()
				return -1
			if e2.flow.name.toLowerCase() < e1.flow.name.toLowerCase()
				return 1
			return 0

	allocationFactors: (dataset) ->
		unless dataset.exchanges
			return
		if dataset.nonCausalAllocationFactors
			dataset.nonCausalAllocationFactors.sort (f1, f2) ->
				if f1.product.name.toLowerCase() < f2.product.name.toLowerCase()
					return -1
				if f1.product.name.toLowerCase() > f2.product.name.toLowerCase()
					return 1
				return 0
		if dataset.causalAllocationFactors
			order = {}
			for exchange, i in dataset.exchanges
				order[exchange.internalId] = i
			dataset.causalAllocationFactors.sort (f1, f2) ->
				return order[f1.exchange.internalId] - order[f2.exchange.internalId]
			order = {}
			for exchange, i in dataset.exchanges
				order[exchange.internalId] = i
			for factor in dataset.causalAllocationFactors
				factor.products.sort (p1, p2) ->
					return order[p1.id] - order[p2.id]

	socialAspects: (dataset) ->
		@sortByName dataset.socialAspects, 'socialIndicator'

	parameters: (dataset) ->
		@sortByName dataset.parameters

	parameterSets: (dataset) ->
		unless dataset.parameterSets
			return
		dataset.parameterSets.sort (s1, s2) ->
			if s1.isBaseline isnt s2.isBaseline
				if s1.isBaseline
					return -1
				return 1
			return 0

	parameterRedefs: (dataset) ->
		unless dataset.parameterRedefs
			return
		dataset.parameterRedefs.sort (p1, p2) ->
			type1 = if p1.context then p1.context.type else 'Global'
			type2 = if p2.context then p2.context.type else 'Global'
			if type1 isnt type2
				context1 = if type1 is 'Process' then 1 else if type1 is 'ImpactMethod' then 2 else 0
				context2 = if type2 is 'Process' then 1 else if type2 is 'ImpactMethod' then 2 else 0
				if context1 < context2
					return -1
				return 1
			if p1.context and p2.context
				if p1.context.name.toLowerCase() < p2.context.name.toLowerCase()
					return -1	
				if p2.context.name.toLowerCase() < p1.context.name.toLowerCase()
					return 1
			if p1.name.toLowerCase() < p2.name.toLowerCase()
				return -1	
			if p2.name.toLowerCase() < p1.name.toLowerCase()
				return 1
			return 0

	flowPropertyFactors: (dataset) ->
		unless dataset.flowProperties
			return
		dataset.flowProperties.sort (f1, f2) ->
			if f1.isRefFlowProperty
				return -1
			if f2.isRefFlowProperty
				return 1
			if f1.flowProperty.name.toLowerCase() < f2.flowProperty.name.toLowerCase()
				return -1	
			if f2.flowProperty.name.toLowerCase() < f1.flowProperty.name.toLowerCase()
				return 1
			return 0

	units: (dataset) ->
		unless dataset.units
			return
		dataset.units.sort (u1, u2) ->
			if u1.isRefUnit
				return -1
			if u2.isRefUnit
				return 1
			if u1.name.toLowerCase() < u2.name.toLowerCase()
				return -1	
			if u2.name.toLowerCase() < u1.name.toLowerCase()
				return 1
			return 0
						
	indicatorsAndScores: (dataset) ->
		unless dataset.indicators
			return
		dataset.indicators.sort (i1, i2) ->
			return i1.position - i2.position
		for indicator in dataset.indicators
			unless indicator.scores
				continue
			indicator.scores.sort (s1, s2) ->
				return s1.position - s2.position
