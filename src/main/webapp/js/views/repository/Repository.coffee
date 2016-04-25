define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/repository/repository'
			]

	(Backbone, Avatar, Events, Forms, Layers, Renderer, Status, Router, currentUser, template) ->

		class RepositoryView extends Backbone.View

			className: 'repository-view multi-box-view'

			events:
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'repository', @repository.get('group') + '/' + @repository.get('name')
				'click [data-action=delete-repository]': (event) -> @deleteRepository event
				'click [data-action=clone-repository]': (event) -> @openCloneLayer event

			initialize: (options) ->
				{@repository} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				@$el.html template
					repository: repository
				Renderer.render @, renderOptions

			deleteRepository: (event) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				Layers.askDeleteQuestion "repository #{fullPath}", fullPath, () =>
					Layers.showProgressIndicator 'Deleting'
					@repository.destroy
						success: () =>
							Layers.hideProgressIndicator()
							Router.navigate 'dashboard/repositories'

			openCloneLayer: (event) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				@loadCommitsAndGroups (commits, groups) =>
						Layers.showTemplateInLayer
							title: "Clone #{fullPath}"
							template: 'repository/clone'
							model: {commits: commits, groups: groups, formatCommitDescription: @formatCommitDescription}
							buttons: [{text: 'Clone', className: 'btn-success', callback: () => @cloneRepository()}]

			loadCommitsAndGroups: (callback) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				$.ajax
					type: 'GET'
					url: "/ws/history/#{fullPath}/null"
					success: (commits) =>
						$.ajax
							type: 'GET'
							url: '/ws/group?onlyIfCanWrite=true'
							success: (result) =>
								options = []
								options.push currentUser.get 'username'
								for group in result.data
									options.push group.name
								callback commits, options

			cloneRepository: () ->
				repo = @repository.toJSON()
				newGroup = $('#group').val()
				newName = $('#name').val()
				commitId = $('#commit').val()
				unless newName
					Forms.handleError 'clone-form', {responseJSON: {field: 'name', message: 'Missing input: Name'}}
				Layers.showProgressIndicator 'Cloning'
				$.ajax
					type: 'POST'
					url: "/ws/repository/clone/#{repo.group}/#{repo.name}/#{commitId}/#{newGroup}/#{newName}"
					success: () -> 
						Layers.hideProgressIndicator()
						Layers.closeActive()
						Status.success 'Successfully cloned repository'
						Router.navigate "#{newGroup}/#{newName}"
					error: (response) -> 
						Layers.hideProgressIndicator()
						Forms.handleError 'clone-form', response

			formatCommitDescription: (text) ->
				if text.length < 100
					return text
				space = -1
				while text.indexOf(' ', space + 1) < 100 and text.indexOf(' ', space + 1) isnt -1
					space = text.indexOf(' ', space + 1)
				if space is -1
					return text.substring(0, 100) + '...'
				return text.substring(0, space) + '...'

)