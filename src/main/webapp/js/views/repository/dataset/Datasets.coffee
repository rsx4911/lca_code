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
						@sortEntries result
						result.repository = @repository.toJSON()
						result.baseUrl = "#{group}/#{name}"
						result.categoryPath = @categoryPath
						result.getRootLabel = (type) -> return ModelTypes[type]
						result.formatLastUpdate = (value) -> return moment(value).fromNow()
						if result.entries?.length
							@$('.no-content-message').hide()
							@$('.table-browse').show()
						else
							@$('.no-content-message').show()
							@$('.table-browse').hide()
						@initialized = true
				
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

)