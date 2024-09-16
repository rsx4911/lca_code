define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/Repository'
				'cs!models/CurrentUser'
				'templates/views/repository/create'
			]

	(Backbone, Events, Forms, Layers, Model, Renderer, Router, Repository, currentUser, template) ->

		class RepositoryCreate extends Backbone.View

			loadGroups: (callback) ->
				$.ajax
					type: 'GET'
					url: 'ws/group?onlyIfCanWrite=true&page=0'
					success: (result) =>
						options = []
						for group in result.data
							options.push [group.name, group.label]
						callback options

			createRepository: () ->
				Events.preventDefault event
				@repository.set Forms.toJson 'repository-form'
				@repository.set 'isNew', true
				Model.save @repository, 
					success: () => 
						group = @repository.get 'group'
						name = @repository.get 'name'
						isExternal = @importFormat is 'external'
						isJsonLd = @importFormat is 'json-ld'
						if @doImport
							Layers.showProgressIndicator 'Importing'
							if isExternal
								data = JSON.stringify
									url: @$('#url').val()
									username: @$('#username').val()
									password: @$('#password').val()
									token: @$('#token').val()
							else
								data = new FormData()
								data.append('file', @$('#data')[0].files[0])
							url = 'ws/repository/import/'
							if isExternal
								url += 'external/'
							url += "#{group}/#{name}"
							if isJsonLd
								url += '?format=json-ld'
								data.append 'commitMessage', @$('#commitMessage').val()
							$.ajax	
								type: 'POST'
								url: url
								cache: isExternal
								contentType: if isExternal then 'application/json' else false
								processData: isExternal
								data: data
								success: () -> 
									Layers.hideProgressIndicator()
									Router.navigate "#{group}/#{name}"						
								error: (response) =>
									if response.responseJSON and response.responseJSON.field is 'token'
										@$('#token-input').show()
									Forms.handleError 'repository-form', response
									$.ajax
										type: 'DELETE'
										url: "ws/repository/#{group}/#{name}"
										success: () => Layers.hideProgressIndicator()
										error: () => Layers.hideProgressIndicator()
						else
							Router.navigate "#{group}/#{name}"						
					error: (model, response) -> Forms.handleError 'repository-form', response
				return false

			className: 'repository-view multi-box-view'

			events:
				'click [data-action=create-repository]': 'createRepository'

			initialize: (options) ->
				{@groupName, @doImport, @importFormat} = options
				@repository = new Repository()

			render: (renderOptions) ->
				@loadGroups (groups) =>
					unless @groupName
						@groupName = currentUser.get 'username'
					@$el.html template
						groups: groups
						selection: @groupName
						doImport: @doImport
						importFormat: @importFormat
					Renderer.render @, renderOptions

)