define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Renderer'
				'cs!views/repository/Download'
				'templates/views/repository/commit/commits'
				'templates/views/repository/commit/commit-list'
			]

	(Backbone, moment, Events, Filter, Format, Renderer, Download, template, listTemplate) ->

		class RepositoryCommits extends Backbone.View

			className: 'repository-commits'

			events: 
				'click a': (event) -> Events.followLink event
				'click [data-action=download-changelog]': (event) -> 
					Events.preventDefault(event)
					Download.changelog @repository.get('group'), @repository.get('name')

			initialize: (options) ->
				{@repository, @standalone} = options
				group = @repository.get 'group'
				name = @repository.get 'name'
				@filter = new Filter
					container: '.repository-commits .content-box'
					template: listTemplate
					filterId: 'filter'
					url: "ws/history/search/#{group}/#{name}?"
					beforeRender: (result) =>
						unless result
							return
						result.repository = {group: group, name: name}
						result.standalone = @standalone
						@prepareModel result
						result.formatDate = Format.date

			render: (renderOptions) ->
				@$el.html template
					canCreateChangeLog: @repository.get('userCanCreateChangeLog')
					standalone: @standalone
				Renderer.render @, renderOptions
				@filter.init()

			prepareModel: (result) ->
				# id, message, timestamp, user
				result.groups = []
				previous = null
				group = null
				if result.data
					for commit in result.data
						if !@isSameDay(previous, commit.timestamp)
							group = {commits: []}
							result.groups.push group
						group.date = new Date(commit.timestamp)
						group.count = result.resultInfo.groupCount[commit.id]
						group.commits.push commit
						previous = commit.timestamp

			isSameDay: (t1, t2) ->
				d1 = moment(t1)
				d2 = moment(t2)
				if d1.year() isnt d2.year()
					return false
				if d1.dayOfYear() isnt d2.dayOfYear()
					return false
				return true

)