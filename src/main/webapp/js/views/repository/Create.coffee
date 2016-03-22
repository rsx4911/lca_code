define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/Repository'
				'cs!models/CurrentUser'
				'templates/views/repository/create'
			]

	(Backbone, Events, Forms, Model, Renderer, Router, Repository, currentUser, template) ->

		class RepositoryCreate extends Backbone.View

			className: 'repository-view multi-box-view'

			createRepository: () ->
				@repository.set Forms.toJson 'repository-form'
				@repository.set 'isNew', true
				Model.save @repository, 
					success: () => 
						group = @repository.get 'group'
						name = @repository.get 'name'
						Router.navigate "#{group}/#{name}"						
					error: (model, response) -> Forms.handleError 'repository-form', response
				return false

			events:
				'click [data-action=create-repository]': (event) -> @createRepository event

			initialize: (options) ->
				@repository = new Repository()

			render: (renderOptions) ->
				@$el.html template()
				@$('#group').val currentUser.get 'username'
				Renderer.render @, renderOptions

)