define () ->

	isCapital = (char) ->
		asInt = char.charCodeAt(0)
		if asInt < 65 or asInt > 90
			return false
		return true

	get: (ref, additionalTypeInfo) ->
		icon = ''
		first = true
		for char, index in ref.type 
			if !first and isCapital(char) and !isCapital(ref.type[index + 1])
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
