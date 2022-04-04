define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Renderer'
				'cs!utils/Toggle'
				'cs!views/repository/dataset/Comments'
				'cs!views/repository/dataset/DatasetPrepare'
				'cs!views/repository/dataset/DatasetRendering'
				'cs!views/repository/dataset/DQLayer'
				'cs!views/repository/dataset/DQSystem'
				'cs!views/repository/dataset/Exchanges'
				'cs!views/repository/dataset/Flow'
				'cs!views/repository/dataset/ImpactFactors'
				'cs!views/repository/dataset/Location'
				'cs!views/repository/dataset/ProductSystem'
				'cs!views/repository/Download'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'cs!models/Settings'
				'templates/views/repository/dataset/project'
				'templates/views/repository/dataset/product-system'
				'templates/views/repository/dataset/impact-method'
				'templates/views/repository/dataset/impact-category'
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
				'templates/views/repository/dataset/epd'
				'templates/views/repository/dataset/result'
				'tablesorter'
			]

	(Backbone, Events, Layers, LocalStorage, Renderer, Toggle, Comments, DatasetPrepare, DatasetRendering, DQLayer, DQSystem, Exchanges, Flow, ImpactFactors, Location, ProductSystem, Download, Router, currentUser, settings, project, productSystem, impactMethod, impactCategory, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location, dqSystem, epd, result) ->

		class RepositoryDataset extends Backbone.View

			getTemplate: () ->
				switch @type
					when 'PROJECT' then return project
					when 'PRODUCT_SYSTEM' then return productSystem
					when 'IMPACT_METHOD' then return impactMethod
					when 'IMPACT_CATEGORY' then return impactCategory
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
					when 'EPD' then return epd
					when 'RESULT' then return result

			loadDataset: (refId, commitId, callback) ->
				urlPart = @getUrlPart @type, refId
				url = "ws/public/browse/#{urlPart}"
				if commitId
					url += '?commitId=' + commitId
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

			updateIcon: (path, commentCount) ->
				if commentCount
					$("[data-path='#{path}'] img[data-action=comment]").attr 'src', 'images/comment_highlighted.png'
				else if LocalStorage.getValue('reviewMode')
					$("[data-path='#{path}'] img[data-action=comment]").attr 'src', 'images/comment.png'
				else
					$("[data-path='#{path}'] img[data-action=comment]").remove()

			showDataQuality: (event) ->
				Events.preventDefault event
				target = $ Events.target event
				entry = target.attr 'data-entry'
				schemaId = target.attr 'data-scheme'
				DQLayer.open @repository.toJSON(), @commitId, schemaId, entry, @dataset

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
				'click [data-format]': (event) -> 
					Events.preventDefault(event)
					Download.dataset(@repository.get('group'), @repository.get('name'), @type, @refId, @commitId, $(Events.target(event)).attr('data-format'))
				'click [data-compare-to]': 'initComparison'
				'click a[data-action=show-data-quality]': 'showDataQuality'
				'click .maximize-content > a': 'maximizeContent'
				'change #commitId': 'switchCommit'
				'click [href="#supply-chain"]': (event) -> @doInitialize 'process-tree', () => ProductSystem.initTree @repository, @dataset, @commitId
				'click [href="#graph"]': (event) -> @doInitialize 'process-graph', () => ProductSystem.initGraph @dataset
				'click .select-method': (event) -> ProductSystem.selectImpactMethod @repository, @dataset

			initialize: (options) ->
				{@repository, @type, @refId, @commitId, @commentPath, @compareToCommitId, @standalone} = options

			doInitialize: (varName, method) ->
				if @[varName]
					return
				@[varName] = true
				method()

			render: (renderOptions) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				if !currentUser.isLoggedIn() and @commitId
					@commitId = null
					Router.navigate "#{group}/#{name}/dataset/" + @type + "/" + @refId, 
						trigger: false
						replace: true					
				@loadDataset @refId, @commitId, (dataset) =>
					@dataset = dataset
					@loadCommitHistory (commits) =>
						@commits = commits
						if !@commitId && commits && commits.length
							@commitId = commits[0].id
						DatasetPrepare.applyTo @dataset
						@doRender renderOptions

			doRender: (renderOptions, comparisonCommitId) ->
				template = @getTemplate()
				group = @repository.get 'group'
				name = @repository.get 'name'
				exchangesField = null
				if @dataset.type is 'Process'
					exchangesField = 'exchanges'
				else if @dataset.type is 'ProductSystem'
					exchangesField = 'inventory'
				model =
					standalone: @standalone
					dataset: @dataset
					commits: @commits
					commitId: @commitId or @commits?[0]?.id
					compareTo: @compareTo
					comparisonCommitId: comparisonCommitId
					baseUrl: "#{group}/#{name}/dataset"
					fileBaseUrl: @getFileBaseUrl()
					exchangeMap: if exchangesField then Exchanges.map(@dataset[exchangesField], @type) else null
					otherExchangeMap: if @compareTo and exchangesField then Exchanges.map(@compareTo[exchangesField], @type) else null
					factorMap: if @dataset.type is 'ImpactCategory' then ImpactFactors.map(@dataset.impactFactors) else null
					otherFactorMap: if @compareTo and @compareTo.type is 'ImpactCategory' then ImpactFactors.map(@compareTo.impactFactors) else null
					reviewMode: LocalStorage.getValue('reviewMode')
					isPublic: !currentUser.isLoggedIn()
				$.extend model, DatasetRendering.getFunctions @dataset, @compareTo
				@$el.html template model
				if renderOptions
					Renderer.render @, renderOptions
				Toggle.init @$el
				@initDatasetSpecifics()
				@initTableSorting()
				if currentUser.isLoggedIn()
					if @compareToCommitId and !@compareTo
						@applyComparison @refId, @compareToCommitId
					if @compareTo
						@setComparisonStatistics()
					if !@compareTo and !@compareToCommitId
						@initComments true
				if @standalone # used for change log
					@removeLinks()

			removeLinks: () ->
				links = @$('a:not([data-toggle=tab]):not(.toggle-control)')
				links.removeAttr 'href'
				links.addClass 'no-link'
				links.on 'click', (event) -> Events.preventDefault event

			initDatasetSpecifics: () ->
				if @dataset.type is 'Location' # and dataset.geometry
					Location.initMap @dataset
				if @dataset.type is 'Flow'
					Flow.init @repository, @refId, @commitId, @dataset.flowType
				if @dataset.type is 'DQSystem'
					DQSystem.init @dataset

			initTableSorting: (table) ->
				if table
					tables = [$(table)]
				else
					tables = @$('table:not(.no-head):not(.no-sorting)')
				for table in tables
					options = {headers: {}}
					for th, index in $('thead > tr > th', table)
						if $(th).is(':empty') or $('a', th).length
							options.headers[index] = {sorter: false}
					$(table).tablesorter options

			initComments: (loadComments) ->
				unless settings.is('COMMENTS_ENABLED')
					return
				Comments.init @$el,
					repository: @repository
					type: @type
					refId: @refId
					commitId: @commitId
					commentPath: @commentPath
					loadComments: loadComments
					updateIcon: @updateIcon

			initComparison: (event) ->
				target = $ Events.target event
				type = target.attr 'data-compare-to'
				if type is 'previous'
					for commit, index in @commits
						if commit.id is @commitId
							commitId = @commits[index + 1].id
							break
					if commitId
						@applyComparison @refId, commitId
				else if type is 'other-version'
					Layers.selectCommit @commits, @commitId, (commitId) =>
						@applyComparison @refId, commitId
				else if type is 'other-dataset'
					repositoryPath = @repository.get('group') + '/' + @repository.get('name')
					Layers.selectModel
						repositoryPath: repositoryPath
						type: @type
						selectVersion: true
						callback: (refId, commitId) =>
							@applyComparison refId, commitId

			applyComparison: (refId, commitId) ->
				@loadDataset refId, commitId, (dataset) =>
					DatasetPrepare.applyTo dataset
					@compareTo = dataset
					@doRender null, commitId

			setComparisonStatistics: () ->
				addedCount = @$('.content-box [data-compare=added] .glyphicon-plus-sign').length
				@$('.comparison-statistics [data-compare=added] .count').html addedCount
				changedCount = @$('.content-box [data-compare=changed] .glyphicon-exclamation-sign').length
				@$('.comparison-statistics [data-compare=changed] .count').html changedCount
				removedCount = @$('.content-box [data-compare=removed] .glyphicon-minus-sign').length
				@$('.comparison-statistics [data-compare=removed] .count').html removedCount
				for pane in @$('.tab-pane')
					id = $(pane).attr 'id'
					addedCount = $('[data-compare=added] .glyphicon-plus-sign', pane).length
					changedCount = $('[data-compare=changed] .glyphicon-exclamation-sign', pane).length
					removedCount = $('[data-compare=removed] .glyphicon-minus-sign', pane).length
					count = addedCount + changedCount + removedCount
					if count
						@$("a[href='##{id}'] .change-count").html count
				for dropdown in @$('li.dropdown')
					count = 0
					for entry in $('.dropdown-menu li', dropdown)
						if $('.glyphicon-plus-sign, .glyphicon-minus-sign', entry).length
							count++
						else
							current = parseInt $('.change-count', entry).text()
							if current and !isNaN(current)
								count += current
								$(entry).attr 'data-compare', 'changed'
					if count
						$('.dropdown-toggle .change-count', dropdown).html count

)