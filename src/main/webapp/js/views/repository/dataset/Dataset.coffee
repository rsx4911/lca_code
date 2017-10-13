define([
				'backbone'
				'open-layers'
				'cs!utils/DataQuality'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/Labels'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!views/repository/dataset/Comments'
				'cs!views/repository/dataset/DatasetPrepare'
				'cs!views/repository/dataset/DataQualityLayer'
				'cs!views/repository/dataset/Graph'
				'cs!views/repository/dataset/Tree'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/repository/dataset/project'
				'templates/views/repository/dataset/product-system'
				'templates/views/repository/dataset/impact-method'
				'templates/views/repository/dataset/parameter'
				'templates/views/repository/dataset/process'
				'templates/views/repository/dataset/flow'
				'templates/views/repository/dataset/social-indicator'
				'templates/views/repository/dataset/flow-property'
				'templates/views/repository/dataset/unit-group'
				'templates/views/repository/dataset/currency'
				'templates/views/repository/dataset/source'
				'templates/views/repository/dataset/actor'
				'templates/views/repository/dataset/location'
				'templates/views/repository/dataset/dq-system'
				'templates/views/repository/dataset/impact-factor-rows'
				'templates/views/repository/dataset/nw-factor-rows'
				'tablesorter'
			]

	(Backbone, OpenLayers, DataQuality, Events, Format, Icons, Labels, Layers, LocalStorage, ModelTypes, Renderer, Comments, DatasetPrepare, DataQualityLayer, Graph, Tree, Router, currentUser, project, productSystem, impactMethod, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location, dqSystem, impactFactorsTemplate, nwFactorsTemplate) ->

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
					when 'DQ_SYSTEM' then return dqSystem

			loadDataset: (callback) ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				$.ajax
					type: 'GET'
					url: "ws/public/browse/#{urlPart}/#{commitId}" 
					success: callback

			loadCommitHistory: (callback) ->
				unless currentUser.isLoggedIn()
					callback()
					return
				urlPart = @getUrlPart()
				$.ajax
					type: 'GET'
					url: "ws/history/#{urlPart}"
					success: callback

			getDownloadUrl: (format = 'json') ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				return "ws/public/download/#{format}/prepare/#{urlPart}/#{commitId}" 

			getFileBaseUrl: () ->
				urlPart = @getUrlPart()
				commitId = @commitId or 'null'
				return "ws/public/repository/file/#{urlPart}/#{commitId}" 

			getUrlPart: (type, refId) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				type = type or @type
				refId = refId or @refId
				return "#{group}/#{name}/#{type}/#{refId}"

			downloadData: (event) ->
				@$('iframe#download-frame').remove()
				target = $ Events.target event
				format = target.attr('data-format') or 'json'
				Layers.showProgressIndicator 'Collecting<br>data sets'
				$.ajax
					type: 'GET'
					url: @getDownloadUrl(format)
					success: (token) =>
						Layers.hideProgressIndicator()
						@$el.append '<iframe id="download-frame" class="hidden" border="0" height="0" width="0" src="ws/public/download/' + format + '/' + token + '"></iframe>'

			showDataQuality: (event) ->
				target = $ Events.target event
				entry = target.attr 'data-entry'
				schemaId = target.attr 'data-schema'
				DataQualityLayer.open @repository.toJSON(), @commitId, schemaId, entry

			switchCommit: (event) ->
				repo = @repository.toJSON()
				type = @type
				refId = @refId
				commitId = $(Events.target(event)).val()
				Router.navigate "#{repo.group}/#{repo.name}/dataset/#{type}/#{refId}/#{commitId}"

			initProcessGraph: (event) ->
				if @graphInitialized
					return
				@graphInitialized = true
				setTimeout () =>
					frameWindow = $('iframe')[0].contentWindow
					frameWindow.processes = Graph.getModel @dataset 
					frameWindow.modelIds = Object.keys(frameWindow.processes)
					frameWindow.render('2d', 15)
				, 100

			initProcessTree: (event) ->
				if @treeInitialized
					return
				@treeInitialized = true
				Tree.init @repository, @dataset, @commitId

			maximizeContent: (event) ->
				pane = @$('.tab-pane.active')
				pane.addClass 'modal-content'
				$('body').append '<div class="modal-backdrop in"></div>'
				$('.modal-backdrop').on 'click', (event) => @restoreContent event

			restoreContent: (event) ->
				pane = @$('.tab-pane.active')
				pane.css 'position', ''
				pane.css 'top', ''
				pane.css 'left', ''
				pane.removeClass 'modal-content'
				$('.modal-backdrop').remove()

			className: 'repository-dataset'

			events: 
				'click a:not([role]):not([target=_blank]):not([data-action])': (event) -> Events.followLink event
				'click [data-format]': 'downloadData'
				'click a[data-action=show-data-quality]': 'showDataQuality'
				'click [href=#process-graph]': 'initProcessGraph'
				'click [href=#process-tree]': 'initProcessTree'
				'click .maximize-content > a': 'maximizeContent'
				'change #commitId': 'switchCommit'
				'change #impact-category': (event) -> @loadImpactCategory () -> @$('#impact-factors').trigger('update')
				'change #nw-set': (event) -> @loadNwSet () -> @$('#nw-factors').trigger('update')

			initialize: (options) ->
				{@repository, @type, @refId, @commitId, @commentPath} = options

			render: (renderOptions) ->
				template = @getTemplate()
				group = @repository.get 'group'
				name = @repository.get 'name'
				@loadDataset (dataset) =>
					@dataset = dataset
					# might have not found for requested commit id, so next best commit is returned, need to update the @commitId value and backbone history url
					if @commitId isnt dataset.commitId
						Router.navigate "#{group}/#{name}/dataset/" + @type + "/" + @refId + "/#{dataset.commitId}", 
							trigger: false
							replace: true
					@commitId = dataset.commitId
					@loadCommitHistory (commits) =>
						DatasetPrepare.applyTo dataset
						@$el.html template 
							dataset: dataset
							baseUrl: "#{group}/#{name}/dataset"
							formatDate: Format.dateTime
							getSpecificTypeLabel: @getSpecificTypeLabel
							getValue: (object, path) => return @getValue object, path
							getIcon: Icons.get
							getTypeAsEnum: (type) => return @getTypeAsEnum(type)
							getTypeLabel: (type) => return ModelTypes[type]
							getLabel: (path) => return Labels.get @getTypeAsEnum(dataset.type), path
							getUncertaintyLabel: @getUncertaintyLabel
							getDQColor: DataQuality.getColor 
							noToStr: Format.number
							fileBaseUrl: @getFileBaseUrl()
							commits: commits
							commitId: @commitId or commits?[0]?.id
							formatCommitDescription: Format.formatCommitDescription
							reviewMode: LocalStorage.getValue('reviewMode')
							isPublic: !currentUser.isLoggedIn()
						Renderer.render @, renderOptions
						if dataset.type is 'Location' # and dataset.geometry
							@initMap dataset
						if dataset.type is 'ImpactMethod'
							@loadImpactCategory () =>
								@loadNwSet () =>
									@initTableSorting()
									Comments.init @$el, 
										repository: @repository, 
										type: @type, 
										refId: @refId, 
										commitId: @commitId
										commentPath: @commentPath
						else
							@initTableSorting()
							Comments.init @$el,
								repository: @repository, 
								type: @type, 
								refId: @refId, 
								commitId: @commitId
								commentPath: @commentPath
						if dataset.type is 'DQSystem'
							@initDataQualityPopups dataset

			initTableSorting: () ->
				tables = @$('table:not(.no-head)')
				for table in tables
					options = {headers: {}}
					for th, index in $('thead > tr > th', table)
						if $(th).is(':empty') or $('a', th).length
							options.headers[index] = {sorter: false}
					$(table).tablesorter options

			getSpecificTypeLabel: (type, value) ->
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

			getValue: (object, path) ->
				unless path
					return null
				unless object
					return null
				if path.indexOf('.') is -1 and path.indexOf('[') is -1
					return object[path]
				subpath = path
				if subpath.indexOf('.') isnt -1 
					subpath = path.substring 0, path.indexOf('.')
				arrayPos = null
				if subpath.indexOf('[') isnt -1
					arrayPos = subpath.substring(subpath.indexOf('[') + 1, subpath.indexOf(']'))
					subpath = subpath.substring 0, subpath.indexOf('[')
				object = object[subpath]
				if (arrayPos and (parseInt(arrayPos) is NaN or parseInt(arrayPos) > 0)) or parseInt(arrayPos) is 0
					object = object[arrayPos]
				if path.indexOf('.') is -1
					return object
				path = path.substring path.indexOf('.') + 1
				return @getValue object, path

			isCapital: (char) ->
				asInt = char.charCodeAt(0)
				if asInt < 65 or asInt > 90
					return false
				return true

			getTypeAsEnum: (type) ->
				asEnum = ''
				first = true
				for char, index in type 
					if !first and @isCapital(char) and !@isCapital(type[index + 1])
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
						url: "ws/public/browse/#{urlPart}/#{commitId}"
						success: (impactCategory) =>
							group = @repository.get 'group'
							name = @repository.get 'name'
							DatasetPrepare.applyTo impactCategory
							@$('#impact-category-description').html impactCategory.description
							@$('#impact-category-unit').html impactCategory.referenceUnitName
							@$('#impact-factors tbody').empty()
							@$('#impact-factors tbody').append impactFactorsTemplate 
								dataset: impactCategory
								noToStr: Format.number
								getValue: (object, path) => return @getValue object, path 
								getTypeAsEnum: (type) => return @getTypeAsEnum(type)
								getIcon: Icons.get
								commitId: @commitId or commits?[0]?.id
								baseUrl: "#{group}/#{name}/dataset"
							callback()
				else
					callback()

			loadNwSet: (callback) ->
				commitId = @commitId or 'null'
				selectedNwSet = $('#nw-set option:selected').attr 'id'
				if selectedNwSet
					urlPart = @getUrlPart 'NW_SET', selectedNwSet
					$.ajax
						type: 'GET'
						url: "ws/public/browse/#{urlPart}/#{commitId}"
						success: (nwSet) =>
							DatasetPrepare.applyTo nwSet
							@$('#nw-set-unit').html nwSet.weightedScoreUnit
							@$('#nw-factors tbody').empty()
							@$('#nw-factors tbody').append nwFactorsTemplate 
								nwSet: nwSet
								noToStr: Format.number
							callback()
				else
					callback()

			initDataQualityPopups: (dataset) ->
				@$('table.data-quality td').on 'click', (event) ->
					target = $ Events.target event
					span = $ 'span', target
					if span.css('display') is 'none'
						iIndex = target.attr('data-indicator') - 1
						sIndex = target.attr('data-score') - 1
						indicator = dataset.indicators[iIndex]
						score = indicator.scores[sIndex]
						iName = if indicator.name then indicator.name else indicator.position
						sName = if score.label then score.label else score.position
						Layers.showMessageInLayer
							title: "#{iName} - #{sName}"
							body: score.description

)