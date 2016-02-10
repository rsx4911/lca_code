define () ->

	get: (ref, additionalTypeInfo) ->
		icon = ''
		first = true
		for char in ref.type 
			asInt = char.charCodeAt(0)
			if !first and asInt >= 65 and asInt <= 90
				icon += '_'
			first = false
			icon += char
		if ref.type is 'Flow'
			flowType = if additionalTypeInfo then additionalTypeInfo else ref.flowType
			icon += '_' + flowType.substring(0, flowType.indexOf('_'))
		if ref.type is 'Process'
			processType = if additionalTypeInfo then additionalTypeInfo else ref.processType
			if processType is 'LCI_RESULT'
				icon += '_system'
		return "#{icon}.png".toLowerCase()