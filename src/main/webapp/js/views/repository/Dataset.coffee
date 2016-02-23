define([
				'backbone'
				'moment'
				'open-layers'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/Renderer'
				'templates/views/repository/model/project'
				'templates/views/repository/model/product-system'
				'templates/views/repository/model/impact-method'
				'templates/views/repository/model/parameter'
				'templates/views/repository/model/process'
				'templates/views/repository/model/flow'
				'templates/views/repository/model/social-indicator'
				'templates/views/repository/model/flow-property'
				'templates/views/repository/model/unit-group'
				'templates/views/repository/model/currency'
				'templates/views/repository/model/source'
				'templates/views/repository/model/actor'
				'templates/views/repository/model/location'
			]

	(Backbone, Moment, OpenLayers, Events, Icons, Renderer, project, productSystem, impactMethod, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location) ->

		class RepositoryDataset extends Backbone.View

			getTemplate: () ->
				switch @type
					when 'PROJECT' then return project
					when 'PRODUCT_SYSTEM' then return productSystem
					when 'IMPACT_METHOD' then return impactMethod
					when 'PARAMETER' then return parameter
					when 'PROCESS' then return process
					when 'FLOW' then return flow
					when 'SOCIAL_INDICATOR' then return socialIndicator
					when 'FLOW_PROPERTY' then return flowProperty
					when 'UNIT_GROUP' then return unitGroup
					when 'CURRENCY' then return currency
					when 'SOURCE' then return source
					when 'ACTOR' then return actor
					when 'LOCATION' then return location

			loadDataset: (callback) ->
				$.ajax
					type: 'GET'
					url: @getUrl()
					success: callback

			getUrl: () ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				commitId = @commitId or 'null'
				type = @type
				refId = @refId
				return "/ws/fetch/data/#{group}/#{name}/#{type}/#{refId}/#{commitId}"

			className: 'repository-dataset'

			events: 
				'click a:not([role]):not([download])': (event) -> Events.followLink event

			initialize: (options) ->
				{@repository, @type, @refId} = options

			render: (renderOptions) ->
				template = @getTemplate()
				group = @repository.get 'group'
				name = @repository.get 'name'
				@loadDataset (dataset) =>
					@removeAtSigns dataset
					@$el.html template 
						dataset: dataset
						baseUrl: "/#{group}/#{name}/dataset"
						formatDate: (value) -> return if !value then '' else moment(value).format('MM/DD/YYYY hh:mm:ss')
						getLabel: @getLabel
						getIcon: Icons.get
						getTypeAsEnum: @getTypeAsEnum
						getUncertaintyLabel: @getUncertaintyLabel
						downloadUrl: @getUrl()
					Renderer.render @, renderOptions
					if dataset.type is 'Location' # and dataset.geometry
						@initMap dataset				

			removeAtSigns: (object) ->
				for key in Object.keys(object)
					if key.indexOf('@') is 0
						object[key.substring(1)] = object[key]
					else if typeof(object[key]) is 'object'
						@removeAtSigns object[key]

			getLabel: (type, value) ->
				switch type 
					when 'FlowPropertyType'
						switch value
							when 'ECONOMIC_QUANTITY' then return 'Economic flow property'
							when 'PHYSICAL_QUANTITY' then return 'Physical flow property'
					when 'FlowType'
						switch value
							when 'ELEMENTARY_FLOW' then return 'Elementary flow'
							when 'PRODUCT_FLOW' then return 'Product flow'
							when 'WASTE_FLOW' then return 'Waste flow'
					when 'ProcessType'
						switch value
							when 'UNIT_PROCESS' then return 'Unit process'
							when 'LCI_RESULT' then return 'System process'
				return ''

			getTypeAsEnum: (type) ->
				asEnum = ''
				first = true
				for char in type 
					asInt = char.charCodeAt(0)
					if !first and asInt >= 65 and asInt <= 90
						asEnum += '_'
					first = false
					asEnum += char
				return asEnum.toUpperCase()

			initMap: (dataset) ->
				map = new OpenLayers.Map
					layers: [
						new OpenLayers.layer.Tile
							source: new OpenLayers.source.OSM()
					]
					target: 'map'
					view: new OpenLayers.View
						center: OpenLayers.proj.transform [dataset.longitude or 0, dataset.latitude or 0], 'EPSG:4326', 'EPSG:3857'
						zoom: 5

)