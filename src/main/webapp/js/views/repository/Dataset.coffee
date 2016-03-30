define([
				'backbone'
				'moment'
				'open-layers'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/Renderer'
				'cs!app/Router'
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

	(Backbone, Moment, OpenLayers, Events, Icons, Renderer, Router, project, productSystem, impactMethod, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location) ->

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
					url: @getDataSetUrl()
					success: callback

			loadCommitHistory: (callback) ->
				urlPart = @getUrlPart()
				$.ajax
					type: 'GET'
					url: "/ws/history/#{urlPart}"
					success: callback

			getDataSetUrl: () ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				return "/ws/fetch/data/#{urlPart}/#{commitId}" 

			getFileBaseUrl: () ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				return "/ws/fetch/file/#{urlPart}/#{commitId}" 

			getUrlPart: () ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				type = @type
				refId = @refId
				return "#{group}/#{name}/#{type}/#{refId}"

			showExchangeDetails: (event) ->
				target = $ Events.target event
				exchangeId = target.attr 'data-id'

			formatCommitDescription: (text) ->
				if text.length < 100
					return text
				space = -1
				while text.indexOf(' ', space + 1) < 100 and text.indexOf(' ', space + 1) isnt -1
					space = text.indexOf(' ', space + 1)
				if space is -1
					return text.substring(0, 100) + '...'
				return text.substring(0, space) + '...'

			className: 'repository-dataset'

			events: 
				'click a:not([role]):not([download]):not([target=_blank])': (event) -> Events.followLink event
				'click a[data-action=show-exchange-details]': (event) -> showExchangeDetails event
				'change #commitId': (event) -> 
					repo = @repository.toJSON()
					type = @type
					refId = @refId
					commitId = $(Events.target(event)).val()
					Router.navigate "/#{repo.group}/#{repo.name}/dataset/#{type}/#{refId}/#{commitId}"

			initialize: (options) ->
				{@repository, @type, @refId, @commitId} = options
				window.formatCommitDescription = @formatCommitDescription

			render: (renderOptions) ->
				template = @getTemplate()
				group = @repository.get 'group'
				name = @repository.get 'name'
				@loadDataset (dataset) =>
					@loadCommitHistory (commits) =>
						@removeAtSigns dataset
						@prepareDataset dataset
						@$el.html template 
							dataset: dataset
							baseUrl: "/#{group}/#{name}/dataset"
							formatDate: (value) -> return if !value then '' else moment(value).format('MM/DD/YYYY hh:mm:ss')
							getLabel: @getLabel
							getIcon: Icons.get
							getTypeAsEnum: @getTypeAsEnum
							getUncertaintyLabel: @getUncertaintyLabel
							downloadUrl: @getDataSetUrl()
							fileBaseUrl: @getFileBaseUrl()
							commits: commits
							commitId: @commitId or commits[0].id
							formatCommitDescription: @formatCommitDescription
						Renderer.render @, renderOptions
						if dataset.type is 'Location' # and dataset.geometry
							@initMap dataset

			removeAtSigns: (object) ->
				for key in Object.keys(object)
					if key.indexOf('@') is 0
						object[key.substring(1)] = object[key]
					else if typeof(object[key]) is 'object'
						@removeAtSigns object[key]

			prepareDataset: (dataset) ->
				switch @type
					when 'PROCESS'
						@sortExchanges dataset
						@prepareAllocationFactors dataset

			sortExchanges: (dataset) ->
				dataset.exchanges.sort (e1, e2) ->
					if e1.input and !e2.input
						return -1
					if !e1.input and e2.input
						return 1
					if e1.flow.flowType is 'PRODUCT_FLOW' and e2.flow.flowType isnt 'PRODUCT_FLOW'
						return -1
					if e1.flow.flowType isnt 'PRODUCT_FLOW' and e2.flow.flowType is 'PRODUCT_FLOW'
						return 1
					if e1.flow.flowType is 'WASTE_FLOW' and e2.flow.flowType isnt 'WASTE_FLOW'
						return -1
					if e1.flow.flowType isnt 'WASTE_FLOW' and e2.flow.flowType is 'WASTE_FLOW'
						return 1
					if e1.flow.name.toLowerCase() < e2.flow.name.toLowerCase()
						return -1
					if e1.flow.name.toLowerCase() > e2.flow.name.toLowerCase()
						return 1
					return 0

			prepareAllocationFactors: (dataset) ->
				nonCausalAllocationFactors = {}
				causalAllocationFactors = {}
				exchangeMap = {}
				flowMap = {}
				for e in dataset.exchanges
					exchangeMap[e.id] = e
					flowMap[e.flow.id] = e.flow
				if dataset.allocationFactors?.length
					for factor in dataset.allocationFactors
						if factor.allocationType is 'PHYSICAL_ALLOCATION' or factor.allocationType is 'ECONOMIC_ALLOCATION'
							f = nonCausalAllocationFactors[factor.product.id]
							unless f
								f = {product: flowMap[factor.product.id]}
								nonCausalAllocationFactors[factor.product.id] = f
							if factor.allocationType is 'PHYSICAL_ALLOCATION'
								f.physical = factor.value
							else if factor.allocationType is 'ECONOMIC_ALLOCATION'
								f.economic = factor.value
						else if factor.allocationType is 'CAUSAL_ALLOCATION'
							f = causalAllocationFactors[factor.exchange.id]
							unless f
								f = {exchange: exchangeMap[factor.exchange.id], products: []}
								causalAllocationFactors[factor.exchange.id] = f
							f.products.push {product: flowMap[factor.product.id], value: factor.value}
				dataset.nonCausalAllocationFactors = []
				dataset.causalAllocationFactors = []
				for key in Object.keys(nonCausalAllocationFactors)
					dataset.nonCausalAllocationFactors.push nonCausalAllocationFactors[key]
				for key in Object.keys(causalAllocationFactors)
					dataset.causalAllocationFactors.push causalAllocationFactors[key]
				delete dataset.allocationFactors 
				@sortAllocationFactors dataset

			sortAllocationFactors: (dataset) ->
				order = {}
				for exchange, i in dataset.exchanges
					order[exchange.id] = i
				dataset.nonCausalAllocationFactors.sort (f1, f2) ->
					if f1.product.name.toLowerCase() < f2.product.name.toLowerCase()
						return -1
					if f1.product.name.toLowerCase() > f2.product.name.toLowerCase()
						return 1
					return 0
				dataset.causalAllocationFactors.sort (f1, f2) ->
					return order[f1.exchange.id] - order[f2.exchange.id]
				for factor in dataset.causalAllocationFactors
					factor.products.sort (p1, p2) ->
						return order[p1.product.id] - order[p2.product.id]

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