define([
				'cs!utils/ModelTree'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Model'
				'cs!utils/ModelTypes'
				'cs!models/CurrentUser'
				'templates/views/layer'
				'templates/views/progress-indicator'
				'bootstrap'
			]

	(ModelTree, Data, Events, Format, Model, ModelTypes, currentUser, template, progressIndicatorTemplate) ->

		Layers = () ->

		Layers:: = (() ->

			# private

			showInLayer = (options) ->
				if options.buttons
					for button in options.buttons
						unless button.id
							button.id = createRandomId()
				@closeActive()
				$('body').append template options
				if options.viewInstance
					options.viewInstance.render
						container: $('.modal-body')
				$('.modal').on 'hidden.bs.modal', (event) ->
					$('.modal').remove()
					options.onClose?()
				if options.static
					$('.modal').modal
						backdrop: 'static'
				else
					$('.modal').modal()
				if options.actionButtons?.length
					for button in options.actionButtons
						if button.callback
							unless button.context
								button.context = @
							setListener button
				if options.buttons?.length
					for button in options.buttons
						if button.callback
							unless button.context
								button.context = @
							setListener button
				if options.autoAdjustHeight
					updateHeight()
					$('a, button', '.modal').on 'click', () ->
						setTimeout updateHeight, 1

			setListener = (button) ->
				$(".modal button##{button.id}").on 'click', (event) ->
					if typeof button.callback is 'function'
						button.callback.apply button.context, event
					else
						button.context[button.callback].apply button.context, event

			createRandomId = () ->
				lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
				text = ''
				for i in [1..6]
					index = Math.random() * lexicon.length
					text += lexicon.charAt Math.floor index;
				return text

			wrapIndex = (onAnswer, index) ->
				() => 
					@closeActive()
					onAnswer?(index)

			updateHeight = () ->
				maxHeight = $('.modal-body').css('max-height')
				height = $('.modal-body').innerHeight() - $('.modal-body').height() + 20
				$('.modal-body').children().each () ->
					height += $(@).outerHeight()
				if height > maxHeight
					height = maxHeight
				$('.modal-body').css 'height', height
				totalHeight = height + $('.modal-header')?.outerHeight() + $('.modal-footer')?.outerHeight()
				$('.modal-content').css 'height', totalHeight
				$('.modal-dialog').css 'height', totalHeight

			# public

			constructor: Layers

			showMessageInLayer: (options) ->
				internalOptions = $.extend {}, options
				(@_ showInLayer) internalOptions

			showTemplateInLayer: (options) ->
				if options.template
					require ["templates/views/#{options.template}"], (templ) =>
						options.body = templ options.model
						(@_ showInLayer) options
						Events.listenToAnchorClicks $('.modal'), @closeActive
						options.callback?()

			showViewInLayer: (options) ->
				if options.view
					require ["cs!views/#{options.view}"], (View) =>
						viewOptions = options.viewOptions or {}
						viewOptions.inLayer = true # views can react to this if necessary
						view = new View viewOptions
						options.viewInstance = view
						if options.buttons
							for button in options.buttons
								unless button.context
									button.context = view
						(@_ showInLayer) options

			showLoginLayer: () ->
				@showViewInLayer
					title: 'Sign in'
					view: 'LoginLayer'
					notCloseable: true
					static: true
					buttons: [
						{id: 'close', text: 'Close', className: 'btn-default', callback: () -> window.location.href = '/login'}
						{id: 'login', text: 'Login', className: 'btn-primary', callback: 'login'}
					]
			
			promptInput: (options) ->
				unless options.value
					options.value = ''
				buttons = []
				buttons.push {text: 'Cancel', className: 'btn-default', callback: @closeActive}
				buttons.push {text: 'Ok', className: 'btn-success', callback: () =>
					if options.type is 'file'
						file = $('#input-data')[0].files[0]
						fileName = Forms.getFileName 'input-data'
						if !file or !fileName
							options.callback()
						else 
							fileData = new FormData()
							fileData.append 'file', file
							fileData.append 'fileName', fileName
							options.callback fileData
					else
						options.callback $('#input-data').val()
					@closeActive()
				}
				body = "<label for=\"input-data\">#{options.label}</label>"
				if options.type is 'textarea'
					body += '<textarea id="input-data" class="form-control" rows="5"></textarea>'
				else if options.type is 'select'
					body += '<select id="input-data" name="input-data" class="form-control">'
					for option in options.options
						if !Array.isArray option
							option = [option, option]
						body += "<option value=#{option[0]}>#{option[1]}</option>"
					body += '</select>'
				else
					inputType = if options.type is 'date' then 'text' else options.type
					body = "<input id=\"input-data\" type=\"#{inputType}\" class=\"form-control\">"
				@showMessageInLayer
					body: body
					title: "Specify #{options.label}"
					buttons: buttons
				if options.value
					$('#input-data').val options.value

			handleUserAndTeamData: (options, users, teams) ->
				existing = options.exclude or []
				if options.excludeSelf
					existing.push username: currentUser.get('username')
				users = if !options.users then [] else Data.usersToOptions users, existing, (options.excludeSelf or options.exclude)
				teams = if !options.teams then [] else Data.teamsToOptions teams
				@showTemplateInLayer
					template: 'select-user'
					title: if options.title then options.title else 'Select user'
					model: {users: users, teams: teams}
					buttons: [
						{id: 'select-user', className: 'btn-primary', text: 'Select', callback: () =>
							selection = $ '.modal #user-selection-name option:selected'
							type = selection.attr 'data-group-id'
							id = selection.val()
							displayName = selection.text()
							@closeActive()
							options.callback {type: type, id: id, displayName: displayName}
						}
					]

			selectUser: (options) ->
				unless options
					options = {users: true, teams: true}
				options.users = if options.users is false then false else true
				options.teams = if options.teams is false then false else true
				if !options.users and !options.teams
					return
				if !options.users
					Data.getTeams options.module, (teams) => @handleUserAndTeamData options, null, teams
				else if !options.teams
					Data.getUsers options.module, options.repository, (users) => @handleUserAndTeamData options, users
				else
					Data.getUsersAndTeams options.module, options.repository, (users, teams) => @handleUserAndTeamData options, users, teams

			selectModel: (options) ->
				unless options
					return []
				unless options.repositoryPath 
					return []
				unless options.callback
					return []
				title = if options.multipleSelection then 'Select data sets' else 'Select data set'
				if options.type
					if options.multipleSelection
						title = "Select #{ModelTypes[options.type].toLowerCase()}"
					else
						title = "Select #{ModelTypes.singular(options.type).toLowerCase()}"
				actionButtons = []
				if options.multipleSelection
					actionButtons.push {id: 'check-all', text: 'Check all', callback: () -> $("#model-tree").jstree('check_all')}
					actionButtons.push {id: 'uncheck-all', text: 'Uncheck all', callback: () -> $("#model-tree").jstree('uncheck_all')}
				@showTemplateInLayer
					title: title
					template: 'select-model-layer'
					model: 
						label: if options.type then ModelTypes.singular(options.type) else 'Data set'
						selectVersion: options.selectVersion
						releases: options.releases
					buttons: [
						{text: 'Cancel', callback: () => @closeActive()}
						{id: 'select-model-button', text: 'Select', className: 'btn-primary', callback: () => 
							if options.multipleSelection
								selection = ModelTree.getSelection '#model-tree'
								@closeActive()
								options.callback selection
							else
								refId = ModelTree.getSelection('#model-tree', true)
								commitId = null
								if options.selectVersion
									commitId = $('#model-selection #commitId').val()
								@closeActive()
								options.callback refId, commitId
						}
					]
					actionButtons: actionButtons
					callback: () =>
						ModelTree.init '#model-tree', options.repositoryPath, 
							multipleSelection: options.multipleSelection
							defaultPath: options.path || options.type
							commitId: options.commitId
						$('#select-model-button').prop 'disabled', true
						$('#model-tree').on 'activate_node.jstree', (event, data) =>
							selection = ModelTree.getSelection '#model-tree'
							$('#select-model-button').prop 'disabled', !selection or (options.multipleSelection and !selection.length)
							if options.type and !options.multipleSelection and options.selectVersion and !options.releases
								$('#select-model-button').prop 'disabled', true
								refId = data.node.original.refId
								@showProgressIndicator ['Loading', 'versions']
								$.ajax
									type: 'GET'
									url: "ws/public/history/#{options.repositoryPath}/#{options.type}/#{refId}"
									success: (commits) =>
										$('#select-model-button').prop 'disabled', (!commits || !commits.length)
										$('#model-selection #commitId').empty()
										if commits?.length
											for commit, index in commits
												$('#model-selection #commitId').append '<option value="' + commit.id + '">' + (if index is 0 then 'Latest' else commit.id) + '</option>'
												$('#model-selection #commitId').append '<optgroup class="additional-info" label="&nbsp; &nbsp;' + Format.commitDescription(commit.message) + '"></optgroup>'
										@hideProgressIndicator()
									error: () => 
										$('#select-model-button').prop 'disabled', true
										@hideProgressIndicator()

			selectCommit: (commits, commitId, callback) ->
				@showTemplateInLayer
					title: 'Select version'
					template: 'select-commit-layer'
					model:
						commits: commits
						commitId: commitId
						formatCommitDescription: Format.commitDescription
					buttons: [
						{id: 'close', className: 'btn-default', text: 'Close', callback: () => @closeActive()}
						{id: 'select', className: 'btn-primary', text: 'Select', callback: () => 
							selection = $('#commit-selection #commitId').val()
							@closeActive()
							callback selection
						}
					]

			askQuestion: (options) ->
				unless options.question
					return
				unless options.answers?.length
					return
				unless options.type
					options.type = 'success'
				buttons = []
				for answer, index in options.answers
					buttons.push
						text: answer
						className: (if index is (options.answers.length - 1) then "btn-#{options.type}" else 'btn-default')
						callback: (@_ wrapIndex) options.onAnswer, index
				@showMessageInLayer
					body: options.question
					title: options.title
					buttons: buttons

			askDeleteQuestion: (toDelete, confirmationPhrase, callback) ->
				buttons = []
				buttons.push
					text: 'Confirm'
					id: 'btn-confirm-delete'
					className: 'btn-danger'
					callback: () =>
						@closeActive()
						callback?()
				buttons.push
					text: 'Cancel'
					className: 'btn-default'
					callback: @closeActive
				@showTemplateInLayer
					title: 'Confirmation required'
					template: 'confirm-delete'
					model: 
						text: "You are about to delete #{toDelete}. This action can not be undone. Are you absolutely sure?"
						confirmationPhrase: confirmationPhrase
					buttons: buttons
					callback: () =>
						if confirmationPhrase
							$('#btn-confirm-delete').prop 'disabled', true
							$('#confirmation-phrase').on 'keyup', (event) ->
								target = $ Events.target event
								$('#btn-confirm-delete').prop 'disabled', (target.val() isnt confirmationPhrase)
						$('#confirmation-phrase').on 'keydown', (event) =>
							target = $ Events.target event
							key = Events.keyCode event
							if (!confirmationPhrase or target.val() is confirmationPhrase) and key is 13
								@closeActive()
								callback?()								

			showProgressIndicator: (message) ->
				$('.progress-indicator').remove()
				$('body').append progressIndicatorTemplate
					message: message				

			isProgressShowing: () ->
				return $('.progress-indicator').length > 0

			hideProgressIndicator: () ->
				$('.progress-indicator').remove()

			closeActive: () ->
				active = $ '.modal' 
				active.modal 'hide'
				active.remove()

			_: (callback) ->
				() =>
					callback.apply @, arguments

		)()

)