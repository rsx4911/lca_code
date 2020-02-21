define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/Settings'
				'templates/views/home'
			]

	(Backbone, Events, Renderer, Router, settings, template) ->

		class Home extends Backbone.View

			className: 'home'

			search: () ->
				query = @$('#search').val()
				url = 'search'
				if query
					url += "/query=#{query}"
				Router.navigate url

			browseRepo: (event) ->
				target = $ Events.target event
				repo = target.attr 'data-repo'
				Router.navigate repo

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #search-form': 'search'
				'click [data-repo]': 'browseRepo'

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/public/repository'
					success: (repositories) =>
						@$el.html template
							title: settings.getVal('HOME_TITLE')
							welcomeText: settings.getVal('HOME_TEXT')
							repositories: repositories
						Renderer.render @, renderOptions

)