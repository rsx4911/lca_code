define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/Layers'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!views/repository/Download'
				'cs!models/Settings'
				'cs!models/CurrentUser'
				'templates/views/repository/commit/commit'
				'templates/views/repository/commit/commit-references'
			]

	(Backbone, Events, Filter, Format, Icons, Layers, ModelTypes, Renderer, Status, Download, settings, currentUser, template, refTemplate) ->

		class RepositoryCommit extends Backbone.View

			className: 'repository-commit'

			events: 
				'click a[href]:not([target=_blank]):not(.standalone)': (event) -> Events.followLink event
				'click [data-action="push-to-glad"]': 'pushToGlad' 
				'click .download-changelog': (event) -> 
					Events.preventDefault(event)
					Download.changelog @repository.get('group'), @repository.get('name'), @commitId

			initialize: (options) ->
				{@repository, @commitId, @standalone} = options
				repo = @repository.toJSON()
				commitId = @commitId
				@filter = new Filter
					container: '.file-references'
					filterId: 'filter'
					noPaging: @standalone
					template: refTemplate
					beforeRender: (result) => 
						result.commitId = commitId
						result.standalone = @standalone
						result.baseUrl = "#{repo.group}/#{repo.name}/dataset"
						result.getIcon = Icons.get
						result.getTypeLabel = (type) -> return ModelTypes[type]
					afterRender: () =>
						if @standalone
							links = @$('a:not([data-type=changed])')
							links.removeAttr 'href'
							links.addClass 'no-link'
							links.on 'click', (event) -> Events.preventDefault event
					url: () => @getUrl()

			getUrl: () ->
				repo = @repository.toJSON()
				commitId = @commitId
				url = "ws/public/history/references/#{repo.group}/#{repo.name}/#{commitId}?"
				if @type
					url += 'categoryPath=' + @type + '&'
				return url

			render: (renderOptions) ->
				repo = @repository.toJSON()
				commitId = @commitId
				@loadCommit (commit) =>
					@$el.html template
						repository: repo
						commit: commit
						changeLogEnabled: settings.is('CHANGE_LOG_ENABLED')
						formatDate: Format.dateTime
						standalone: @standalone
						isLoggedIn: currentUser.isLoggedIn()
						isGladAvailable: settings.isGladConfigured() and currentUser.isDataManager()
					@filter.init (result) => @setModelFilters result.modelTypes
				Renderer.render @, renderOptions

			loadCommit: (callback) ->
				repo = @repository.toJSON()
				commitId = @commitId
				$.ajax
					type: 'GET'
					url: "ws/public/history/commit/#{repo.group}/#{repo.name}/#{commitId}"
					success: callback

			setModelFilters: (modelTypes) ->
				modelFilters = []
				selected = []
				for type in modelTypes
					if $.inArray(type, selected) is -1
						selected.push type
						modelFilters.push [type, ModelTypes[type]]
				modelFilters.sort (a, b) ->
					return ModelTypes.ordinal(a[0]) - ModelTypes.ordinal(b[0])
				select = @$ '#type'
				select.append '<option value="">All</option>'
				for filter in modelFilters
					select.append '<option value="' + filter[0] + '">' + filter[1] + '</option>'
				select.off 'change'
				select.on 'change', (event) =>
					target = $ Events.target event
					@type = target.val()
					@filter.page = 1
					@filter.load (result) =>
						@filter.append result

			pushToGlad: () ->
				repository = @repository.toJSON()
				repoPath = "#{repository.group}/#{repository.name}"
				commitId = @commitId
				Layers.selectModel
					repositoryPath: repoPath
					commitId: commitId
					multipleSelection: true
					type: 'PROCESS'
					callback: (selection) ->
						Layers.showProgressIndicator 'Pushing'
						$.ajax
							type: 'PUT'
							url: "ws/datamanager/glad/push/#{repoPath}/#{commitId}"
							contentType: 'application/json'
							data: JSON.stringify(selection)
							success: (response) ->
								Layers.closeActive()
								Status.success 'Selected data is now being uploaded to GLAD'
								Layers.hideProgressIndicator()

)