define([
				'cs!utils/Layers'
			]

	(Layers) ->

		hide = (statusBar, options) ->
			if statusBar.prop('data-removed')
				return
			statusBar.prop 'data-removed', true	
			animateTime = if options?.sticky then 0 else 500
			statusBar.animate { top: -40 }, animateTime
			if options?.sticky
				$('#cs-header').animate { top: 0 }, 0
				$('#main-navigation').animate { marginTop: 65 }, 0
				$('#main').animate { paddingTop: 65 }, 0
			setTimeout () ->
				statusBar.remove()
			, animateTime

		message: (message, type, options) ->
			if type is 'error'
				type = 'danger'
			if $.inArray(type, ['success', 'warning', 'info', 'danger']) is -1
				return
			statusBarHtml = "<div class=\"status-bar alert-#{type}\">#{message}"
			statusBarHtml += "</div>"
			statusBar = $ statusBarHtml
			$('body').prepend statusBar
			animateTime = if options?.sticky then 0 else 500
			statusBar.animate { top: 0 }, animateTime
			if options?.sticky
				$('#cs-header').animate { top: 40 }, 0
				$('#main-navigation').animate { marginTop: 105 }, 0
				$('#main').animate { paddingTop: 105 }, 0
			else
				$('body').on 'click.statusbar', () =>
					hide statusBar, options
					$('body').off 'click.statusbar'
				setTimeout () =>
					hide statusBar, options
				, 5000

		ajaxError: (response, options) ->
			errorText = "#{response.status} #{response.statusText}"
			@message errorText, 'error', options

		error: (message, options) ->
			@message message, 'error', options

		success: (message, options) ->
			@message message, 'success', options

		warning: (message, options) ->
			@message message, 'warning', options

		info: (message, options) ->
			@message message, 'info', options

)