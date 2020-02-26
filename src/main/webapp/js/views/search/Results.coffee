define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Toggle'
				'cs!views/repository/Download'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/search/results'
			]

	(Backbone, Events, Icons, ModelTypes, Renderer, Toggle, Download, Router, currentUser, template) ->

		class SearchResultsView extends Backbone.View

			className: 'search-view'

			aggregationOrder: 
				group: 1
				type: 2
				flowType: 3
				processType: 4
				modellingApproach: 5
				location: 6
				categoryPaths: 7
				validFromYear: 8
				validUntilYear: 9
				contact: 10

			events: 
				'click a:not([href="#"])': (event) -> Events.followLink event

			initialize: (options) ->
				@aggregations = {}
				if options
					for option in Object.keys(options)	
						if option is 'query'
							@query = options[option]
						else if option is 'page'
							@page = options[option]
						else if option is 'pageSize'
							@pageSize = options[option]
						else
							values = options[option]
							if $.isArray(values)
								@aggregations[option] = values							
							else
								@aggregations[option] = [values]

			render: (renderOptions) ->
				url = @getUrlPart 'ws/public/search?', @query, @page, @pageSize, @aggregations
				$.ajax
					type: 'GET'
					url: url
					success: (result) =>
						allAggregations = []
						for aggregation in result.aggregations
							allAggregations.push aggregation 
						@correctUrl result
						@prepareAggregations result
						result.typeFiltered = !!@aggregations['type']
						result.getIcon = Icons.get
						result.isPublic = !currentUser.isLoggedIn()
						result.getAggregationLabel = (type) => @getAggregationLabel type
						result.getLabel = (type, value, label) => @getLabel type, value, label
						result.getPagingUrl = (page) => return @getUrlPart 'search/', @query, page, @pageSize, @aggregations, allAggregations
						result.isSelectedAggregationValue = (type, value) => return @aggregations[type] and $.inArray(value, @aggregations[type]) isnt -1
						result.getSubCount = (entry) => @getSubCount entry
						result.getTotalCount = (aggregation) => @getTotalCount aggregation
						result.getAggregationUrl = (type, value, without = false) => 
							aggregations = if without then @aggreagtionsWithout(type, value) else @aggreagtionsWith(type, value)
							return @getUrlPart 'search/', @query, 1, @pageSize, aggregations, allAggregations
						result.query = @query
						@$el.html template result
						Renderer.render @, renderOptions
						Toggle.init @$el
						@$('[data-format][data-datatype=dataset]').on 'click', (event) ->
							Events.preventDefault event
							target = $ Events.target event
							Download.dataset(target.attr('data-group'), target.attr('data-repo'), target.attr('data-type'), target.attr('data-ref-id'), target.attr('data-commit-id'), target.attr('data-format'))
						@$('[data-format][data-datatype=repository]').on 'click', (event) ->
							Events.preventDefault event
							target = $ Events.target event
							Download.repository(target.attr('data-group'), target.attr('data-repo'), target.attr('data-commit-id'), null, target.attr('data-format'))
						@$('#page-size').on 'change', (event) => Router.navigate @getUrlPart 'search/', @query, 1, $(Events.target(event)).val(), @aggregations, allAggregations
						if @query
							for textElement in $('.search-view .content-box .result-text')
								@highlight @query, $(textElement)

			correctUrl: (result) ->
				copy = {}
				keys = Object.keys(@aggregations)
				for key in keys
					found = false
					for aggregation in result.aggregations
						if aggregation.name is key
							found = true
							break
					unless found
						continue
					copy[key] = []
					for v in @aggregations[key]
						copy[key].push v
				@aggregations = copy
				Router.navigate @getUrlPart('search/', @query, @page, @pageSize, @aggregations),
					trigger: false
					replace: true

			aggreagtionsWithout: (type, value) ->
				copy = {}
				keys = Object.keys(@aggregations)
				for key in keys
					copy[key] = []
					for v in @aggregations[key]
						if type is key and v is value
							continue
						copy[key].push v
				return copy

			aggreagtionsWith: (type, value) ->
				copy = {}
				unless @aggregations[type]
					@aggregations[type] = []
				keys = Object.keys(@aggregations)
				for key in keys
					if type is 'repositoryId' and key is 'group'
						continue						
					copy[key] = []
					for v in @aggregations[key]
						if type isnt key
							copy[key].push v
					if type is key and $.inArray(value, copy[key]) is -1
						copy[key].push value
				return copy

			isInResult: (key, allAggregations) ->
				for aggregation in allAggregations
					if aggregation.name is key
						return true
				return false

			getTotalCount: (aggregation) =>
				count = aggregation.entries.length
				for entry in aggregation.entries
					count += @getSubCount entry
				return count

			getSubCount: (entry) =>
				unless entry.subEntries
					return 0
				count = entry.subEntries.length
				for subEntry in entry.subEntries
					count += @getSubCount subEntry
				return count

			highlight: (word, element) ->
				word = word.toLowerCase()
				text = element.html()
				replaced = ''
				next = text.toLowerCase().indexOf word
				while next isnt -1
					replaced += text.substring(0, next) + '<span class="highlight-result">' + text.substring(next, next + word.length) + '</span>'
					text = text.substring(next + word.length)
					next = text.toLowerCase().indexOf word, next
				replaced += text
				element.html replaced

			getUrlPart: (base, query, page, pageSize, aggregations, allAggregations) ->
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
						if allAggregations and !@isInResult(key, allAggregations)
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

			getLabel: (type, value, label) ->
				console.log(label)
				if label
					return label
				if type is 'type'
					return ModelTypes[value]
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
				return value

			getAggregationLabel: (type) ->
				if type is 'group'
					return 'Group/Repository'
				if type is 'type'
					return 'Model type'
				if type is 'flowType'
					return 'Flow type'
				if type is 'processType'
					return 'Process type'
				if type is 'location'
					return 'Location'
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

			prepareAggregations: (result) ->
				aggregations = []
				repoAggregation = null
				groupAggregation = null
				for aggregation in result.aggregations
					if aggregation.name is 'repositoryId'
						repoAggregation = aggregation
						continue
					if aggregation.name is 'group'
						groupAggregation = aggregation
					aggregations.push aggregation
				result.aggregations = aggregations
				@sortAggregations result
				if repoAggregation and groupAggregation
					@groupRepos groupAggregation, repoAggregation

			groupRepos: (groups, repos) ->
				for entry in repos.entries
					group = entry.key.substring 0, entry.key.indexOf('/')
					groupEntry = @findEntry groups, group
					groupEntry.subAggregationName = repos.name
					groupEntry.subEntries = groupEntry.subEntries or []
					groupEntry.subEntries.push entry

			findEntry: (aggregation, key) ->
				for entry in aggregation.entries
					if entry.key is key
						return entry
				return null

			sortAggregations: (result) ->
				result.aggregations.sort (a, b) =>
					aSelected = if a.name is 'group' then @aggregations['repositoryId'] else @aggregations[a.name]
					bSelected = if b.name is 'group' then @aggregations['repositoryId'] else @aggregations[b.name]
					if aSelected and !bSelected
						return -1
					if bSelected and !aSelected
						return 1
					return @aggregationOrder[a.name] - @aggregationOrder[b.name]



)