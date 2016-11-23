define([
				'cs!utils/Layers'
			]

	(Layers) ->

		hide = (statusBar) ->
			if statusBar.prop('data-removed')
				return
			statusBar.prop 'data-removed', true	
			statusBar.animate
				top: statusBar.outerHeight() * -1
			, 500
			setTimeout () ->
				statusBar.remove()
			, 500

		message: (message, type, details, time = 5000) ->
			if type is 'error'
				type = 'danger'
			if $.inArray(type, ['success', 'warning', 'info', 'danger']) is -1
				return
			statusBarHtml = "<div class=\"status-bar alert-#{type}\">#{message}"
			statusBarHtml += "</div>"
			statusBar = $ statusBarHtml
			$('body').append statusBar
			statusBar.on 'click', () =>
				hide statusBar
			statusBar.animate
				top: 0
			, 500 
			setTimeout () =>
				hide statusBar
			, time

		ajaxError: (response, time = 5000) ->
			errorText = "#{response.status} #{response.statusText}"			
			@message errorText, 'error', response.responseText, time

		error: (message, time = 5000) ->
			@message message, 'error', null, time

		success: (message, time = 5000) ->
			@message message, 'success', null, time

		warning: (message, time = 5000) ->
			@message message, 'warning', null, time

		info: (message, time = 5000) ->
			@message message, 'info', null, time

)