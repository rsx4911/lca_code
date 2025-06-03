define([
				'backbone'
				'moment'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!views/repository/Download'
				'cs!models/CurrentUser'
				'cs!models/Settings'
				'templates/views/repository/commit/commits'
				'templates/views/repository/commit/commit-list'
				'templates/views/repository/commit/commit-info'
			]

	(Backbone, moment, Events, Filter, Format, Forms, Layers, Renderer, Download, currentUser, settings, template, listTemplate, infoTemplate) ->

		class RepositoryCommits extends Backbone.View

			onRelease: (event) ->
				target = $ Events.target event
				commitId = target.attr 'data-commit-id'
				isReleased = target.attr('data-status') is 'released'
				@getReleaseInfo commitId, isReleased, (releaseInfo) =>
					buttons = [{id: 'close', className: 'btn-default', text: 'Cancel', callback: () -> Layers.closeActive()}]
					if isReleased
						buttons.push {id: 'delete', className: 'btn-danger', text: 'Revoke release', callback: () => @revokeRelease(commitId)}
					okButtonLabel = if isReleased then 'Update release' else 'Release'
					buttons.push {id: 'complete-release', className: 'btn-success', text: okButtonLabel, callback: () => @release(commitId, isReleased)}
					dataTypes = settings.get('TYPES_OF_DATA').map((type) -> type)
					dataTypes.splice(0, 0, '')
					Layers.showTemplateInLayer
						title: if isReleased then 'Edit release information' else 'Specify release information'
						template: 'repository/commit/release-info-layer'
						dialogType: 'modal-large'
						model:
							values: releaseInfo
							dataTypes: dataTypes
						buttons: buttons
						callback: () ->
							$('[data-action=changeLog-click]').on 'click', (event) ->
								Events.preventDefault event
								$('#changeLog').val releaseInfo.generatedChangeLog

			getReleaseInfo: (commitId, isReleased, callback) ->
				unless isReleased
					repoSettings = @repository.get 'settings'
					unless repoSettings.label
						repoSettings.label = @repository.get 'name'
					callback repoSettings
					return
				group = @repository.get 'group'
				name = @repository.get 'name'
				$.ajax
					type: 'GET'
					url: "ws/release/#{group}/#{name}/#{commitId}"
					success: callback

			release: (commitId, isReleased) ->
				data = Forms.toJson 'release-info'
				unless data.label
					Forms.markWithMessage 'release-info', 'label', 'Missing input', Forms.validateNotEmpty
				unless data.version
					Forms.markWithMessage 'release-info', 'version', 'Missing input', Forms.validateNotEmpty
				if !data.label or !data.version
					return
				if data.tags
					data.tags = JSON.stringify data.tags.split ','
				Layers.closeActive()
				Layers.showProgressIndicator 'Releasing'
				group = @repository.get 'group'
				name = @repository.get 'name'
				$.ajax
					type: if isReleased then 'PUT' else 'POST'
					url: "ws/release/#{group}/#{name}/#{commitId}"
					contentType: 'application/json'
					data: JSON.stringify(data)								
					success: () ->
						Layers.hideProgressIndicator()
						Backbone.history.loadUrl()

			revokeRelease: (commitId) ->
				Layers.closeActive()
				Layers.showProgressIndicator 'Revoking release'
				group = @repository.get 'group'
				name = @repository.get 'name'
				$.ajax
					type: 'DELETE'
					url: "ws/release/#{group}/#{name}/#{commitId}"
					success: () ->
						Layers.hideProgressIndicator()
						Backbone.history.loadUrl()

			className: 'repository-commits'

			events: 
				'click a': (event) -> Events.followLink event
				'click .release': 'onRelease'
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
					delayedFilter: true
					url: "ws/public/history/search/#{group}/#{name}?"
					beforeRender: (result) =>
						unless result
							return
						result.repository = {group: group, name: name}
						result.standalone = @standalone
						result.releasesEnabled = settings.is 'RELEASES_ENABLED'
						result.canCreateReleases = @repository.get 'userCanCreateReleases'
						result.isLoggedIn = currentUser.isLoggedIn()
						@prepareModel result
						result.formatDate = Format.date
					afterRender: (result) =>
						unless result
							return
						setTimeout () =>
							for commit in result.data
								$.get "ws/public/history/count/#{group}/#{name}/#{commit.id}", (count) =>
									$(".commit-info-container[data-commit-id=#{count.id}]").html infoTemplate count
						, 10

			render: (renderOptions) ->
				@$el.html template
					canCreateChangeLog: settings.is('CHANGE_LOG_ENABLED') and @repository.get('userCanCreateChangeLog')
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