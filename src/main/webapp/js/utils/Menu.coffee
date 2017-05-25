define([
				'cs!utils/Events'
			]

	(Events) ->

		init: (element) ->
			menu = $(element)
			@activateActiveBar menu
			@activateHoverBar menu
			return menu

		getX: (menu, elem) ->
			margin = 0
			for li in $('li', menu)
				if li is elem
					break
				margin += $(li).outerHeight true
			return margin

		activateActiveBar: (menu) ->
			bar = $('.menu-active-bar', menu) 
			li = $('li.active', menu)
			margin = @getX menu, li[0]
			height = $(li).height()
			bar.css 'margin', margin + 'px 0 0 0'
			bar.css 'height', height + 'px'

		onClick: (menu, li) ->
			bar = $('.menu-active-bar', menu) 
			bar.css 'transition', 'height .5s ease, margin .3s ease'
			margin = @getX menu, li[0]
			height = $(li).height()
			bar.css 'margin', margin + 'px 0 0 0'
			bar.css 'height', height + 'px'

		activateHoverBar: (menu) ->
			bar = $('.menu-hover-bar', menu) 
			bar.css 'transition', 'height .5s ease, background .5s ease'
			color = bar.css 'background'
			self = @
			$('li', menu).hover () ->
				self.onHover menu, @, color
			menu.mouseleave () ->
				self.onHoverBarLeave menu

		onHover: (menu, li, color) ->
			bar = $('.menu-hover-bar', menu) 
			isOn = bar.prop 'data-on'
			if isOn
				bar.css 'transition', 'height .5s ease, margin .3s ease, background .5s ease'
			bar.prop 'data-on', true
			margin = @getX(menu, li)
			height = $(li).height()
			bar.css 'margin', margin + 'px 0 0 -2px'
			bar.css 'height', height + 'px'
			bar.css 'background', color

		onHoverBarLeave: (menu) ->
			bar = $('.menu-hover-bar', menu) 
			bar.prop 'data-on', false
			bar.css 'transition', 'height .5s ease, background .5s ease'
			bar.css 'height', '0'
			bar.css 'background', 'transparent'
			setTimeout () ->
				isOn = bar.prop 'data-on'
				unless isOn
					bar.css 'margin', '0 0 0 -2px'
			, 500

)