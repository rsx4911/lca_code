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

	(Backbone, Events, Layers, LocalStorage, Renderer, Toggle, Comments, DatasetPrepare, DatasetRendering, DQLayer, DQSystem, Exchanges, Flow, Location, ProductSystem, Router, currentUser, project, productSystem, impactMethod, parameter, process, flow, socialIndicator, flowProperty, unitGroup, currency, source, actor, location, dqSystem) ->

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
				DQLayer.open @repository.toJSON(), @commitId, schemaId, entry, (object, path) => return @getValue object, path

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
				'click [data-compare-to]': 'initComparison'
				'click a[data-action=show-data-quality]': 'showDataQuality'
				'click .maximize-content > a': 'maximizeContent'
				'change #commitId': 'switchCommit'
				'click [href=#supply-chain]': (event) -> @doInitialize 'process-tree', () => ProductSystem.initTree @repository, @dataset, @commitId
				'click [href=#graph]': (event) -> @doInitialize 'process-graph', () => ProductSystem.initGraph @dataset

			initialize: (options) ->
				{@repository, @type, @refId, @commitId, @commentPath} = options

			doInitialize: (varName, method) ->
				if @[varName]
					return
				@[varName] = true
				method()

			render: (renderOptions) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				@loadDataset @refId, @commitId, (dataset) =>
					@dataset = dataset
					# might have not found for requested commit id, so next best commit is returned, need to update the @commitId value and backbone history url
					if @commitId isnt dataset.commitId
						Router.navigate "#{group}/#{name}/dataset/" + @type + "/" + @refId + "?commitId=#{dataset.commitId}", 
							trigger: false
							replace: true
					@commitId = dataset.commitId
					@loadCommitHistory (commits) =>
						@commits = commits
						DatasetPrepare.applyTo @dataset
						@doRender renderOptions

			doRender: (renderOptions, comparisonCommitId) ->
				template = @getTemplate()
				group = @repository.get 'group'
				name = @repository.get 'name'
				model =
					dataset: @dataset
					commits: @commits
					commitId: @commitId or commits?[0]?.id
					compareTo: @compareTo
					comparisonCommitId: comparisonCommitId
					baseUrl: "#{group}/#{name}/dataset"
					fileBaseUrl: @getFileBaseUrl()
					exchangeMap: if @dataset.type is 'Process' then Exchanges.map @dataset.exchanges else null
					otherExchangeMap: if @compareTo?.type is 'Process' then Exchanges.map @compareTo.exchanges else null
					reviewMode: LocalStorage.getValue('reviewMode')
					isPublic: !currentUser.isLoggedIn()
				$.extend model, DatasetRendering.getFunctions @dataset, @compareTo
				@$el.html template model
				if renderOptions
					Renderer.render @, renderOptions
				Toggle.init @$el
				@initDatasetSpecifics()
				@initTableSorting()
				unless @compareTo
					@initComments true

			initDatasetSpecifics: () ->
				if @dataset.type is 'Location' # and dataset.geometry
					Location.initMap @dataset
				if @dataset.type is 'Flow'
					Flow.init @repository, @refId, @commitId
				if @dataset.type is 'DQSystem'
					DQSystem.init @dataset

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
					repository: @repository
					type: @type
					refId: @refId
					commitId: @commitId
					commentPath: @commentPath
					loadComments: loadComments
					updateIcon: @updateIcon

			initComparison: (event) ->
				target = $ Events.target event
				commitId = target.attr 'data-compare-to'
				if !commitId or commitId is 'previous' or commitId is 'next'
						for commit, index in @commits
							if commitId is 'previous'
								if commit.id is @commitId
									commitId = @commits[index + 1].id
									break
							else if commitId is 'next'
								if commit.id is @commitId
									break
								commitId = commit.id
				if !commitId and !@commitId
					commitId = @commits[@commits.length - 1].id
				if !commitId or commitId is '0'
					return
				@loadDataset @refId, commitId, (dataset) =>
					DatasetPrepare.applyTo dataset
					@compareTo = dataset
					@doRender null, commitId
					@setComparisonStatistics()

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
						@$("a[href=##{id}] .change-count").html count
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