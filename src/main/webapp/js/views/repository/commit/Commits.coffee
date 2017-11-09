define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Renderer'
				'templates/views/repository/commit/commits'
				'templates/views/repository/commit/commit-list'
			]

	(Backbone, moment, Events, Filter, Format, Renderer, template, listTemplate) ->

		class RepositoryCommits extends Backbone.View

			className: 'repository-commits'

			events: 
				'click a': (event) -> Events.followLink event

			initialize: (options) ->
				group = options.repository.get 'group'
				name = options.repository.get 'name'
				@filter = new Filter
					container: '.repository-commits .content-box'
					template: listTemplate
					filterId: 'filter'
					url: "ws/history/#{group}/#{name}?"
					callback: (result) =>
						result.repository = {group: group, name: name}
						result.groups = @prepareModel result.data
						result.formatDate = Format.date
						console.log result

			render: (renderOptions) ->
				@$el.html template
				Renderer.render @, renderOptions
				@filter.init()

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