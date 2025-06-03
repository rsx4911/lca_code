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
				'click [data-action=list-glad]': 'listGladData'
				'click [data-action=clear-glad]': () -> @deleteGladData()

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
				type = typeAndKey[0]
				key = typeAndKey[1]
				value = if target.attr('type') is 'checkbox' then target.is ':checked' else target.val()
				@setSetting type, key , value, (returnValue) =>
					if target.attr('type') is 'checkbox'
						target.prop('checked', returnValue is true or returnValue is 'true')
					else
						target.val returnValue
					if type is 'SERVER_SETTING'
						settings.setVal key, returnValue
					@updateUI()

			updateUI: () ->
				depending = {
					'SEARCH_ENABLED': ['USAGE_SEARCH_ENABLED']
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
				@$('#SEARCH_INDEX__PUBLIC').prop 'disabled', !@$('#SERVER_SETTING__RELEASES_ENABLED').is(':checked')
				@$('#SEARCH_INDEX__USAGE').prop 'disabled', !@$('#SERVER_SETTING__USAGE_SEARCH_ENABLED').is(':checked')
				@$('#SEARCH_INDEX__PUBLIC_USAGE').prop('disabled', !@$('#SERVER_SETTING__USAGE_SEARCH_ENABLED').is(':checked') or !@$('#SERVER_SETTING__RELEASES_ENABLED').is(':checked'))

			setSetting: (type, key, value, callback) ->
				if type is 'SERVER_SETTING' or type is 'SEARCH_INDEX'
					if type is 'SEARCH_INDEX' or key is 'SEARCH_ENABLED' or key is 'USAGE_SEARCH_ENABLED' or key is 'RELEASES_ENABLED'
						@$('#search-note').show()
				$.ajax
					type: 'PUT'
					url: 'ws/admin/area/settings'
					contentType: 'application/json'
					data: JSON.stringify({type: type, key: key, value: value})
					success: (returnValue) -> callback?(returnValue)

			testMailConfiguration: (event) ->
				Layers.promptInput
					type: 'text'
					label: 'Recipient'
					value: currentUser.get('email')
					callback: (recipient) ->
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
						url: 'ws/datamanager/glad/testConfig'
						success: () -> Status.success 'GLAD service is configured correctly'
						error: (error) ->
							text = error?.responseText
							unless text
								text = 'Could not reach GLAD service'
							Status.error text

			deleteGladData: (repositoryPath) ->
				path = if repositoryPath then "/#{repositoryPath}" else ''
				name = if repositoryPath then " of repository #{repositoryPath}" else ''
				Layers.showProgressIndicator 'Deleting data from GLAD'
				$.ajax
					type: 'DELETE'
					url: "ws/datamanager/glad/clear#{path}"
					success: () ->
						Layers.hideProgressIndicator()
						Status.success "Data#{name} is now being deleted from GLAD"
					error: () ->
						Layers.hideProgressIndicator()
						text = error?.responseText
						unless text
							text = 'Could not reach GLAD service'
						Status.error text

			listGladData: (event) ->
				Layers.showProgressIndicator 'Loading data from GLAD'
				$.ajax
					type: 'GET'
					url: "ws/datamanager/glad/list"
					success: (repositories) =>
						Layers.hideProgressIndicator()
						Layers.showTemplateInLayer
							template: 'admin/list-glad'
							title: 'Repositories on GLAD'
							model:
								repositories: repositories
							callback: () =>
								$('[data-repository]').on 'click', (event) =>
									Layers.closeActive()
									target = $ Events.target event
									repository = target.attr 'data-repository' 
									@deleteGladData repository


)