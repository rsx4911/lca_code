define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/dashboard/groups'
				'templates/views/dashboard/groups-list'
			]

	(Backbone, Events, Filter, Renderer, Router, currentUser, template, listTemplate) ->

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
					url: 'ws/group?'

			render: (renderOptions) ->
				@$el.html template
					canCreateGroups: (currentUser.get('settings')?.canCreateGroups or currentUser.isAdmin())
				Renderer.render @, renderOptions
				@filter.init()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)