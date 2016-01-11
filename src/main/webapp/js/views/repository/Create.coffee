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

			createRepository = () ->
				@repository.set Forms.toJson 'repository'
				@repository.set 'isNew', true
				unless @repository.get('name')
					Forms.handleError 'repository', {responseJSON: {field: 'name', message: 'Missing input: Name'}}
					return false
				unless @repository.get('group')
					Forms.handleError 'repository', {responseJSON: {field: 'group', message: 'Missing input: Group'}}
					return false
				Model.save @repository, 
					success: () =>
						(@_ reload)()
					error: (model, response) -> Forms.handleError 'repository', response
				return false

			reload = () ->
				if currentUser.isAdmin() and @adminArea
					Router.navigate 'admin/overview'
				else
					group = @repository.get 'group'
					name = @repository.get 'name'
					Router.navigate "repository/#{group}/#{name}"

			events:
				'click [data-action=create-repository]': createRepository

			initialize: (options) ->
				{@adminArea} = options
				@repository = new Repository()

			render: (renderOptions) ->
				@$el.html template()
				@$('#group').val currentUser.get 'username'
				Renderer.render @, renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)