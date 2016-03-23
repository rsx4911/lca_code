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
				'templates/views/group/group'
			]

	(Backbone, Avatar, Events, Forms, Layers, Renderer, Status, Router, currentUser, template) ->

		class GroupView extends Backbone.View

			className: 'group-view multi-box-view'

			events:
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'group', @group.get('name')
				'click [data-action=delete-group]': (event) -> @deleteGroup event

			initialize: (options) ->
				{@group} = options

			render: (renderOptions) ->
				@$el.html template
					group: @group.toJSON()
				Renderer.render @, renderOptions

			deleteGroup: (event) ->
				name = @group.get 'name'
				Layers.askDeleteQuestion "group #{name}", name, () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: "/ws/group/#{name}"
						success: () =>
							Layers.hideProgressIndicator()
							Router.navigate 'dashboard/groups'

)