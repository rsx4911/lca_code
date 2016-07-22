define([
				'cs!utils/Events'
				'cs!utils/Model'
				'templates/views/layer'
				'templates/views/progress-indicator'
				'bootstrap'
			]

	(Events, Model, template, progressIndicatorTemplate) ->

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
				if options.static
					$('.modal').modal
						backdrop: 'static'
				else
					$('.modal').modal()
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

			showProgressInLayer: (options) ->
				@showViewInLayer
					title: options.title
					view: 'ProgressLayer'
					viewOptions: 
						url: options.url
						pageReloadOnClose: options.pageReloadOnClose
					notCloseable: true
					static: true
					buttons: [
						{id: 'progress-btn-close', text: 'Close', callback: 'close'}
						{id: 'progress-btn-cancel', text: 'Cancel', callback: 'cancel'}
						{id: 'progress-btn-run', className: 'btn-primary', text: 'Run', callback: 'start'}
					]

			askQuestion: (options) ->
				unless options.question
					return
				unless options.answers?.length
					return
				buttons = []
				for answer, index in options.answers
					buttons.push
						text: answer
						className: (if index is (options.answers.length - 1) then 'btn-success' else 'btn-default')
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
					callback: () ->
						$('#btn-confirm-delete').prop 'disabled', true
						$('#confirmation-phrase').on 'keyup', (event) ->
							target = $ Events.target event
							$('#btn-confirm-delete').prop 'disabled', (target.val() isnt confirmationPhrase)

			showProgressIndicator: (message) ->
				$('.progress-indicator').remove()
				$('body').append progressIndicatorTemplate
					message: message				

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