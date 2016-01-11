define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'templates/views/repository/datasets'
			]

	(Backbone, Moment, Events, Renderer, template) ->

		class RepositoryDatasets extends Backbone.View

			getRootLabel = (entry) ->
				switch entry
					when 'PROJECT' then return 'Projects'
					when 'PRODUCT_SYSTEM' then return 'Product systems'
					when 'IMPACT_METHOD' then return 'Impact methods'
					when 'PROCESS' then return 'Processes'
					when 'FLOW' then return 'Flows'
					when 'FLOW_PROPERTY' then return 'Flow properties'
					when 'UNIT_GROUP' then return 'Unit groups'
					when 'ACTOR' then return 'Actors'
					when 'SOURCE' then return 'Sources'
					when 'LOCATION' then return 'Locations'
					when 'SOCIAL_INDICATOR' then return 'Social indicators'
					when 'CURRENCY' then return 'Currencies'
					when 'PARAMETER' then return 'Parameters'

			loadEntries = (callback) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				url = "/ws/browse/#{group}/#{name}"
				if @categoryId
					url += '/' + @categoryId
				$.ajax
					type: 'GET'
					url: url
					success: callback

			className: 'repository-datasets'

			events: 
				'click a': (event) -> Events.followLink event

			initialize: (options) ->
				{@repository, @categoryId} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				(@_ loadEntries) (entries) =>
					path = ''
					if entries?.length
						if typeof(entries[0]) is 'object'
							path = entries[0].fullPath
							type = if entries[0].type is 'CATEGORY' then entries[0].categoryType else entries[0].type 
							path = getRootLabel(type) + '/' + path.substring 0, path.lastIndexOf('/')
							path = path.replace(/\//g, ' / ')
					@$el.html template
						repository: repository
						entries: entries
						path: path
						baseUrl: "/repository/datasets/#{repository.group}/#{repository.name}"
						datasetBaseUrl: "/repository/dataset/#{repository.group}/#{repository.name}"
						isRoot: (if @categoryId then false else true)
						formatLastUpdate: (value) -> return moment(value).fromNow()
						getRootLabel: getRootLabel
				Renderer.render @, renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)