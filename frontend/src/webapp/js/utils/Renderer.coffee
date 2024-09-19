define () ->

	render: (view, options) ->
		unless options
			return
		if options.append is true
			$(options.container).append view.$el
		else if options.prepend is true
			$(options.container).prepend view.$el
		else if options.before is true
			$(options.container).before view.$el			
		else if options.after is true
			$(options.container).after view.$el			
		else
			$(options.container).html view.$el
		unless options.noAnimation
			view.$el.animateCss 'fadeIn'
		options.callback?()
