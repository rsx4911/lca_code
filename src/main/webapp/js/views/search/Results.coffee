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

			doPage: (event) ->
				Events.preventDefault event
				target = $ Events.target event
				page = target.attr 'data-page'
				Router.navigate @getUrl @query, page, @type

			doFilterType: (event) ->
				target = $ Events.target event
				type = target.val()
				if type is 'All'
					type = null
				Router.navigate @getUrl @query, null, type

			getUrl: (query, page, type) ->
				url = 'search/'
				if query
					url += "query=#{query}"
				if page
					if query
						url += '&'
					url += "page=#{page}"
				if type
					if query or page
						url += '&'
					url += "type=#{type}"
				return url

			className: 'search-view'

			events: 
				'click .result a': (event) -> Events.followLink event
				'click a[data-page]': (event) -> @doPage event
				'change #type': (event) -> @doFilterType event

			initialize: (options) ->
				{@query, @page, @type} = options
				unless @query
					@query = ''
				unless @page
					@page = 1

			render: (renderOptions) ->
				url = 'ws/search?query=' + @query + '&page=' + @page
				if @type
					url += '&type=' + @type
				$.ajax
					type: 'GET'
					url: url
					success: (result) =>
						result.getTypeLabel = (type) -> return ModelTypes[type]
						@$el.html template result
						Renderer.render @, renderOptions
						if result.filter
							for textElement in $('.search-view .content-box .result-text')
								@highlight result.filter, $(textElement)

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

)