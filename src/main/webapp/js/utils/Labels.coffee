define () ->

	map: 
		'id': 'UUID'
		'telephone': 'Phone'
		'telefax': 'Fax'
		'inputParameter': 'Type'
		'referenceExchange.flow': 'Reference product'
		'targetUnit.name': 'Target unit'
		'activityUnit.name': 'Activity unit'
		'parameterRedefs': 'Parameters'
		'copyright': 'Copyright protected'
		'exchanges': 'Inputs/Outputs'
		'processDocumentation.restrictionsDescription': 'Access & use restrictions'
		'processDocumentation.inventoryMethodDescription': 'LCI method'
		'processDocumentation.modelingConstantsDescription': 'Modeling constants'
		'processDocumentation.completenessDescription': 'Data completeness'
		'processDocumentation.dataSelectionDescription': 'Data selection'
		'processDocumentation.dataTreatmentDescription': 'Data treatment'
		'processDocumentation.samplingDescription': 'Sampling procedure'
		'processDocumentation.dataCollectionDescription': 'Data collection period'
		'processDocumentation.projectDescription': 'Project'
		'dqSystem': 'Process data quality schema'
		'exchangeDqSystem': 'Input/Output data quality schema'
		'socialDqSystem': 'Social data quality schema'
		'exchanges.dqEntry': 'Data quality'
		'exchanges.defaultProvider': 'Provider'
		'socialAspects.quality': 'Data quality'
		'nwSet': 'Normalisation & Weighting set'

	get: (type, path) ->
		if type is 'CURRENCY' and path is 'code'
			return 'Currency code'
		if type is 'PROCESS' and path is 'dqEntry'
			return 'Process data quality entry'
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
