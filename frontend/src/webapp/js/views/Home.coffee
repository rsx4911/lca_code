define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!views/repository/Download'
				'cs!models/Settings'
				'templates/views/home'
			]

	(Backbone, Events, Renderer, Router, Download, settings, template) ->

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
				while !target.hasClass('pinned-repository')
					target = target.parent()
				group = target.attr 'data-group'
				repo = target.attr 'data-repo'
				Router.navigate "#{group}/#{repo}"

			downloadRepo: (event) ->
				target = $ Events.target event
				while !target.hasClass('pinned-repository')
					target = target.parent()
				group = target.attr 'data-group'
				repo = target.attr 'data-repo'
				Download.repository group, repo

			updateCount: (repo) ->
				$.get "ws/public/repository/count/#{repo.group}/#{repo.name}", (count) =>
					$(".pinned-repository[data-group=#{repo.group}][data-repo=#{repo.name}] .dataset-count-container").html "#{count.datasets} #{if count.datasets is 1 then 'data set' else 'data sets' }"

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #search-form': 'search'
				'click [data-action=browse]': 'browseRepo'
				'click [data-action=download]': 'downloadRepo'

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/public/repository'
					success: (repositories) =>
						visible = []
						hiddenRepositories = (settings.getVal('REPOSITORIES_HIDDEN') || [])
						for repo in repositories
							if $.inArray(repo.group+'/'+repo.name, hiddenRepositories) is -1
								visible.push repo
						orderedRepositories = (settings.getVal('REPOSITORIES_ORDER') || [])
						visible.sort (r1, r2) ->
							i1 = orderedRepositories.indexOf(r1.group + '/' + r1.name)
							i2 = orderedRepositories.indexOf(r2.group + '/' + r2.name)
							return i1 - i2
						@$el.html template
							title: settings.getVal('HOME_TITLE')
							welcomeText: settings.getVal('HOME_TEXT')
							showSearch: settings.is('SEARCH_AVAILABLE')
							repositories: visible
						Renderer.render @, renderOptions
						setTimeout () =>
							for repo in repositories
								@updateCount repo
						, 10


)