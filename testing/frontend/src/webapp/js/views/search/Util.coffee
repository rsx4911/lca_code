define([
				'cs!utils/ModelTypes'
			]

	(ModelTypes) ->

		isInResult = (key, allAggregations) ->
			for aggregation in allAggregations
				if aggregation.name is key
					return true
				if aggregation.entries
					for entry in aggregation.entries
						if isInSubAggregation(key, entry)
							return true
			return false

		isInSubAggregation = (key, entry) ->
			if entry.subAggregationName is key
				return true
			if entry.subEntries
				for subEntry in entry.subEntries
					if isInSubAggregation(key, subEntry)
						return true
			return false

		urlPart = (base, query, page, pageSize, aggregations, allAggregations) ->
			url = base
			isFirst = true
			if query
				url += "query=#{encodeURIComponent(query)}"
				isFirst = false
			if page
				unless isFirst
					url += '&'
				url += "page=#{page}"
				isFirst = false
			if pageSize
				unless isFirst
					url += '&'
				url += "pageSize=#{pageSize}"
				isFirst = false
			if aggregations and Object.keys(aggregations).length
				for key in Object.keys(aggregations)
					if allAggregations and !isInResult(key, allAggregations)
						continue
					for value in aggregations[key]
						unless isFirst
							url += '&'
						url += "#{encodeURIComponent(key)}=#{encodeURIComponent(value)}"
						isFirst = false
			if url.indexOf('/', url.length - 1) isnt -1
				url = url.substring(0, url.length - 1)
			if url.indexOf('?', url.length - 1) isnt -1
				url = url.substring(0, url.length - 1)
			return url

		aggregationsWithout: (aggregations, type, value) ->
			copy = {}
			keys = Object.keys(aggregations)
			for key in keys
				copy[key] = []
				for v in aggregations[key]
					if type is key and (!v or v is value)
						continue
					copy[key].push v
			return copy

		aggregationsWith: (aggregations, type, value, multiple) ->
			copy = {}
			unless aggregations[type]
				aggregations[type] = []
			keys = Object.keys(aggregations)
			for key in keys
				if type is 'repositoryId' and key is 'group'
					continue						
				if type is 'group' and key is 'repositoryId'
					continue						
				copy[key] = []
				if multiple or type isnt key
					for v in aggregations[key]
						copy[key].push v
				if type is key and $.inArray(value, copy[key]) is -1
					copy[key].push value
			return copy

		getUrlPart: (base, query, page, pageSize, aggregations, allAggregations) ->
            return urlPart base, query, page, pageSize, aggregations, allAggregations
			
		getLabel: (type, value, label) ->
			if label
				return label
			if type is 'type'
				return ModelTypes[value]					
			if type is 'mostRecent'
				if value is 'true'
					return 'Yes'
				if value is 'false'
					return 'No'
			if type is 'modellingApproach'
				if value is 'PHYSICAL'
					return 'Phsycial allocation'
				else if value is 'ECONOMIC'
					return 'Economic allocation'
				else if value is 'CAUSAL'
					return 'Causal allocation'
				else if value is 'NONE'
					return 'No allocation'
				else if value is 'UNKNOWN'
					return 'Unknown'
			if type is 'processType'
				if value is 'UNIT_PROCESS' or value is 'UNIT'
					return 'Unit process'
				else if value is 'LCI_RESULT' or value is 'SYSTEM'
					return 'System process'
				else if value is 'UNKNOWN'
					return 'Unknown'
			if type is 'flowType'
				if value is 'ELEMENTARY_FLOW'
					return 'Resource/Emission'
				else if value is 'WASTE_FLOW'
					return 'Waste'
				else if value is 'PRODUCT_FLOW'
					return 'Product'
			if type is 'reviewType'
				if value is 'unreviewed'
					return 'No reviews'
				if value is 'unspecified'
					return 'Not specified'
			return value

		getAggregationLabel: (type) ->
			if type is 'group'
				return 'Group/Repository'
			if type is 'repositoryTags'
				return 'Repository tag'
			if type is 'mostRecent'
				return 'Most recent'
			if type is 'tags'
				return 'Data set tag'
			if type is 'type'
				return 'Model type'
			if type is 'flowType'
				return 'Flow type'
			if type is 'processType'
				return 'Process type'
			if type is 'location'
				return 'Location'
			if type is 'reviewType'
				return 'Review type'
			if type is 'complianceDeclaration'
				return 'Compliance declaration'
			if type is 'flowCompleteness'
				return 'Flow completeness'
			if type is 'categoryPaths'
				return 'Category'
			if type is 'validFromYear'
				return 'Start of validity'
			if type is 'validUntilYear'
				return 'End of validity'
			if type is 'contact'
				return 'Data set owner'
			if type is 'modellingApproach'
				return 'Modelling approach'

)