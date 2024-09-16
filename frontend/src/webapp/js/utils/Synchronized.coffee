define () ->

	forEach: (asynchronousAction, elements, callback) ->
		unless elements.length
			callback?()
		else
			finish = () =>
				@forEach asynchronousAction, elements[1..], callback
			asynchronousAction elements[0], finish
