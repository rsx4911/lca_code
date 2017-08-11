define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'templates/views/group/group'
			]

	(Backbone, Avatar, Events, Forms, Layers, Renderer, Status, Router, template) ->

		class GroupView extends Backbone.View

			loadRepositories: (callback) ->
				group = @group.get 'name'
				$.ajax
					type: 'GET'
					url: "ws/repository?filter=#{group}/"
					success: callback

			className: 'group-view multi-box-view'

			events:
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'group', @group.get('name')
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new/' + @group.get('name')
				'click [data-action=delete-group]': 'deleteGroup'

			initialize: (options) ->
				{@group} = options

			render: (renderOptions) ->
				@loadRepositories (repositories) =>
					@$el.html template
						group: @group.toJSON()
						repositories: repositories.data
					Renderer.render @, renderOptions
					Avatar.initCropper 'group', @group.get('name')

			deleteGroup: (event) ->
				name = @group.get 'name'
				Layers.askDeleteQuestion "group #{name}", name, () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: "ws/group/#{name}"
						success: () =>
							Layers.hideProgressIndicator()
							Router.navigate 'dashboard/groups'

)