define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!models/Settings'
				'templates/views/admin/settings'
			]

	(Backbone, Events, Renderer, settings, template) ->

		class ServerSettingsView extends Backbone.View

			className: 'server-settings-view content-box'

			events: 
				'change input[type=checkbox]': 'updateSetting'

			render: (renderOptions) ->
				@$el.html template
					settings: settings.toMap()
				Renderer.render @, renderOptions

			updateSetting: (event) ->
				target = $ Events.target event
				key = target.attr 'id'
				value = target.is ':checked'
				$.ajax
					type: 'PUT'
					url: "ws/admin/area/settings/#{key}/#{value}"

)