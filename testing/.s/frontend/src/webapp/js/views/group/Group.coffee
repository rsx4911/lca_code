define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/group/group'
				'templates/views/group/repositories'
			]

	(Backbone, Avatar, Events, Filter, Format, Forms, Layers, Renderer, Roles, Status, Router, currentUser, template, listTemplate) ->

		class GroupView extends Backbone.View

			className: 'group-view multi-box-view'

			events:
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'group', @group.get('name')
				'change [data-setting]': 'setSetting'
				'keydown #maxSize': (event) -> Events.validateNumber event
				'click [data-action=create-repository]': () -> Router.navigate 'repository/new/' + @group.get('name')
				'click [data-action=import-repository]': () -> Router.navigate 'repository/import/' + @group.get('name')
				'click [data-action=import-json]': () -> Router.navigate 'repository/import-json/' + @group.get('name')
				'click [data-action=delete-group]': 'deleteGroup'

			updateCount: (repo) ->
				repoId = "#{repo.group}/#{repo.name}"
				$.get "ws/repository/count/#{repoId}", (count) =>
					$('.dataset-count-container[data-repo-id="' + repoId + '"]').html "#{count.datasets} #{if count.datasets is 1 then 'data set' else 'data sets' }"

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
						result.formatDate = Format.date
						setRole = (r) ->
							role = Roles[r.role]
							if role
								r.role = { name: Roles[r.role].name, description: Roles[r.role].descriptionForGroup} 
							else
								r.role = undefined
						setRole r for r in result.data
					afterRender: (result) =>
						@$('.group-repository-count').html(result.resultInfo.totalCount)
						setTimeout () =>
							for repo in result.data
								@updateCount repo
						, 10

			render: (renderOptions) ->
				group = @group.toJSON()
				@$el.html template
					group: group
					isAdmin: currentUser.isUserManager() || currentUser.isDataManager()
				Renderer.render @, renderOptions
				@setMaxSize group.settings.maxSize
				Avatar.initCropper 'group', group.name 
				@filter.init()

			setSetting: (event) ->
				target = $ Events.target event
				group = @group.toJSON()
				setting = target.attr 'data-setting'
				if setting is 'MAX_SIZE'
					size = parseInt @$('#maxSize').val()
					if isNaN(size)
						value = 0
					else
						unit = parseInt @$('#maxSize-group #unit').val()
						value = size * unit
				else
					value = target.val()
				$.ajax
					type: 'PUT'
					url: "ws/group/settings/#{group.name}/#{setting}"
					contentType: 'application/json'
					data: JSON.stringify({value: value || 0})

			setMaxSize: (size) ->
				unless size
					return
				if size % 1073741824 is 0
					@$('#maxSize-group #unit').val('1073741824')
					@$('#maxSize').val(size / 1073741824)
				else
					@$('#maxSize-group #unit').val('1048576')
					@$('#maxSize').val(parseInt(size / 1048576))

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