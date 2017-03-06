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
				{@repository, @categoryId} = options
				unless @categoryId
					@categoryId = 'null'
				group = @repository.get 'group'
				name = @repository.get 'name'
				url = "ws/public/browse/#{group}/#{name}/" + @categoryId
				@filter = new Filter
					container: '.table-browse > tbody'
					template: entriesTemplate
					filterId: 'filter'
					url: (page, filter) -> "#{url}?filter=#{filter}"
					callback: (type, result) =>
						unless @initialized
							@setPath result
						@sortEntries result
						result.repository = @repository.toJSON()
						result.baseUrl = "#{group}/#{name}"
						result.isRoot = (if @categoryId and @categoryId isnt 'null' then false else true)
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
				@$el.html template
					isRoot: (if @categoryId and @categoryId isnt 'null' then false else true)
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