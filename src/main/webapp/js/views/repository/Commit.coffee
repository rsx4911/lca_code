define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Icons'
				'cs!utils/Renderer'
				'templates/views/repository/commit'
			]

	(Backbone, moment, Events, Icons, Renderer, template) ->

		class RepositoryCommit extends Backbone.View

			className: 'repository-commit'

			events: 
				'click a': (event) -> Events.followLink event

			initialize: (options) ->
				{@repository, @commitId} = options

			render: (renderOptions) ->
				repo = @repository.toJSON()
				@loadCommit (commit) =>
					@$el.html template
						repository: repo
						commit: commit
						formatDate: (value) -> return if !value then '' else moment(value).format('M/D/YYYY h:mm:ssa')
						getIcon: Icons.get
						baseUrl: "/repository/dataset/#{repo.group}/#{repo.name}"
				Renderer.render @, renderOptions

			loadCommit: (callback) ->
				repo = @repository.toJSON()
				commitId = @commitId
				$.ajax
					type: 'GET'
					url: "/ws/history/commit/#{repo.group}/#{repo.name}/#{commitId}"
					success: callback

)