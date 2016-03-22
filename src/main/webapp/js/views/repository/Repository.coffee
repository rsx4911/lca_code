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

)