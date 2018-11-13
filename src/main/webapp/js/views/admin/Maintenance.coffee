define([
				'backbone'
				'cs!utils/Renderer'
				'templates/views/admin/maintenance'
			]

	(Backbone, Renderer, template) ->

		class MaintenanceView extends Backbone.View

			className: 'maintenance-view multi-box-view'

			events:
				'click .toggle-maintenance-mode': 'toggleMaintenanceMode'

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/settings/maintenanceMode'
					success: (value) =>
						@maintenanceModeActive = value is true or value is 'true'
						@$el.html template
							maintenanceModeActive: @maintenanceModeActive
						Renderer.render @, renderOptions

			toggleMaintenanceMode: () ->
				$.ajax
					type: 'PUT'
					url: 'ws/admin/area/settings'
					contentType: 'application/json'
					data: JSON.stringify({key: 'MAINTENANCE_MODE', value: !@maintenanceModeActive})
					success: () -> Backbone.history.loadUrl()

)