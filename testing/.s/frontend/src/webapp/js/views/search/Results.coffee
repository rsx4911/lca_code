define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!views/repository/Download'
				'cs!views/search/Util'
				'cs!views/search/Aggregation'
				'cs!models/CurrentUser'
				'templates/views/search/results'
			]

	(Backbone, Events, Icons, Renderer, Router, Download, Util, AggregationView, currentUser, template) ->

		class SearchResultsView extends Backbone.View

			className: 'search-view'

			events: 
				'click a:not([href="#"])': (event) -> Events.followLink event
				'click [data-action=refresh]': () -> Backbone.history.loadUrl()

			aggregationOrder:
				group: 1
				type: 2
				mostRecent: 3
				flowType: 4
				processType: 5
				modellingApproach: 6
				location: 7
				reviewType: 8
				complianceDeclaration: 9
				flowCompleteness: 10
				categoryPaths: 11
				validFromYear: 12
				validUntilYear: 13
				contact: 14
				repositoryTags: 15
				tags: 16

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
				url = Util.getUrlPart 'ws/public/search?', @query, @page, @pageSize, @aggregations
				$.ajax
					type: 'GET'
					url: url
					success: (result) =>
						@correctUrl result
						@prepareAggregations result
						repoId = @aggregations.repositoryId or []
						result.typeFiltered = !!@aggregations['type']
						result.query = @query
						result.selectedAggregations = @aggregations
						result.isPublic = !currentUser.isLoggedIn()
						result.getLabel = Util.getLabel
						result.getIcon = Icons.get
						result.getPagingUrl = (page) => return Util.getUrlPart 'search/', @query, page, @pageSize, @aggregations, result.aggregations
						result.clearUrl = Util.getUrlPart 'search/', null, 1, 10
						result.getHighlightedVersionIndex = (dataset) =>
							index = dataset.versions.findIndex (version) => 
								if @query and @query isnt dataset.refId and version.name.toLowerCase().indexOf(@query.toLowerCase()) is -1
									return false
								return !!version.repos.find (repo) =>
									repoId.indexOf(repo.path) isnt -1
							if index isnt -1
								return index
							return dataset.versions.length - 1
						result.getHighlightedRepoIndex = (version) =>
							index = version.repos.findIndex (repo) =>
								repoId.indexOf(repo.path) isnt -1 
							if index isnt -1
								return index
							return version.repos.length - 1
						result.getAggregationUrl = (type, value, without = false) =>
							query = @query
							if type is 'query' and without
								query = ''	
							aggregations = if without then Util.aggregationsWithout(@aggregations, type, value) else Util.aggregationsWith(@aggregations, type, value)
							return Util.getUrlPart 'search/', query, 1, @pageSize, aggregations, result.aggregations
						@$el.html template result
						Renderer.render @, renderOptions
						@$('[data-format][data-datatype=dataset]').on 'click', (event) ->
							Events.preventDefault event
							target = $ Events.target event
							Download.dataset(target.attr('data-group'), target.attr('data-repo'), target.attr('data-type'), target.attr('data-ref-id'), target.attr('data-commit-id'), target.attr('data-format'))
						@$('[data-format][data-datatype=repository]').on 'click', (event) ->
							Events.preventDefault event
							target = $ Events.target event
							Download.repository(target.attr('data-group'), target.attr('data-repo'), target.attr('data-commit-id'), null, target.attr('data-format'))
						@$('#page-size').on 'change', (event) => Router.navigate Util.getUrlPart 'search/', @query, 1, $(Events.target(event)).val(), @aggregations, result.aggregations
						if @query
							phrases = @splitQuery @query
							for textElement in $('.search-view .content-box .result-text')
								@highlight phrases, $(textElement)
						@initAggregationViews result.aggregations

			initAggregationViews: (aggregations) ->
				for aggregation in aggregations
					if !aggregation.entries?.length
						continue
					view = new AggregationView
						aggregation: aggregation
						aggregations: aggregations
						selectedAggregations: @aggregations
						query: @query
						pageSize: @pageSize
					view.render 
						container: '.search-view .aggregations'
						noAnimation: true
						append: true

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
				Router.navigate Util.getUrlPart('search/', @query, @page, @pageSize, @aggregations),
					trigger: false
					replace: true

			splitQuery: (query) =>
				phrases = []
				inQuotes = false
				phrase = ''
				for c in query.replace('@', ' ')
					if c is '"'
						inQuotes = !inQuotes
					else if c is ' ' && !inQuotes
						phrases.push phrase
						phrase = ''
					else
						phrase += c
				if phrase
					phrases.push phrase
				return phrases

			highlight: (phrases, element) ->
				for phrase in phrases
					phrase = phrase.toLowerCase()
					text = element.html()
					replaced = ''
					next = text.toLowerCase().indexOf phrase
					while next isnt -1
						replaced += text.substring(0, next) + '<span class="highlight-result">' + text.substring(next, next + phrase.length) + '</span>'
						text = text.substring(next + phrase.length)
						next = text.toLowerCase().indexOf phrase, next
					replaced += text
					element.html replaced

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
					unless groupEntry
						continue
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