define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'cs!utils/Status'
				'cs!app/Router'
				'templates/views/group/group'
				'templates/views/group/repositories'
			]

	(Backbone, Avatar, Events, Filter, Forms, Layers, Renderer, Roles, Status, Router, template, listTemplate) ->

		class GroupView extends Backbone.View

			className: 'group-view multi-box-view'

			events:
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'group', @group.get('name')
				'change #label': (event) -> @setSetting event, 'label'
				'change #description': (event) -> @setSetting event, 'description'
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
					url: "ws/repository?module=GROUP&"
					beforeRender: (result) =>
						setRole = (r) ->
							role = Roles[r.role]
							if role
								r.role = { name: Roles[r.role].name, description: Roles[r.role].descriptionForGroup} 
							else
								r.role = undefined
						setRole r for r in result.data
					afterRender: (result) =>
						@$('.group-repository-count').html(result.resultInfo.totalCount)

			render: (renderOptions) ->
				@$el.html template
					group: @group.toJSON()
				Renderer.render @, renderOptions
				Avatar.initCropper 'group', @group.get('name')
				@filter.init()

			setSetting: (event, setting) ->
				target = $ Events.target event
				value = target.val()
				group = @group.toJSON()
				$.ajax
					type: 'PUT'
					url: "ws/group/settings/#{group.name}/#{setting}"
					contentType: 'application/json'
					data: JSON.stringify({value: value || ''})

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