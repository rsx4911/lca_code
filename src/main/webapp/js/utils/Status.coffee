define([
				'cs!utils/Layers'
			]

	(Layers) ->

		message: (message, type, details, time = 5000) ->
			if type is 'error'
				type = 'danger'
			if $.inArray(type, ['success', 'warning', 'info', 'danger']) is -1
				return
			statusBarHtml = "<div class=\"status-bar alert-#{type}\">#{message}" 				
			if details
				statusBarHtml += '&nbsp;<button class="btn btn-xs btn-default">Show details</button>'
			statusBarHtml += "</div>"
			statusBar = $ statusBarHtml
			if details
				$('button', statusBar).on 'click', () ->
					Layers.showMessageInLayer 
						title: message
						body: details
			$('body').append statusBar
			statusBar.animate
				bottom: 0
			, 1000 
			setTimeout () ->
				statusBar.animate
					bottom: statusBar.outerHeight() * -1
				, 1000
				setTimeout () ->
					statusBar.remove()
				, 1000
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