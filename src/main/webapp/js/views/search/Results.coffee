define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!app/Router'
				'templates/views/search/results'
			]

	(Backbone, Events, ModelTypes, Renderer, Router, template) ->

		class SearchResultsView extends Backbone.View

			className: 'search-view'

			events: 
				'click a:not([href=#])': (event) -> Events.followLink event

			initialize: (options) ->
				@aggregations = {}
				for option in Object.keys(options)	
					if option is 'query'
						@query = options[option]
					else if option is 'page'
						@page = options[option]
					else
						values = options[option]
						if $.isArray(values)
							@aggregations[option] = values							
						else
							@aggregations[option] = [values]

			render: (renderOptions) ->
				url = @getUrlPart 'ws/search?', @query, @page, @aggregations
				$.ajax
					type: 'GET'
					url: url
					success: (result) =>
						result.getAggregationLabel = (type) => @getAggregationLabel type
						result.getLabel = (type, value) => @getLabel type, value
						result.getPagingUrl = (page) => return @getUrlPart 'search/', @query, page, @aggregations, result
						result.isSelectedAggregationValue = (type, value) => return @aggregations[type] and $(value, @aggregations[type]) isnt -1
						result.getAggregationUrl = (type, value, without = false) => 
							aggregations = if without then @aggreagtionsWithout(type, value, result) else @aggreagtionsWith(type, value, result)
							return @getUrlPart 'search/', @query, @page, aggregations, result
						result.query = @query
						@$el.html template result
						Renderer.render @, renderOptions
						if @query
							for textElement in $('.search-view .content-box .result-text')
								@highlight @query, $(textElement)

			aggreagtionsWithout: (type, value, result) ->
				copy = {}
				keys = Object.keys(@aggregations)
				for key in keys
					copy[key] = []
					for v in @aggregations[key]
						if type is key and v is value
							continue
						copy[key].push v
				return copy

			aggreagtionsWith: (type, value, result) ->
				copy = {}
				unless @aggregations[type]
					@aggregations[type] = []
				keys = Object.keys(@aggregations)
				for key in keys
					copy[key] = []
					for v in @aggregations[key]
						copy[key].push v
					if type is key and $.inArray(value, copy[key]) is -1
						copy[key].push value
				return copy

			isInResult: (key, result) ->
				for aggregation in result.aggregations
					if aggregation.name is key
						return true
				return false

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

			getUrlPart: (base, query, page, aggregations, result) ->
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
				if aggregations and Object.keys(aggregations).length
					for key in Object.keys(aggregations)
						if result and !@isInResult(key, result)
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

			getLabel: (type, value) ->
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
					if value is 'UNIT'
						return 'Unit process'
					else if value is 'SYSTEM'
						return 'System process'
					else if value is 'UNKNOWN'
						return 'Unknown'
				return value

			getAggregationLabel: (type) ->
				if type is 'repositoryId'
					return 'Repository'
				if type is 'type'
					return 'Model type'
				if type is 'categoryType'
					return 'Category type'
				if type is 'categoryRefId'
					return 'Category'
				if type is 'processType'
					return 'Process type'
				if type is 'modellingApproach'
					return 'Modelling approach'

)