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
				@loadSettings allSettings ->
					@$el.html template()
					Renderer.render @, renderOptions
					flattened = {}
					for type in Object.keys(allSettings)
						for key in Object.keys(allSettings[type])
							flattened["#{type}-#{key}"] = allSettings[type][key]
					Forms.fill('settings-form', flattened)
					@updateUI()

			loadSettings: (callback) ->
				$.ajax
					type: 'GET'
					ws: "ws/admin/area/settings"
					success: callback

			updateSetting: (event) ->
				target = $ Events.target event
				typeAndKey = target.attr('id').split('-')
				value = if target.attr('type') is 'checkbox' then target.is ':checked' else target.val()
				@setSetting typeAndKey[0], typeAndKey[1], value
				@updateUI()

			updateUI: () ->
				@$('#USER_REGISTRATION_APPROVAL_ENABLED').prop 'disabled', !@$('#USER_REGISTRATION_ENABLED').is(':checked')			
				@$('#DATASET_TAGS_ON_DASHBOARD_ENABLED, #DATASET_TAGS_ON_GROUPS_ENABLED, #DATASET_TAGS_ON_REPOSITORIES_ENABLED').prop 'disabled', !@$('#DATASET_TAGS_ENABLED').is(':checked')			

			setSetting: (type, key, value, callback) ->
				if type is 'SERVER_SETTING'
					settings.setVal key, value
				$.ajax
					type: 'PUT'
					url: 'ws/admin/area/settings'
					contentType: 'application/json'
					data: JSON.stringify({type: type, key: key, value: value})
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
				@setSetting 'SERVER_SETTING', 'SEARCH_CLUSTER', @$('#SEARCH_CLUSTER').val(), () =>
					@setSetting 'SERVER_SETTING', 'SEARCH_HOST', @$('#SEARCH_HOST').val(), () =>
						@setSetting 'SERVER_SETTING', 'SEARCH_INDEX_NAME', @$('#SEARCH_INDEX_NAME').val(), () =>
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
				@setSetting 'SERVER_SETTING', 'GLAD_URL', @$('#GLAD_URL').val(), () ->
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