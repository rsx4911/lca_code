define([
				'cs!views/repository/dataset/DatasetSort'
				'cs!utils/Allocation'
				'cs!utils/RiskLevel'
			]

	(Sort, Allocation, RiskLevel) ->

		applyTo: (dataset) ->
			@removeAtSigns dataset
			switch dataset.type
				when 'ImpactMethod'
					Sort.impactCategories dataset
					Sort.nwSets dataset
				when 'ImpactCategory'
					Sort.impactFactors dataset
				when 'NwSet'
					Sort.nwFactors dataset
				when 'Project'
					Sort.projectVariants dataset
					@prepareParameterRedefs dataset
					Sort.parameterRedefs dataset
					if dataset.variants
						for variant in dataset.variants
							variant.allocationMethod = Allocation[variant.allocationMethod]
				when 'ProductSystem'
					Sort.parameterSets dataset
				when 'Process'
					Sort.exchanges dataset, 'exchanges'
					Sort.socialAspects dataset
					Sort.documentation dataset
					Sort.parameters dataset
					@prepareAllocationFactors dataset
					if dataset.socialAspects
						for aspect in dataset.socialAspects
							if aspect.riskLevel
								aspect.riskLevel = RiskLevel[aspect.riskLevel]
							else
								aspect.riskLevel = RiskLevel.DEFAULT
				when 'Flow'
					Sort.flowPropertyFactors dataset
				when 'UnitGroup'
					Sort.units dataset
				when 'DQSystem'
					Sort.indicatorsAndScores dataset
				when 'Result'
					Sort.exchanges dataset, 'flowResults'
				when 'Epd'
					Sort.epdModules dataset

		removeAtSigns: (object) ->
			unless object
				return
			for key in Object.keys(object)
				if key.indexOf('@') is 0
					object[key.substring(1)] = object[key]
					delete object[key]
				else if typeof(object[key]) is 'object'
					@removeAtSigns object[key]

		prepareAllocationFactors: (dataset) ->
			nonCausalAllocationFactors = {}
			causalAllocationFactors = {}
			exchangeMap = {}
			flowMap = {}
			if dataset.exchanges
				for e in dataset.exchanges
					unless e.flow
						continue
					exchangeMap[e.internalId] = e
					flowMap[e.flow.id] = e.flow
			if dataset.allocationFactors?.length
				for factor, index in dataset.allocationFactors
					unless factor.product?.id
						continue
					if factor.allocationType is 'PHYSICAL_ALLOCATION' or factor.allocationType is 'ECONOMIC_ALLOCATION'
						f = nonCausalAllocationFactors[factor.product.id]
						unless f
							f = {product: flowMap[factor.product.id]}
							nonCausalAllocationFactors[factor.product.id] = f
						if factor.allocationType is 'PHYSICAL_ALLOCATION'
							f.physical = {value: factor.value, index: index}
						else if factor.allocationType is 'ECONOMIC_ALLOCATION'
							f.economic = {value: factor.value, index: index}
					else if factor.allocationType is 'CAUSAL_ALLOCATION'
						unless factor.exchange?.internalId
							continue
						unless exchangeMap[factor.exchange.internalId]
							continue
						f = causalAllocationFactors[factor.exchange.internalId]
						unless f
							f = {exchange: exchangeMap[factor.exchange.internalId], products: []}
							causalAllocationFactors[factor.exchange.internalId] = f
						f.products.push {flow: flowMap[factor.product.id], id: factor.product.id, value: factor.value, index: index}
			dataset.nonCausalAllocationFactors = []
			dataset.causalAllocationFactors = []
			for key in Object.keys(nonCausalAllocationFactors)
				dataset.nonCausalAllocationFactors.push nonCausalAllocationFactors[key]
			for key in Object.keys(causalAllocationFactors)
				dataset.causalAllocationFactors.push causalAllocationFactors[key]
			Sort.allocationFactors dataset

		prepareParameterRedefs: (dataset) ->
			parameters = {}
			unless dataset.variants
				return
			for variant, vIndex in dataset.variants
				unless variant.parameterRedefs
					continue
				for param, pIndex in variant.parameterRedefs
					contextId = 'global'
					if param.context
						contextId = param.context.id
					p = parameters[contextId + param.name]
					unless p
						p = {name: param.name, context: param.context, values: {}}
						p.path = 'variants[' + vIndex + '].parameterRedefs[' + pIndex + ']'
						contextId = if param.context then param.context.id else 'global'
						parameters[contextId + param.name] = p
					commentPath = 'variants[' + variant.productSystem.id + '-' + variant.name + '].parameterRedefs[' + contextId + '-' + param.name + ']'
					p.values[variant.productSystem.id] = {variant: variant.name, value: param.value, path: p.path + '.value', commentPath: commentPath}
			dataset.parameterRedefs = []
			for key in Object.keys(parameters)
				p = parameters[key]
				values = []
				for variant in dataset.variants
					unless p.values[variant.productSystem.id]
						p.values[variant.productSystem.id] = 0
				for key2 in Object.keys(p.values)
					values.push p.values[key2]
				p.values = values
				dataset.parameterRedefs.push p

)