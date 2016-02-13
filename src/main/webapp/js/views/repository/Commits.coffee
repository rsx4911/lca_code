define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'templates/views/repository/commits'
			]

	(Backbone, moment, Events, Renderer, template) ->

		class RepositoryCommits extends Backbone.View

			className: 'repository-commits'

			events: 
				'click a': (event) -> Events.followLink event

			initialize: (options) ->
				{@repository} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				@loadCommits (commits) =>
					@$el.html template
						repository: repository
						groups: @prepareModel commits
						formatDate: (value) -> return if !value then '' else moment(value).format('MM/DD/YYYY')
				Renderer.render @, renderOptions

			loadCommits: (callback) ->
				repo = @repository.toJSON()
				$.ajax
					type: 'GET'
					url: "/ws/history/#{repo.group}/#{repo.name}/null"
					success: callback

			prepareModel: (commits) ->
				# id, message, timestamp, user
				groups = []
				previous = null
				group = null
				if commits
					for commit in commits
						if !@isSameDay(previous, commit.timestamp)
							group = {commits: []}
							groups.push group
						group.date = new Date(commit.timestamp)
						group.commits.push commit
						previous = commit.timestamp
				return groups			

			isSameDay: (t1, t2) ->
				d1 = moment(t1)
				d2 = moment(t2)
				if d1.year() isnt d2.year()
					return false
				if d1.dayOfYear() isnt d2.dayOfYear()
					return false
				return true

)