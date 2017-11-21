define([
				'backbone'
				'cs!utils/DataQuality'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/Labels'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Toggle'
				'cs!views/repository/dataset/Comments'
				'cs!views/repository/dataset/DatasetPrepare'
				'cs!views/repository/dataset/DQLayer'
				'cs!views/repository/dataset/DQSystem'
				'cs!views/repository/dataset/Exchanges'
				'cs!views/repository/dataset/Flow'
				'cs!views/repository/dataset/ImpactMethod'
				'cs!views/repository/dataset/Location'
				'cs!views/repository/dataset/ProductSystem'
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
				'tablesorter'
			]

	(Backbone, DataQuality, Events, Format, Icons, Labels, Layers, LocalStorage, ModelTypes, Renderer, Toggle, Comments, DatasetPrepare, DQLayer, DQSystem, Exchanges, Flow, ImpactMethod, Location, ProductSystem, Router, currentUser, project, productSystem, impactMethod, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location, dqSystem) ->

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
				url = "ws/public/browse/#{urlPart}"
				if @commitId
					url += '?commitId=' + @commitId
				$.ajax
					type: 'GET'
					url: url 
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
				url = "ws/public/download/#{format}/prepare/#{urlPart}" 
				if @commitId
					url +='?commitId=' + @commitId
				return url

			getFileBaseUrl: () ->
				urlPart = @getUrlPart()
				url = "ws/public/repository/file/#{urlPart}"
				if @commitId
					url +='?commitId=' + @commitId
				return url 

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
				schemaId = target.attr 'data-scheme'
				DQLayer.open @repository.toJSON(), @commitId, schemaId, entry

			switchCommit: (event) ->
				repo = @repository.toJSON()
				type = @type
				refId = @refId
				commitId = $(Events.target(event)).val()
				Router.navigate "#{repo.group}/#{repo.name}/dataset/#{type}/#{refId}?commitId=#{commitId}"

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
				'click .maximize-content > a': 'maximizeContent'
				'change #commitId': 'switchCommit'
				'change #impact-category': (event) -> ImpactMethod.initCategory @createModelForImpactMethod() 
				'change #nw-set': (event) -> ImpactMethod.initNwSet @createModelForImpactMethod()
				'click [href=#process-graph]': (event) -> @doInitialize 'process-graph', () => ProductSystem.initGraph @dataset
				'click [href=#process-tree]': (event) -> @doInitialize 'process-tree', () => ProductSystem.initTree @repository, @dataset, @commitId

			initialize: (options) ->
				{@repository, @type, @refId, @commitId, @commentPath} = options

			doInitialize: (varName, method) ->
				if @[varName]
					return
				@[varName] = true
				method()

			render: (renderOptions) ->
				template = @getTemplate()
				group = @repository.get 'group'
				name = @repository.get 'name'
				@loadDataset (dataset) =>
					@dataset = dataset
					# might have not found for requested commit id, so next best commit is returned, need to update the @commitId value and backbone history url
					if @commitId isnt dataset.commitId
						Router.navigate "#{group}/#{name}/dataset/" + @type + "/" + @refId + "?commitId=#{dataset.commitId}", 
							trigger: false
							replace: true
					@commitId = dataset.commitId
					@loadCommitHistory (commits) =>
						@commits = commits
						DatasetPrepare.applyTo dataset
						@$el.html template 
							dataset: dataset
							exchangeMap: if dataset.type is 'Process' then Exchanges.map dataset.exchanges else null
							baseUrl: "#{group}/#{name}/dataset"
							formatDate: Format.dateTime
							formatScientific: Format.scientific
							getSpecificTypeLabel: @getSpecificTypeLabel
							getValue: (object, path) => return @getValue object, path
							getIcon: Icons.get
							getTypeAsEnum: (type) => return @getTypeAsEnum(type)
							getTypeLabel: (type) => return ModelTypes[type]
							getLabel: (path) => return Labels.get @getTypeAsEnum(@dataset.type), path
							getUncertaintyLabel: @getUncertaintyLabel
							getDQColor: DataQuality.getColor 
							noToStr: Format.number
							fileBaseUrl: @getFileBaseUrl()
							commits: @commits
							commitId: @commitId or commits?[0]?.id
							formatCommitDescription: Format.formatCommitDescription
							reviewMode: LocalStorage.getValue('reviewMode')
							isPublic: !currentUser.isLoggedIn()
						Renderer.render @, renderOptions
						Toggle.init @$el
						if dataset.type is 'Location' # and dataset.geometry
							Location.initMap @dataset
						if dataset.type is 'Flow'
							Flow.init @repository, @refId, @commitId
						if dataset.type is 'DQSystem'
							DQSystem.init @dataset
						if dataset.type is 'ImpactMethod'
							imModel = @createModelForImpactMethod()
							ImpactMethod.initCategory imModel
							ImpactMethod.initNwSet imModel
						@initTableSorting()
						@initComments true

			createModelForImpactMethod: () ->
				return {
					repository: @repository
					commitId: @commitId or @commits?[0]?.id
					impactCategory: @$('#impact-category option:selected').attr 'id'
					nwSet: @$('#nw-set option:selected').attr 'id'
					getUrlPart: (modelType, refId) => @getUrlPart modelType, refId
					getValue: (object, path) => return @getValue object, path
					getTypeAsEnum: (type) => return @getTypeAsEnum type
					initTableSorting: (table) => @initTableSorting table
					initComments: (container) => @initComments container
				}

			initTableSorting: (table) ->
				if table
					tables = [$(table)]
				else
					tables = @$('table:not(.no-head)')
				for table in tables
					options = {headers: {}}
					for th, index in $('thead > tr > th', table)
						if $(th).is(':empty') or $('a', th).length
							options.headers[index] = {sorter: false}
					$(table).tablesorter options

			initComments: (loadComments) ->
				Comments.init @$el, 
					repository: @repository, 
					type: @type, 
					refId: @refId, 
					commitId: @commitId
					commentPath: @commentPath
					loadComments: loadComments

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

			getTypeAsEnum: (type) ->
				asEnum = ''
				first = true
				for char, index in type 
					if !first and @isCapital(char) and !@isCapital(type[index + 1])
						asEnum += '_'
					first = false
					asEnum += char
				return asEnum.toUpperCase()

			isCapital: (char) ->
				asInt = char.charCodeAt(0)
				if asInt < 65 or asInt > 90
					return false
				return true

)