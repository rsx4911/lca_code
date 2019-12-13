define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'templates/views/group/group'
				'templates/views/group/repositories'
			]

	(Backbone, Avatar, Events, Filter, Forms, Layers, Renderer, Status, Router, template, listTemplate) ->

		class GroupView extends Backbone.View

			className: 'group-view multi-box-view'

			events:
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'group', @group.get('name')
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new/' + @group.get('name')
				'click [data-action=import-repository]': () -> Router.navigate 'repository/import/' + @group.get('name')
				'click [data-action=import-json]': () -> Router.navigate 'repository/import-json/' + @group.get('name')
				'click [data-action=delete-group]': 'deleteGroup'

			initialize: (options) ->
				{@group} = options
				name = @group.get 'name'
				@filter = new Filter
					container: '#group-repositories'
					template: listTemplate
					filterId: 'filter'
					filterPrefix: "#{name}/"
					url: "ws/repository?"
					afterRender: (result) =>
						@$('.group-repository-count').html(result.resultInfo.totalCount)

			render: (renderOptions) ->
				@$el.html template
					group: @group.toJSON()
				Renderer.render @, renderOptions
				Avatar.initCropper 'group', @group.get('name')
				@filter.init()

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