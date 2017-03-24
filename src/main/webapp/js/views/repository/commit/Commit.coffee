define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'templates/views/repository/commit/commit'
				'templates/views/repository/commit/commit-references'
			]

	(Backbone, Events, Filter, Format, Icons, ModelTypes, Renderer, template, refTemplate) ->

		class RepositoryCommit extends Backbone.View

			className: 'repository-commit'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event

			initialize: (options) ->
				{@repository, @commitId} = options
				repo = @repository.toJSON()
				commitId = @commitId
				@filter = new Filter
					container: '.file-references'
					template: refTemplate
					callback: (type, result) -> 
						result.commitId = commitId
						result.baseUrl = "#{repo.group}/#{repo.name}/dataset"
						result.getTypeLabel = (type) -> return ModelTypes[type]
					url: (page) -> "ws/history/references/#{repo.group}/#{repo.name}/#{commitId}?page=#{page}"

			render: (renderOptions) ->
				repo = @repository.toJSON()
				commitId = @commitId
				@loadCommit (commit) =>
					@$el.html template
						repository: repo
						commit: commit
						formatDate: Format.dateTime
						getIcon: Icons.get
					@filter.init()
				Renderer.render @, renderOptions

			loadCommit: (callback) ->
				repo = @repository.toJSON()
				commitId = @commitId
				$.ajax
					type: 'GET'
					url: "ws/history/commit/#{repo.group}/#{repo.name}/#{commitId}"
					success: callback

)