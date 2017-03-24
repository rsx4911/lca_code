define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Icons'
				'cs!utils/Layers'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'templates/views/repository/dataset/datasets'
				'templates/views/repository/dataset/datasets-entries'
			]

	(Backbone, Moment, Events, Filter, Icons, Layers, ModelTypes, Renderer, template, entriesTemplate) ->

		class RepositoryDatasets extends Backbone.View

			className: 'repository-datasets'

			events: 
				'click a': (event) -> Events.followLink event
				'click [data-action=download]': (event) -> @downloadRepository event

			initialize: (options) ->
				{@repository, @categoryPath} = options
				unless @categoryId
					@categoryId = 'null'
				group = @repository.get 'group'
				name = @repository.get 'name'
				url = "ws/public/browse/#{group}/#{name}"
				if @categoryPath 
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
					url += "?categoryPath=#{type}#{rest}&"
				else
					url += '?'
				@filter = new Filter
					container: '.table-browse > tbody'
					template: entriesTemplate
					filterId: 'filter'
					url: (page, filter) -> "#{url}filter=#{filter}"
					callback: (type, result) =>
						unless @initialized
							@setPath result
						@sortEntries result
						result.repository = @repository.toJSON()
						result.baseUrl = "#{group}/#{name}"
						result.categoryPath = @categoryPath
						result.getRootLabel = (type) -> return ModelTypes[type]
						result.formatLastUpdate = (value) -> return moment(value).fromNow()
						@initialized = true
				
			downloadRepository: (event) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				@$('iframe').remove()
				Layers.showProgressIndicator 'Preparing'
				$.ajax
					type: 'GET'
					url: "ws/download/prepare/#{group}/#{name}/"
					success: (token) =>
						Layers.hideProgressIndicator()
						@$el.append '<iframe class="hidden" border="0" height="0" width="0" src="ws/download/' + token + '"></iframe>'						

			render: (renderOptions) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				@$el.html template
					baseUrl: "#{group}/#{name}/datasets"
					categoryPath: @categoryPath
					getRootLabel: (type) -> return ModelTypes[type]
				Renderer.render @, renderOptions
				@filter.init()

			sortEntries: (result) ->
				unless result.entries?.length
					return
				if typeof(result.entries[0]) isnt 'object'
					return
				result.entries.sort (a, b) ->
					n1 = a.name.toLowerCase();
					n2 = b.name.toLowerCase();
					if n1 > n2 
						return 1
					else if n1 < n2
						return -1
					return 0

			setPath: (result) ->
				path = ''
				if result.entries?.length
					if typeof(result.entries[0]) is 'object'
						path = result.entries[0].fullPath
						type = if result.entries[0].type is 'CATEGORY' then result.entries[0].categoryType else result.entries[0].type 
						path = ModelTypes[type] + '/' + path.substring(0, path.lastIndexOf('/'))
						path = path.replace(/\//g, ' / ')
				@$('.path').html path

)