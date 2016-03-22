define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Renderer'
				'cs!app/Router'
				'templates/views/dashboard/groups'
				'templates/views/dashboard/groups-list'
			]

	(Backbone, Events, Filter, Renderer, Router, template, listTemplate) ->

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
					url: (page, filter) -> "/ws/group?page=#{page}&filter=#{filter}"

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions
				@filter.init()

			_: (callback) ->
				() =>
					callback.apply @, arguments

)