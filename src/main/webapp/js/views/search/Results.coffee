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
				{@query, @page} = options
				unless @query
					@query = ''
				console.log @page
				unless @page
					@page = 1

			render: (renderOptions) ->
				url = 'ws/search?query=' + @query + '&page=' + @page
				$.ajax
					type: 'GET'
					url: url
					success: (result) =>
						result.getTypeLabel = (type) -> return ModelTypes[type]
						result.getPagingUrl = (page) => return @getPagingUrl page 
						result.originalQuery.query = @query
						@$el.html template result
						Renderer.render @, renderOptions
						if @query
							for textElement in $('.search-view .content-box .result-text')
								@highlight @query, $(textElement)

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

			getPagingUrl: (page) ->
				url = 'search/'
				if @query
					url += 'query=' + @query + '&'
				url += "page=#{page}"
				return url

)