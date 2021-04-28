define([
				'backbone'
				'moment'
				'pace'
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

	(Backbone, moment, pace, Router, Events, Filter, Format, Icons, Layers, LocalStorage, ModelTypes, Renderer, Toggle, Download, currentUser, template, entriesTemplate) ->

		class RepositoryDatasets extends Backbone.View

			changeCommit: (event) ->
				target = $ Events.target event
				commitId = target.val()
				group = @repository.get 'group'
				name = @repository.get 'name'
				path = "#{group}/#{name}/datasets/"
				if @categoryPath
					path += @categoryPath 
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
					path: @getCategoryPath()
					callback: (selection) =>
						if !selection or !selection.length
							return
						Download.repository group, name, @commitId, @getCategoryPath(), format, selection

			download: (format, selection) ->
				@$('iframe#download-frame').remove()
				group = @repository.get 'group'
				name = @repository.get 'name'
				url = "ws/public/download/#{format}/prepare/#{group}/#{name}"
				if @commitId
					url += '?commitId=' + @commitId
				if @categoryPath
					url += if @commitId then '&' else '?'
					url += 'path=' + @getCategoryPath()
				Layers.showProgressIndicator 'Collecting<br>data sets'
				$.ajax
					type: if selection then 'POST' else 'GET'
					url: url
					contentType: if selection then 'application/json' else null
					data: if selection then JSON.stringify(selection) else null
					success: (token) =>
						Layers.hideProgressIndicator()
						@$el.append '<iframe id="download-frame" class="hidden" border="0" height="0" width="0" src="ws/public/download/' + format + '/' + token + '"></iframe>'
					error: () =>
						Layers.hideProgressIndicator()

			className: 'repository-datasets'

			events: 
				'click a:not([href="#"])': (event) -> Events.followLink event
				'click a[data-format]:not([data-action=select-data])': 'downloadData'
				'click a[data-action=select-data]': 'selectData'
				'change #commit': 'changeCommit'

			initialize: (options) ->
				{@repository, @categoryPath, @commitId} = options
				group = @repository.get 'group'
				name = @repository.get 'name'
				if !currentUser.isLoggedIn() and @commitId
					@commitId = null
					Router.navigate "#{group}/#{name}/datasets/" + @categoryPath, 
						trigger: false
						replace: true
				@filter = new Filter
					container: '.table-browse > tbody'
					template: entriesTemplate
					noPaging: true
					filterId: 'filter'
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
						if result.entries?.length or @categoryPath
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
					historyUrl = "ws/history/#{group}/#{name}"
					if @categoryPath && @categoryPath.indexOf('/') is -1
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
				@pathArray = []
				# TODO check category paths
				if @categoryPath and @categoryPath.indexOf('/') isnt -1
					@pathArray.push @categoryPath.substring 0, @categoryPath.indexOf('/')
				else if @categoryPath
					@pathArray.push @categoryPath
				@$el.html template
					baseUrl: "#{group}/#{name}/datasets"
					categoryPath: @categoryPath
					formatDate: Format.dateTime
					pathAsArray: @pathArray
					isPublic: !currentUser.isLoggedIn()
					commits: commits
					commitId: @commitId
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