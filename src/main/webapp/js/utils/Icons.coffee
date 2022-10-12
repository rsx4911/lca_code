define () ->

	isCapital = (char) ->
		asInt = char.charCodeAt(0)
		if asInt < 65 or asInt > 90
			return false
		return true

	getFlowAppendix = (ref) ->
		if !ref.flowType
			return ''
		return '_' + ref.flowType.substring(0, ref.flowType.indexOf('_'))

	getProcessAppendix = (ref) ->
		if ref.processType is 'LCI_RESULT' or ref.processType is 'SYSTEM'
			processPart = 'system'
		else if ref.processType is 'UNIT_PROCESS'
			processPart = 'unit'
		flowType = ref.flowType or ref.exchanges?.find((e) -> e.isQuantitativeReference)?.flow?.flowType
		if flowType is 'PRODUCT_FLOW' or flowType is 'WASTE_FLOW'
			flowPart = flowType.substring(0, flowType.indexOf('_'))
		if !processPart or !flowPart
			return ''
		return '_' + processPart + '_' + flowPart

	get: (ref) ->
		icon = ''
		first = true
		type = if ref.type is 'CATEGORY' or ref.type is 'Category' then ref.categoryType else ref.type
		for char, index in type
			if char isnt '_' and !first and isCapital(char) and type.length > (index + 1) and type[index + 1] isnt '_' and !isCapital(type[index + 1])
				icon += '_'
			first = false
			icon += char
		if ref.type is 'Flow' or ref.type is 'FLOW'
			icon += getFlowAppendix(ref)
		if ref.type is 'Process' or ref.type is 'PROCESS'
			icon += getProcessAppendix(ref)
		if ref.type is 'CATEGORY' or ref.type is 'Category'
			icon = "category/#{icon}"
		return "#{icon}.png".toLowerCase()
