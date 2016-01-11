define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/repository/info'
			]

	(Backbone, Events, Layers, Renderer, Router, currentUser, template) ->

		class RepositoryInfo extends Backbone.View

			deleteRepository = (event) ->
				Layers.showProgressIndicator 'Deleting'
				@repository.destroy
					success: () =>
						Layers.hideProgressIndicator()
						(@_ reload)()

			reload = () ->
				if currentUser.isAdmin() and @adminArea
					Router.navigate 'admin/overview'
				else
					Router.navigate 'dashboard/repositories'

			events:
				'click [data-action=delete-repository]': deleteRepository

			initialize: (options) ->
				{@repository, @adminArea} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				@$el.html template
					repository: repository
					canDelete: currentUser.isAdmin() # TODO include users with permisson to delete
				Renderer.render @, renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)