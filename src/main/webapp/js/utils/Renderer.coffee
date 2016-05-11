define () ->

	getRenderOptions = (type, collection, element) ->
		index = collection.indexOf element
		index++
		if collection.length is 1
			renderOptions =
				container: $ ".#{type}s"
				append: true
		else if index is collection.length
			renderOptions =
				container: $(".#{type}-view:last-child()")
				after: true				
		else 
			renderOptions =
				container: $(".#{type}-view:nth-child(#{index})")
				before: true				
		return renderOptions

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
		options.callback?()

	_: (callback) ->
		() =>
			callback.apply @, arguments
