define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Toggle'
				'cs!app/Router'
				'cs!views/repository/Download'
				'cs!models/Settings'
				'templates/views/home'
			]

	(Backbone, Events, ModelTypes, Renderer, Toggle, Router, Download, settings, template) ->

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

			getMetaData: (repoId, callback) ->
				if @counts[repoId]
					callback @counts[repoId]
				else
					$.get "ws/public/repository/meta/#{repoId}", (count) =>
						@counts[repoId] = count
						callback count

			updateCount: (repo) ->
				@getMetaData "#{repo.group}/#{repo.name}", (metaData) =>
					total = 0
					tooltip = ''
					for type in Object.keys(metaData.counts)
						c = metaData.counts[type]
						total += c
						if c > 0
							if tooltip
								tooltip += '\n'
							tooltip += "#{c} #{if metaData.counts[type] is 1 then ModelTypes.singular(type) else ModelTypes.plural(type)}"
					countContainer = $(".pinned-repository[data-group=#{repo.group}][data-repo=#{repo.name}] .dataset-count-container")
					countContainer.attr 'title', tooltip
					if metaData.mainModelType
						count = metaData.counts[metaData.mainModelType]
						countContainer.html "#{count} #{if count is 1 then ModelTypes.singular(metaData.mainModelType) else ModelTypes.plural(metaData.mainModelType)} "
					else
						countContainer.html "#{total} #{if total is 1 then 'data set' else 'data sets' } "

			applyFilter: () ->
				@typeOfData = $('#typeOfData').val()
				@sortBy = $('#sortBy').val()
				@doRender()
				@delegateEvents()

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #search-form': 'search'
				'change #typeOfData, #sortBy': 'applyFilter'
				'click [data-action=browse]': 'browseRepo'
				'click [data-action=download]': 'downloadRepo'

			render: (renderOptions) ->
				@counts = {}
				@renderOptions = renderOptions
				$.ajax
					type: 'GET'
					url: 'ws/public/repository'
					success: (repositories) =>
						@visible = []
						hiddenRepositories = (settings.getVal('REPOSITORIES_HIDDEN') || [])
						for repo in repositories
							if $.inArray(repo.group+'/'+repo.name, hiddenRepositories) is -1
								@visible.push repo
						@doRender() 

			doRender: () ->
				orderedRepositories = (settings.getVal('REPOSITORIES_ORDER') || [])
				@visible.sort (r1, r2) =>
					if @sortBy is 'Release date'
						i1 = r2.settings.releaseDate
						i2 = r1.settings.releaseDate
					else
						i1 = orderedRepositories.indexOf(r1.group + '/' + r1.name)
						i2 = orderedRepositories.indexOf(r2.group + '/' + r2.name)
					return i1 - i2

				@$el.html template
					title: settings.getVal('HOME_TITLE')
					welcomeText: settings.getVal('HOME_TEXT')
					showSearch: settings.is('SEARCH_AVAILABLE')
					typeOfData: @typeOfData
					getModelTypeLabel: ModelTypes.plural
					sortBy: @sortBy
					repositories: @visible
				Renderer.render @, @renderOptions
				Toggle.init @$el
				setTimeout () =>
					for repo in @visible
						@updateCount repo
				, 10



)