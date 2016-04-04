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

			loadGroups: (callback) ->
				$.ajax
					type: 'GET'
					url: '/ws/group?onlyIfCanWrite=true'
					success: (result) =>
						options = []
						options.push currentUser.get 'username'
						for group in result.data
							options.push group.name
						callback options


			createRepository: () ->
				@repository.set Forms.toJson 'repository-form'
				@repository.set 'isNew', true
				unless @repository.get('name')
					Forms.handleError 'repository-form', {responseJSON: {field: 'name', message: 'Missing input: Name'}}
					return false
				Model.save @repository, 
					success: () => 
						group = @repository.get 'group'
						name = @repository.get 'name'
						Router.navigate "#{group}/#{name}"						
					error: (model, response) -> Forms.handleError 'repository-form', response
				return false

			className: 'repository-view multi-box-view'

			events:
				'click [data-action=create-repository]': (event) -> @createRepository event

			initialize: (options) ->
				{@groupName} = options
				@repository = new Repository()

			render: (renderOptions) ->
				@loadGroups (groups) =>
					unless @groupName
						@groupName = currentUser.get 'username'
					@$el.html template
						groups: groups
						selection: @groupName
					Renderer.render @, renderOptions

)