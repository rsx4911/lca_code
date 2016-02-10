define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/Renderer'
				'templates/views/repository/datasets'
			]

	(Backbone, Moment, Events, Icons, Renderer, template) ->

		class RepositoryDatasets extends Backbone.View

			getRootLabel: (entry) ->
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

			loadEntries: (callback) ->
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
				@loadEntries (data) =>
					path = ''
					if data.entries?.length
						if typeof(data.entries[0]) is 'object'
							path = data.entries[0].fullPath
							type = if data.entries[0].type is 'CATEGORY' then data.entries[0].categoryType else data.entries[0].type 
							path = @getRootLabel(type) + '/' + path.substring(0, path.lastIndexOf('/'))
							path = path.replace(/\//g, ' / ')
							data.entries.sort (a, b) ->
								n1 = a.name.toLowerCase();
								n2 = b.name.toLowerCase();
								if n1 > n2 
									return 1
								else if n1 < n2
									return -1
								return 0
					@$el.html template
						repository: repository
						entries: data.entries
						parentRefId: data.parentRefId
						path: path
						baseUrl: "/repository/datasets/#{repository.group}/#{repository.name}"
						datasetUrl: "/repository/dataset/#{repository.group}/#{repository.name}"
						isRoot: (if @categoryId then false else true)
						formatLastUpdate: (value) -> return moment(value).fromNow()
						getRootLabel: @getRootLabel
						getIcon: @getIcon
				Renderer.render @, renderOptions

)