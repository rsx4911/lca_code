define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!views/repository/Download'
				'templates/views/repository/commit/commit'
				'templates/views/repository/commit/commit-references'
			]

	(Backbone, Events, Filter, Format, Icons, ModelTypes, Renderer, Download, template, refTemplate) ->

		class RepositoryCommit extends Backbone.View

			className: 'repository-commit'

			events: 
				'click a[href]:not([target=_blank]):not(.standalone)': (event) -> Events.followLink event
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
						result.getTypeLabel = (type) -> return ModelTypes[type]
					afterRender: () =>
						if @standalone
							links = @$('a:not([data-type=changed])')
							links.removeAttr 'href'
							links.addClass 'no-link'
							links.on 'click', (event) -> Events.preventDefault event
							modelLinks = @$('a[data-type=changed]').attr 'href', 
					url: () => @getUrl()

			getUrl: () ->
				repo = @repository.toJSON()
				commitId = @commitId
				url = "ws/history/references/#{repo.group}/#{repo.name}/#{commitId}?"
				if @type
					url += 'type=' + @type + '&'
				return url

			render: (renderOptions) ->
				repo = @repository.toJSON()
				commitId = @commitId
				@loadCommit (commit) =>
					@$el.html template
						repository: repo
						commit: commit
						formatDate: Format.dateTime
						getIcon: Icons.get
						standalone: @standalone
					@filter.init (result) => @setModelFilters result.aggregations
				Renderer.render @, renderOptions

			loadCommit: (callback) ->
				repo = @repository.toJSON()
				commitId = @commitId
				$.ajax
					type: 'GET'
					url: "ws/history/commit/#{repo.group}/#{repo.name}/#{commitId}"
					success: callback

			setModelFilters: (aggregations) ->
				modelFilters = []
				for aggregation in aggregations
					if aggregation.name is 'type'
						for entry in aggregation.entries
							modelFilters.push [entry.key, ModelTypes[entry.key]]
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

)