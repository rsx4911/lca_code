define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'templates/views/progress-layer'
			]

	(Backbone, Events, Layers, Renderer, template) ->

		class ProgressLayer extends Backbone.View

			initialize: (options) ->
				{@url, @pageReloadOnClose, @message} = options

			render: (renderOptions) ->
				@$el.html template
					message: @message
				Renderer.render @, renderOptions
				@setButtons true, false, true

			start: () ->
				Pace.ignore () =>
					@setButtons false, true, false
					@socket = new WebSocket @url
					@socket.onopen = () =>
						@didRun = true
						@socket.send 'start'
					@socket.onmessage = (msg) =>
						data = JSON.parse msg.data
						progress = parseInt(data.progress * 100)
						if progress > 100
							progress = 100
						$('.progress-monitor-indicator', @$('.progress-monitor')).css 'width', "#{progress}%"
						$('.progress-monitor-text', @$('.progress-monitor')).html data.message
					@socket.onclose = () =>
						@setButtons true, false, false

			close: () ->
				Layers.closeActive()
				if @didRun
					if @pageReloadOnClose
						window.location.reload()
					else
						Backbone.history.loadUrl()

			cancel: () ->
				$('#progress-btn-cancel').prop 'disabled', true
				@socket.send 'cancel'				

			setButtons: (b1, b2, b3) ->
				$('#progress-btn-close').prop 'disabled', !b1
				$('#progress-btn-cancel').prop 'disabled', !b2
				$('#progress-btn-run').prop 'disabled', !b3

)