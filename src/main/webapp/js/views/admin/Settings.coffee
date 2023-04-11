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
				'change select': 'updateSetting'
				'click [data-action=test-mail]': 'testMailConfiguration'
				'click [data-action=test-search]': 'testSearchConfiguration'
				'click [data-action=test-glad]': 'testGladConfiguration'

			render: (renderOptions) ->
				@loadSettings (allSettings) =>
					@$el.html template
						settings: allSettings 
					Renderer.render @, renderOptions
					flattened = {}
					for type in Object.keys(allSettings)
						for key in Object.keys(allSettings[type])
							flattened["#{type}__#{key}"] = allSettings[type][key]
					Forms.fill('settings-form', flattened)
					@updateUI()

			loadSettings: (callback) ->
				$.ajax
					type: 'GET'
					url: 'ws/admin/area/settings'
					success: callback

			updateSetting: (event) ->
				target = $ Events.target event
				typeAndKey = target.attr('id').split('__')
				value = if target.attr('type') is 'checkbox' then target.is ':checked' else target.val()
				@setSetting typeAndKey[0], typeAndKey[1], value
				@updateUI()

			updateUI: () ->
				depending = {
					'SEARCH_ENABLED': ['SEARCH_LINKS_ENABLED']
					'PUBLIC_REPOSITORY_ENABLED': ['HOMEPAGE_ENABLED'],
					'USER_REGISTRATION_ENABLED': ['USER_REGISTRATION_APPROVAL_ENABLED'],
					'DATASET_TAGS_ENABLED': ['DATASET_TAGS_ON_DASHBOARD_ENABLED', 'DATASET_TAGS_ON_GROUPS_ENABLED', 'DATASET_TAGS_ON_REPOSITORIES_ENABLED']
				}
				for key in Object.keys(depending)
					disabled = !@$('#SERVER_SETTING__' + key).is(':checked')
					for dependent in depending[key]
						@$('#SERVER_SETTING__' + dependent).prop 'disabled', disabled
						if disabled
							@$('#SERVER_SETTING__' + dependent).prop 'checked', false
				if @$('#SERVER_SETTING__SEARCH_ENABLED').is(':checked')
					@$('.search-settings').removeClass 'hidden'
				else
					@$('.search-settings').addClass 'hidden'
				@$('#SEARCH_SETTING__IO_DATA_INDEX_NAME').prop 'disabled', !@$('#SERVER_SETTING__SEARCH_LINKS_ENABLED').is(':checked')

			setSetting: (type, key, value, callback) ->
				if type is 'SERVER_SETTING'
					settings.setVal key, value
					if key is 'SEARCH_ENABLED' or key is 'SEARCH_LINKS_ENABLED'
						@$('.search-note').show()
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
				@setSetting 'SEARCH_SETTING', 'SCHEMA', @$('#SEARCH_SETTING__SCHEMA').val(), () =>
					@setSetting 'SEARCH_SETTING', 'HOST', @$('#SEARCH_SETTING__HOST').val(), () =>
						@setSetting 'SEARCH_SETTING', 'PORT', @$('#SEARCH_SETTING__PORT').val(), () =>
							@setSetting 'SEARCH_SETTING', 'INDEX_NAME', @$('#SEARCH_SETTING__INDEX_NAME').val(), () =>
								$.ajax
									type: 'GET'
									url: 'ws/admin/area/testSearchConfig'
									success: () -> Status.success 'Search is configured correctly'
									error: (error) -> 
										text = error?.responseText
										unless text
											text = 'Could not reach opensearch'
										Status.error text

			testGladConfiguration: (event) ->
				@setSetting 'SERVER_SETTING', 'GLAD_URL', @$('#SERVER_SETTING__GLAD_URL').val(), () ->
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