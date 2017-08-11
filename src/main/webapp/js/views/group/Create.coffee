define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/Group'
				'templates/views/group/create'
			]

	(Backbone, Events, Forms, Model, Renderer, Router, Group, template) ->

		class GroupCreate extends Backbone.View

			className: 'group-view multi-box-view'

			createGroup: () ->
				@group.set Forms.toJson 'group-form'
				@group.set 'isNew', true
				unless @group.get('name')
					Forms.handleError 'group-form', {responseJSON: {field: 'name', message: 'Missing input: Name'}}
					return false
				Model.save @group, 
					success: () => 
						name = @group.get 'name'
						Router.navigate "groups/#{name}"						
					error: (model, response) -> Forms.handleError 'group-form', response
				return false

			events:
				'click [data-action=create-group]': 'createGroup'

			initialize: (options) ->
				@group = new Group()

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions

)