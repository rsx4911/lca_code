define([
				'backbone'
				'moment'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Toggle'
				'cs!views/repository/Download'
				'cs!models/CurrentUser'
				'templates/views/repository/datasets'
				'templates/views/repository/datasets-entries'
			]

	(Backbone, moment, Router, Events, Filter, Format, Icons, Layers, LocalStorage, ModelTypes, Renderer, Toggle, Download, currentUser, template, entriesTemplate) ->

		class RepositoryDatasets extends Backbone.View

			changeCommit: (event) ->
				target = $ Events.target event
				commitId = target.val()
				group = @repository.get 'group'
				name = @repository.get 'name'
				path = "#{group}/#{name}/datasets"
				if @categoryPath
					path += "/#{@categoryPath}"
				if commitId
					path += "?commitId=#{commitId}"
				Router.navigate path

			downloadData: (event) ->
				Events.preventDefault event
				target = $ Events.target event
				format = target.attr('data-format') or 'json'
				group = @repository.get 'group'
				name = @repository.get 'name'
				Download.repository group, name, @commitId, @getCategoryPath(), format

			selectData: (event) ->
				Events.preventDefault event
				target = $ Events.target event
				format = target.attr('data-format') or 'json'
				group = @repository.get 'group'
				name = @repository.get 'name'
				Layers.selectModel
					repositoryPath: "#{group}/#{name}"
					multipleSelection: true
					commitId: @commitId
					path: @getCategoryPath()
					callback: (selection) =>
						if !selection or !selection.length
							return
						Download.repository group, name, @commitId, @getCategoryPath(), format, selection

			className: 'repository-datasets'

			events: 
				'click a:not([href="#"]):not([target=_blank])': (event) -> Events.followLink event
				'click a[data-format]:not([data-action=select-data])': 'downloadData'
				'click a[data-action=select-data]': 'selectData'
				'change #commit': 'changeCommit'

			initialize: (options) ->
				{@repository, @categoryPath, @commitId} = options
				group = @repository.get 'group'
				name = @repository.get 'name'
				@filter = new Filter
					container: '.table-browse > tbody'
					template: entriesTemplate
					filterId: 'filter'
					pageSize: 100
					url: () =>
						url = "ws/public/browse/#{group}/#{name}?"
						if @categoryPath
							url += 'categoryPath=' + @getCategoryPath() + '&'
						if @commitId
							url += 'commitId=' + @commitId + '&'
						return url
					beforeRender: (result) =>
						result.repository = @repository.toJSON()
						result.baseUrl = "#{group}/#{name}"
						result.categoryPath = @categoryPath
						result.commitId = @commitId
						result.isPublic = !currentUser.isLoggedIn()
						result.getRootLabel = (t) -> return ModelTypes[t]
						result.getModelType = (t) -> 				
							for key in Object.keys(ModelTypes)
								if ModelTypes[key] is t
									return key
							return null
						result.formatLastUpdate = (value) -> return moment(value).fromNow()
						result.getIcon = Icons.get
						if result.data?.length or @categoryPath
							@$('.no-content-message').hide()
							@$('.table-browse').show()
						else
							@$('.no-content-message').show()
							@$('.table-browse').hide()
						@initialized = true
					afterRender: () => Toggle.init @$el

			render: (renderOptions) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				if currentUser.isLoggedIn()
					historyUrl = "ws/public/history/#{group}/#{name}"
					if @categoryPath
						historyUrl += "?path=#{@getCategoryPath()}"
					$.ajax
						type: 'GET'
						url: historyUrl
						success: (commits) => @doRender renderOptions, commits
				else
					@doRender renderOptions, []

			doRender: (renderOptions, commits) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				info = @repository.get 'settings'
				releases = @repository.get 'releases'
				if releases?.length
					info = if @commitId then releases.find (r) => r.id is @commitId else releases[0]
				@$el.html template
					repoUrl: "#{group}/#{name}"
					categoryPath: @categoryPath
					formatDate: Format.dateTime
					formatCommitDescription: Format.commitDescription
					isPublic: !currentUser.isLoggedIn()
					commits: commits
					releases: releases
					commitId: @commitId
					info: info
					settings: @repository.get('settings')
					getRootLabel: (type) -> return ModelTypes[type]
					getIcon: Icons.get
				Renderer.render @, renderOptions
				@filter.init()

			getCategoryPath: () ->
				unless @categoryPath 
					return ''
				slashIndex = @categoryPath.indexOf('/')
				if slashIndex isnt -1
					type = @categoryPath.substring 0, slashIndex
					rest = encodeURIComponent @categoryPath.substring slashIndex
				else
					type = @categoryPath
					rest = ''
				for key in Object.keys(ModelTypes)
					if ModelTypes[key] is type
						type = key
				return "#{type}#{rest}"

)