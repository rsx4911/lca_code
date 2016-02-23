define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/repository/info'
			]

	(Backbone, Events, Forms, Layers, Renderer, Status, Router, currentUser, template) ->

		class RepositoryInfo extends Backbone.View

			className: 'repository-view multi-box-view'

			events:
				'submit #avatar-form': (event) -> @saveAvatar event
				'click [data-action=delete-repository]': (event) -> @deleteRepository event

			initialize: (options) ->
				{@repository, @adminArea} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				@$el.html template
					repository: repository
					canDelete: currentUser.isAdmin() # TODO include users with permisson to delete
				Renderer.render @, renderOptions

			deleteRepository: (event) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				Layers.askDeleteQuestion "repository #{fullPath}", fullPath, () =>
					Layers.showProgressIndicator 'Deleting'
					@repository.destroy
						success: () =>
							Layers.hideProgressIndicator()
							Router.navigate 'dashboard'

			saveAvatar: (event) ->
				Events.preventDefault event
				file = @$('#avatar', '#avatar-form')[0].files[0]
				unless file
					Layers.askQuestion
						title: 'Reset avatar'
						question: 'You did not select a new image, do you want to replace your current avatar with the default?'
						answers: ['No', 'Yes']
						onAnswer: (index) => 
							if index is 1
								@uploadAvatar()
				else
					@uploadAvatar file
				return false

			uploadAvatar: (file) ->
				formData = new FormData()
				if file
					formData.append 'file', file
				else
					formData.append 'dummy', true
				$.ajax
					type: 'PUT'
					url: '/ws/repository/avatar/' + @repository.get('group') + '/' + @repository.get('name')
					data: formData
					processData: false
					contentType: false
					success: () => @reload()
					error: (response) -> 
						console.log response
						Forms.handleError 'avatar-form', response

			reload: () ->
				if currentUser.isAdmin() and @adminArea
					Router.navigate 'administration/overview'
				else
					Status.success 'Successfully updated repository'
					Backbone.history.loadUrl()

)