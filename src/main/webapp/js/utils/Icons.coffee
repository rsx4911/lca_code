define () ->

	isCapital = (char) ->
		asInt = char.charCodeAt(0)
		if asInt < 65 or asInt > 90
			return false
		return true

	get: (ref, additionalTypeInfo) ->
		icon = ''
		first = true
		type = if ref.type is 'CATEGORY' or ref.type is 'Category' then ref.categoryType else ref.type
		for char, index in type
			if char isnt '_' and !first and isCapital(char) and type.length > (index + 1) and type[index + 1] isnt '_' and !isCapital(type[index + 1])
				icon += '_'
			first = false
			icon += char
		if ref.type is 'Flow' or ref.type is 'FLOW'
			flowType = if additionalTypeInfo then additionalTypeInfo else ref.flowType
			if flowType
				icon += '_' + flowType.substring(0, flowType.indexOf('_'))
		if ref.type is 'Process' or ref.type is 'PROCESS'
			processType = if additionalTypeInfo then additionalTypeInfo else ref.processType
			if processType is 'LCI_RESULT' or processType is 'SYSTEM'
				icon += '_system'
		if ref.type is 'CATEGORY' or ref.type is 'Category'
			icon = "category/#{icon}"
		return "#{icon}.png".toLowerCase()
