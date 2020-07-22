define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!models/Settings'
				'cs!models/CurrentUser'
				'templates/views/admin/settings'
			]

	(Backbone, Events, Forms, Layers, Renderer, Status, settings, currentUser, template) ->

		class ServerSettingsView extends Backbone.View

			className: 'server-settings-view multi-box-view'

			events: 
				'change input': 'updateSetting'
				'click [data-action=test-mail]': 'testMailConfiguration'
				'click [data-action=test-search]': 'testSearchConfiguration'
				'click [data-action=test-glad]': 'testGladConfiguration'

			render: (renderOptions) ->
				@$el.html template
					settings: settings.toMap()
				Renderer.render @, renderOptions
				Forms.fill 'settings-form', settings.toMap()
				@updateUI()

			updateSetting: (event) ->
				target = $ Events.target event
				key = target.attr 'id'
				value = if target.attr('type') is 'checkbox' then target.is ':checked' else target.val()
				@setSetting key, value
				@updateUI()

			updateUI: () ->
				@$('#USER_REGISTRATION_APPROVAL_ENABLED').prop 'disabled', !@$('#USER_REGISTRATION_ENABLED').is(':checked')			
				@$('#DATASET_TAGS_ON_DASHBOARD_ENABLED, #DATASET_TAGS_ON_GROUPS_ENABLED, #DATASET_TAGS_ON_REPOSITORIES_ENABLED').prop 'disabled', !@$('#DATASET_TAGS_ENABLED').is(':checked')			

			setSetting: (key, value, callback) ->
				settings.setVal key, value
				$.ajax
					type: 'PUT'
					url: 'ws/admin/area/settings'
					contentType: 'application/json'
					data: JSON.stringify({key: key, value: value})
					success: () -> callback?()

			testMailConfiguration: (event) ->
				Layers.promptInput 'Recipient', 'text', currentUser.get('email'), (recipient) ->
					$.ajax
						type: 'GET'
						url: "ws/admin/area/testMailConfig/#{recipient}"
						success: () -> Status.success "Test email was send to #{recipient}"
						error: (error) -> 
							text = error?.responseText
							unless text
								text = 'Could not send test mail'
							Status.error text

			testSearchConfiguration: (event) ->
				@setSetting 'SEARCH_CLUSTER', @$('#SEARCH_CLUSTER').val(), () =>
					@setSetting 'SEARCH_HOST', @$('#SEARCH_HOST').val(), () =>
						@setSetting 'SEARCH_INDEX_NAME', @$('#SEARCH_INDEX_NAME').val(), () =>
							$.ajax
								type: 'GET'
								url: 'ws/admin/area/testSearchConfig'
								success: () -> Status.success 'Search is configured correctly'
								error: (error) -> 
									text = error?.responseText
									unless text
										text = 'Could not reach elastic search'
									Status.error text

			testGladConfiguration: (event) ->
				@setSetting 'GLAD_URL', @$('#GLAD_URL').val(), () ->
					$.ajax
						type: 'GET'
						url: 'ws/admin/area/testGladConfig'
						success: () -> Status.success 'GLAD service is configured correctly'
						error: (error) ->
							text = error?.responseText
							unless text
								text = 'Could not reach GLAD service'
							Status.error text

)