define([
				'backbone'
				'moment'
				'open-layers'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/Renderer'
				'cs!views/repository/util/DatasetPrepare'
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
				'templates/views/repository/model/impact-factor-rows'
				'templates/views/repository/model/nw-factor-rows'
				'tablesorter'
			]

	(Backbone, Moment, OpenLayers, Events, Icons, Renderer, DatasetPrepare, Router, project, productSystem, impactMethod, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location, impactFactorsTemplate, nwFactorsTemplate) ->

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
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				$.ajax
					type: 'GET'
					url: "/ws/browse/#{urlPart}/#{commitId}" 
					success: callback

			loadCommitHistory: (callback) ->
				urlPart = @getUrlPart()
				$.ajax
					type: 'GET'
					url: "/ws/history/#{urlPart}"
					success: callback

			getDownloadUrl: () ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				return "/ws/fetch/data/#{urlPart}/#{commitId}" 

			getFileBaseUrl: () ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				return "/ws/fetch/file/#{urlPart}/#{commitId}" 

			getUrlPart: (type, refId) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				type = type or @type
				refId = refId or @refId
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
				'change #impact-category': (event) -> @loadImpactCategory () -> @$('#impact-factors').trigger('update')
				'change #nw-set': (event) -> @loadNwSet () -> @$('#nw-factors').trigger('update')
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
						DatasetPrepare.applyTo dataset
						@$el.html template 
							dataset: dataset
							baseUrl: "/#{group}/#{name}/dataset"
							formatDate: (value) -> return if !value then '' else moment(value).format('MM/DD/YYYY hh:mm:ss')
							getLabel: @getLabel
							getIcon: Icons.get
							getTypeAsEnum: @getTypeAsEnum
							getUncertaintyLabel: @getUncertaintyLabel
							downloadUrl: @getDownloadUrl()
							fileBaseUrl: @getFileBaseUrl()
							commits: commits
							commitId: @commitId or commits[0].id
							formatCommitDescription: @formatCommitDescription
						Renderer.render @, renderOptions
						if dataset.type is 'Location' # and dataset.geometry
							@initMap dataset
						if dataset.type is 'ImpactMethod'
							@loadImpactCategory () =>
								@loadNwSet () =>
									@initTableSorting()
						else
							@initTableSorting()

			initTableSorting: () ->
				tables = @$('table:not(.no-head)')
				for table in tables
					options = {headers: {}}
					for th, index in $('thead > tr > th', table)
						if $(th).is(':empty') or $('a', th).length
							options.headers[index] = {sorter: false}
					$(table).tablesorter options

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

			loadImpactCategory: (callback) ->
				commitId = @commitId or 'null'
				selectedImpactCategory = $('#impact-category option:selected').attr 'id'
				if selectedImpactCategory
					urlPart = @getUrlPart 'IMPACT_CATEGORY', selectedImpactCategory
					$.ajax
						type: 'GET'
						url: "/ws/browse/#{urlPart}/#{commitId}"
						success: (impactCategory) =>
							DatasetPrepare.applyTo impactCategory
							@$('.impact-category-description').html impactCategory.description
							@$('#impact-factors tbody').empty()
							@$('#impact-factors tbody').append impactFactorsTemplate 
								impactCategory: impactCategory
							callback()

			loadNwSet: (callback) ->
				commitId = @commitId or 'null'
				selectedNwSet = $('#nw-set option:selected').attr 'id'
				if selectedNwSet
					urlPart = @getUrlPart 'NW_SET', selectedNwSet
					$.ajax
						type: 'GET'
						url: "/ws/browse/#{urlPart}/#{commitId}"
						success: (nwSet) =>
							DatasetPrepare.applyTo nwSet
							@$('#nw-set-unit').html nwSet.weightedScoreUnit
							@$('#nw-factors tbody').empty()
							@$('#nw-factors tbody').append nwFactorsTemplate 
								nwSet: nwSet
							callback()

)