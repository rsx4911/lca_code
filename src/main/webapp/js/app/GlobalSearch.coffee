define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!app/Router'
				'templates/views/global-search'
			]

	(Backbone, Events, Renderer, Router, template) ->

		class UserMenu extends Backbone.View

			onSearchKeyUp: (event) ->
				if Events.keyCode(event) isnt 13
					return
				route = Backbone.history.fragment
				input = $ Events.target event, 'input'
				query = input.val()
				newRoute = 'search'
				if query || route.indexOf('search/') is 0
					newRoute += '/'
					if query
						newRoute += "query=#{query}"
				if route.indexOf('search/') is 0
					for param in route.substring(7).split('&')
						key = param.split('=')[0]
						if key isnt 'query' && key isnt 'page'
							if newRoute isnt 'search/'
								newRoute += '&'
							newRoute += param
				if newRoute is 'search/'
					newRoute = 'search'
				Router.navigate newRoute

			events: 
				'click a[href]:not([target=_blank]):not(.login):not(.logout):not([data-action])': (event) -> Events.followLink event
				'keyup #global-search': (event) -> @onSearchKeyUp event
				'click .glyphicon-search': () -> Router.navigate 'search'

			render: (renderOptions) ->
				@$el.html template()				
				Renderer.render @, renderOptions

)