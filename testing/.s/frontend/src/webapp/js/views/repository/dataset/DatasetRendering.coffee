define([
				'cs!utils/DataQuality'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/Labels'
				'cs!utils/ModelTypes'
			]

	(DataQuality, Format, Icons, Labels, ModelTypes) ->

		_getValue = (object, path) ->
			unless path
				return null
			unless object
				return null
			if path.indexOf('.') is -1 and path.indexOf('[') is -1
				return object[path]
			subpath = path
			if subpath.indexOf('.') isnt -1 
				subpath = path.substring 0, path.indexOf('.')
			arrayPos = null
			if subpath.indexOf('[') isnt -1
				arrayPos = subpath.substring(subpath.indexOf('[') + 1, subpath.indexOf(']'))
				subpath = subpath.substring 0, subpath.indexOf('[')
			object = object[subpath]
			unless object
				return null
			if (arrayPos and (parseInt(arrayPos) is NaN or parseInt(arrayPos) > 0)) or parseInt(arrayPos) is 0
				object = object[arrayPos]
			if path.indexOf('.') is -1
				return object
			path = path.substring path.indexOf('.') + 1
			return _getValue object, path

		getArrayValues = (ref, ref2, type, path, subPath) ->
			values = []
			value = if path then _getValue(ref, path) else ref
			value2 = if path then _getValue(ref2, path) else ref2
			if ref and value
				for v in value
					if subPath
						v = _getValue v, subPath
					other = if ref2 then findValue(type, v, value2, ref, ref2) else null
					values.push [v, other]
			if ref2 and value2
				for other in value2
					if subPath
						other = _getValue other, subPath
					v = if ref then findValue(type, other, value, ref, ref2) else null
					unless v
						values.push [null, other]
			return values

		findValue = (type, value, array, ref, ref2) ->
			if !array or !array.length or !value
				return null
			for v in array
				if !v
					continue
				if !type and v is value
					return v
				switch type
					when 'UNIT' then if v.name is value.name then return v
					when 'FLOW_PROPERTY_FACTOR' then if v.flowProperty?.id is value.flowProperty?.id then return v
					when 'SOURCE' then if v.id is value.id then return v
					when 'ACTOR' then if v.id is value.id then return v
					when 'DQ_INDICATOR' then if v.position is value.position then return v
					when 'DQ_SCORE' then if v.position is value.position then return v
					when 'SOCIAL_ASPECT' then if v.socialIndicator?.id is value.socialIndicator?.id then return v
					when 'EXCHANGE' 
						if ref and ref2 and ref.id is ref2.id
							if v.internalId is value.internalId 
								return v
						else if v.flow?.id is value.flow?.id
							return v
					when 'PARAMETER' then if v.name is value.name and v.isInputParameter is value.isInputParameter then return v
					when 'FLOW' then if v.id is value.id then return v
					when 'PRODUCT' then if v.id is value.id then return v
					when 'IMPACT_CATEGORY' then if v.id is value.id then return v
					when 'IMPACT_FACTOR' then if v.flow?.id is value.flow?.id then return v
					when 'NW_SET' then if v.id is value.id then return v
					when 'NW_FACTOR' then if v.impactCategory?.id is value.impactCategory?.id then return v
					when 'ALLOCATION_FACTOR' then if v.type is value.type && v.exchange?.internalId is value.exchange?.internalId && v.product?.id is value.product?.id then return v
					when 'VARIANT' then if (v.productSystem?.id + '-' + v.name) is (value.productSystem?.id + '-' + value.name) then return v
					when 'PARAMETER_REDEF' then if (if v.context then v.context.id else 'global') + v.name is (if value.context then value.context.id else 'global') + value.name then return v
					when 'PARAMETER_REDEF_SET' then if v.name is value.name then return v
					when 'EPD_MODULE' then if v.name is value.name and v.result?.id is value.result?.id then return v
					when 'IMPACT_RESULT' then if v.indicator?.id is value.indicator?.id then return v
					when 'FLOW_RESULT' then if v.flow?.id is value.flow?.id then return v
					when 'REVIEW' then if v.report?.id is value.report?.id then return v
					when 'ASPECT' then if v.aspect is value.aspect then return v
					when 'SCOPE' then if v.name is value.name then return v
					when 'SCOPE_METHOD' then if v is value then return v
					when 'COMPLIANCE_DECLARATION' then if v.system?.id is value.system?.id then return v
			return null

		hasOneOf = (obj1, obj2, fields) ->
			for field in fields
				if (obj1 and obj1[field]) or (obj2 and obj2[field])
					return true
			return false

		hasAtLeastOne = (ref, ref2, arrayPath, elementPath, filter) ->
			return _hasAtLeastOne(ref, arrayPath, elementPath, filter) or _hasAtLeastOne(ref2, arrayPath, elementPath, filter)

		_hasAtLeastOne = (object, arrayPath, elementPath, filter) ->
			unless object
				return false
			unless arrayPath
				return false
			array = _getValue object, arrayPath
			unless array
				return false
			unless elementPath
				return array.length > 0
			for value in array
				if (!filter || !filter(value)) and value[elementPath]
					return true
			return false

		compare = (value, value2, compareTo) ->
			if compareTo and (compareTo.deleted or compareTo.notFound)
				return 'added'
			if (value2 or value2 is 0) && !(value || value is 0)
				return 'removed'
			if (value or value is 0) && !(value2 || value2 is 0)
				return 'added'
			if $.isArray(value)
				for v in value
					found = false
					for v2 in value2
						if v is v2
							found = true
							break
					if !found
						return 'changed'
				return null
			if !isRef(value) and (value or value2) and value isnt value2
				return 'changed'
			if isRef(value) and value.id isnt value2.id
				return 'changed'
			return null

		isRef = (val) ->
			return $.isPlainObject(val) and val['type'] and val['id']

		compareUncertainty = (u1, u2) ->
			if u1 && !u2
				return 'added'
			if u2 && !u1
				return 'removed'
			if u1 && u2
				if u1.distributionType isnt u2.distributionType
					return 'changed'
				switch u1.distributionType
					when 'LOG_NORMAL_DISTRIBUTION' 
						if u1.geomMean isnt u2.geomMean or u1.geomSd isnt u2.geomSd
							return 'changed'
					when 'NORMAL_DISTRIBUTION' 
						if u1.mean isnt u2.mean or u1.sd isnt u2.sd
							return 'changed'
					when 'TRIANGLE_DISTRIBUTION' 
						if u1.minimum isnt u2.minimum or u1.maximum isnt u2.maximum or u1.mode isnt u2.mode
							return 'changed'
					when 'UNIFORM_DISTRIBUTION' 
						if u1.minimum isnt u2.minimum or u1.maximum isnt u2.maximum
							return 'changed'
			return null

		isCapital = (char) ->
			asInt = char.charCodeAt(0)
			if asInt < 65 or asInt > 90
				return false
			return true

		getSpecificTypeLabel = (type, value) ->
			switch type 
				when 'FlowPropertyType'
					switch value
						when 'ECONOMIC_QUANTITY' then return 'Economic flow property'
						when 'PHYSICAL_QUANTITY' then return 'Physical flow property'
				when 'FlowType'
					switch value
						when 'ELEMENTARY_FLOW' then return 'Elementary flow'
						when 'PRODUCT_FLOW' then return 'Product flow'
						when 'WASTE_FLOW' then return 'Waste flow'
				when 'ProcessType'
					switch value
						when 'UNIT_PROCESS' then return 'Unit process'
						when 'LCI_RESULT' then return 'System process'
				when 'EpdType'
					switch value
						when 'AVERAGE_DATASET' then return 'Average dataset'
						when 'GENERIC_DATASET' then return 'Generic dataset'
						when 'REPRESENTATIVE_DATASET' then return 'Representative dataset'
						when 'SPECIFIC_DATASET' then return 'Specific dataset'
						when 'TEMPLATE_DATASET' then return 'Template dataset'
			return ''

		getFunctions: (dataset, compareTo) ->
			return {
				formatDate: Format.dateTime
				formatScientific: Format.scientific
				formatRelative: (value) -> return Format.relative value, 4
				formatCommitDescription: Format.commitDescription
				getIcon: Icons.get
				getIconByType: Icons.getByType
				getDQColor: DataQuality.getColor
				getValue: @getValue
				getArrayValues: getArrayValues
				findValue: findValue
				hasAtLeastOne: hasAtLeastOne
				hasOneOf: hasOneOf
				compare: (value, value2) -> return if compareTo then compare(value, value2, compareTo) else null
				compareUncertainty: compareUncertainty
				getTypeAsEnum: @getTypeAsEnum
				getLabel: (path) => return Labels.get @getTypeAsEnum(dataset.type), path
				getTypeLabel: (type) -> return ModelTypes[type]
				getSpecificTypeLabel: getSpecificTypeLabel		
			}

		getValue: (object, path, alternativePath, formatter) ->
			unless path
				return null
			unless object
				return null
			value = _getValue object, path
			if !value && alternativePath
				value = _getValue object, alternativePath
			if formatter
				value = formatter value, object
			return value

		getTypeAsEnum: (type) ->
			asEnum = ''
			first = true
			for char, index in type 
				if !first and isCapital(char) and !isCapital(type[index + 1])
					asEnum += '_'
				first = false
				asEnum += char
			return asEnum.toUpperCase()

)