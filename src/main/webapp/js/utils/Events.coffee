define () ->

		setRouter: (router) ->
			# otherwise cyclic reference
			# this is called in the controller initialize method
			@router = router

		preventDefault: (event) ->
			if event
				if event.preventDefault
					event.preventDefault()
				else
					event.returnValue = false

		listenToAnchorClicks: (container, onComplete) ->
			$('a[href][data-follow]', container).on 'click', (event) =>
				@followLink event
				onComplete?()

			$('[data-route][data-follow]', container).on 'click', (event) =>
				@followRoute event
				onComplete?()

		keyCode: (event) ->
			return event.keyCode or event.which

		target: (event, expectedType) ->
			target = event.target or event.srcElement or event.originalTarget
			if expectedType
				until $(target).is expectedType or $(target).is 'body' # prevent errors
					target = target.parentElement
			return target

		followLink: (event) ->
			link = $ @target event, 'a'
			route = link.attr 'href'
			if route.indexOf('callto:') isnt 0 and route.indexOf('mailto:') isnt 0
				@preventDefault event
				if route.charAt(0) is '/'
					route = route.substring 1
				@router.navigate route
				return route
			return ''

		followRoute: (event) ->
			link = $ @target event, 'button'
			route = link.attr 'data-route'
			if route.indexOf('callto:') isnt 0 and route.indexOf('mailto:') isnt 0
				@preventDefault event
				if route.charAt(0) is '/'
					route = route.substring 1
				@router.navigate route
				return route
			return ''