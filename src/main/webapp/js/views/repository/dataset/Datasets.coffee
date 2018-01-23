define([
				'backbone'
				'moment'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Icons'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/repository/datasets'
				'templates/views/repository/datasets-entries'
			]

	(Backbone, Moment, Router, Events, Filter, Icons, Layers, LocalStorage, ModelTypes, Renderer, currentUser, template, entriesTemplate) ->

		class RepositoryDatasets extends Backbone.View

			className: 'repository-datasets'

			events: 
				'click a': (event) -> Events.followLink event
				'change #show-deleted': (event) ->
					target = $ Events.target event
					LocalStorage.toggleValue 'datasets-showDeleted'
					@filter.applyFilter()
				'change #commit': (event) ->
					target = $ Events.target event
					commitId = target.val()
					group = @repository.get 'group'
					name = @repository.get 'name'
					path = "#{group}/#{name}/datasets/"
					if @categoryPath
						path += @categoryPath 
					path += "?commitId=#{commitId}"
					Router.navigate path

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
						url = "#{url}showDeleted=" + LocalStorage.getValue('datasets-showDeleted')
						if @commitId
							url += '&commitId=' + @commitId
						return url + '&'
					beforeRender: (result) =>
						result.repository = @repository.toJSON()
						result.baseUrl = "#{group}/#{name}"
						result.categoryPath = @categoryPath
						result.commitId = @commitId
						result.isPublic = !currentUser.isLoggedIn()
						result.getRootLabel = (t) -> return ModelTypes[t]
						result.formatLastUpdate = (value) -> return moment(value).fromNow()
						result.getIcon = Icons.get
						if result.entries?.length or @categoryPath
							@$('.no-content-message').hide()
							@$('.table-browse').show()
						else
							@$('.no-content-message').show()
							@$('.table-browse').hide()
						@initialized = true
				
			render: (renderOptions) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				url = "ws/public/browse/categoryInfo/#{group}/#{name}"
				if @categoryPath
					url += '?categoryPath=' + @getCategoryPath()
				if @commitId
					if @categoryPath
						url += '&'
					else
						url += '?'
					url += 'commitId=' + @commitId

				$.ajax
					type: 'GET'
					url: url
					success: (categoryInfo) =>
						if currentUser.isLoggedIn()
							historyUrl = "ws/history/"
							if categoryInfo.id
								historyUrl += "category/#{group}/#{name}/#{categoryInfo.id}"
							else
								historyUrl += "#{group}/#{name}"
							$.ajax
								type: 'GET'
								url: historyUrl
								success: (commits) => @doRender renderOptions, categoryInfo, commits
						else
							@doRender renderOptions, categoryInfo, []

			doRender: (renderOptions, categoryInfo, commits) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				@$el.html template
					baseUrl: "#{group}/#{name}/datasets"
					categoryPath: @categoryPath
					showDeleted: LocalStorage.getValue('datasets-showDeleted')
					deleted: (categoryInfo.deleted is 'true')
					isPublic: !currentUser.isLoggedIn()
					commits: commits
					commitId: @commitId
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
					rest = @categoryPath.substring slashIndex
				else
					type = @categoryPath
					rest = ''
				for key in Object.keys(ModelTypes)
					if ModelTypes[key] is type
						type = key
				return "#{type}#{rest}"

)