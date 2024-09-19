define () ->

	map: 
		'id': 'UUID'
		'url': 'URL'
		'email': 'e-mail'
		'cas': 'CAS number'
		'externalFile': 'File'
		'isInputParameter': 'Type'
		'isQuantitativeReference': 'Quantitative reference'
		'refExchange.flow': 'Reference product'
		'targetUnit.name': 'Target unit'
		'activityUnit.name': 'Activity unit'
		'parameterRedefs': 'Parameters'
		'isCopyrightProtected': 'Copyright protected'
		'exchanges': 'Inputs/Outputs'
		'processDocumentation': 'Documentation'
		'processDocumentation.restrictionsDescription': 'Access and use restrictions'
		'processDocumentation.inventoryMethodDescription': 'LCI method'
		'processDocumentation.modelingConstantsDescription': 'Modeling constants'
		'processDocumentation.completenessDescription': 'Data completeness'
		'processDocumentation.dataSelectionDescription': 'Data selection'
		'processDocumentation.dataTreatmentDescription': 'Data treatment'
		'processDocumentation.samplingDescription': 'Sampling procedure'
		'processDocumentation.dataCollectionDescription': 'Data collection period'
		'processDocumentation.projectDescription': 'Project'
		'processDocumentation.complianceDeclarations': 'Compliance'
		'processDocumentation.reviews.reviewType': 'Type'
		'processDocumentation.reviews.reviewers': 'Reviewer'
		'processDocumentation.reviews.assessment.aspect': 'Review aspect'
		'processDocumentation.reviews.assessment.value': 'Quality assessment'
		'processDocumentation.reviews.scope': 'Review scope'
		'processDocumentation.reviews.methods': 'Review methods'
		'dqSystem': 'Process data quality scheme'
		'exchangeDqSystem': 'Input/Output data quality scheme'
		'targetFlowProperty': 'Flow property'
		'targetUnit': 'Unit'
		'refExchange.name': 'Reference product'
		'refProcess': 'Reference process'
		'impactMethod': 'LCIA method'
		'impactCategories.refUnit': 'Reference unit'
		'socialDqSystem': 'Social data quality scheme'
		'exchanges.dqEntry': 'Data quality'
		'exchanges.defaultProvider': 'Provider'
		'exchanges.costs': 'Costs/Revenue'
		'socialAspects.quality': 'Data quality'
		'nwSet': 'Normalisation & Weighting set'
		'nwSets': 'Normalisation & Weighting sets'
		'product.flow': 'Declared product'
		'pcr': 'PCR'
		'modules.name': 'Module'
		'impactResults.indicator': 'Impact category'
		'manufacturing': 'Manufacturing description'
		'productUsage': 'Product usage description'
		'epdType': 'EPD type'
		'manufacturer': 'Declaration owner / Manufacturer'
		'registrationId': 'Registration number'
		'originalEpd': 'Original EPD'
		'urn': 'URN'

	get: (type, path) ->
		if type is 'CURRENCY' and path is 'code'
			return 'Currency code'
		if type is 'PROCESS' and path is 'dqEntry'
			return 'Data quality entry'
		if type is 'EPD' and path is 'validFrom'
			return 'Publication date'
		corrected = path
		while corrected.indexOf('[') isnt -1
			corrected = corrected.substring(0, corrected.indexOf('[')) + corrected.substring(corrected.indexOf(']') + 1)
		if @map[corrected]
			return @map[corrected]
		return @toLabel path

	toLabel: (path) ->
		unless path
			return null
		if path.indexOf('.') isnt -1
			path = path.substring path.lastIndexOf('.') + 1
		if path.indexOf('[') isnt -1
			path = path.substring 0, path.lastIndexOf('[')
			if path is 'impactCategories'
				path = 'impactCategory'
			else if path is 'processes'
				path = 'process'
			else if path is 'flowProperties'
				path = 'flowProperty'
			else if path.charAt(path.length - 1) is 's'
				path = path.substring 0, path.length - 1
		result = ''
		for character, index in path
			if index is 0
				result = character.toUpperCase()
			else if character.toLowerCase() is character
				result += character
			else 
				result += ' ' + character.toLowerCase()
		return result
