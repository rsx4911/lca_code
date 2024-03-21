define () ->

	isCapital = (char) ->
		asInt = char.charCodeAt(0)
		if asInt < 65 or asInt > 90
			return false
		return true

	getFlowAppendix = (flowType) ->
		if !flowType
			return ''
		return '_' + flowType.substring(0, flowType.indexOf('_'))

	getProcessAppendix = (processType, flowType) ->
		if processType is 'LCI_RESULT' or processType is 'SYSTEM'
			processPart = 'system'
		else if processType is 'UNIT_PROCESS'
			processPart = 'unit'
		if flowType is 'PRODUCT_FLOW' or flowType is 'WASTE_FLOW'
			flowPart = flowType.substring(0, flowType.indexOf('_'))
		if !processPart or !flowPart
			return ''
		return '_' + processPart + '_' + flowPart

	_getByType = (type, isCategory, processType, flowType) ->
		icon = ''
		first = true
		for char, index in type
			if char isnt '_' and !first and isCapital(char) and type.length > (index + 1) and type[index + 1] isnt '_' and !isCapital(type[index + 1])
				icon += '_'
			first = false
			icon += char
		if type is 'Flow' or type is 'FLOW'
			icon += getFlowAppendix(flowType)
		if type is 'Process' or type is 'PROCESS'
			icon += getProcessAppendix(processType, flowType)
		if isCategory
			icon = "category/#{icon}"
		return "#{icon}.png".toLowerCase()

	get: (ref) ->
		flowType = ref.flowType or ref.exchanges?.find((e) -> e.isQuantitativeReference)?.flow?.flowType
		return _getByType ref.type, ref.isCategory, ref.processType, flowType

	getByType: (type, isCategory, processType, flowType) ->
		return _getByType type, isCategory, processType, flowType