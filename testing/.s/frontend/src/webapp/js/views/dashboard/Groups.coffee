define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/dashboard/groups'
				'templates/views/dashboard/groups-list'
			]

	(Backbone, Events, Filter, Renderer, Roles, Router, currentUser, template, listTemplate) ->

		class DashboardGroups extends Backbone.View

			className: 'dashboard'

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'click [data-action=create-group]': () -> Router.navigate 'group/new'

			initialize: () ->
				@filter = new Filter
					container: '#groups'
					template: listTemplate
					filterId: 'filter'
					url: 'ws/group?module=DASHBOARD&'
					beforeRender: (result) =>
						for data in result.data
							if data.name is currentUser.get('username')
								data.isUserspace = true
						setRole = (r) ->
							role = Roles[r.role]
							if role
								r.role = { name: Roles[r.role].name, description: Roles[r.role].descriptionForGroup} 
							else
								r.role = undefined
						setRole r for r in result.data
						

			render: (renderOptions) ->
				@$el.html template
					canCreateGroups: (currentUser.get('settings')?.canCreateGroups or currentUser.isAdmin())
				Renderer.render @, renderOptions
				@filter.init (result) =>
					if result.resultInfo.totalCount is 0
						@$('#groups').append('<div class="no-content-message">No groups found</div>')

)