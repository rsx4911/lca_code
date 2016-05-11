define () ->

	formatCommitDescription: (text) ->
		if text.length < 100
			return text
		space = -1
		while text.indexOf(' ', space + 1) < 100 and text.indexOf(' ', space + 1) isnt -1
			space = text.indexOf(' ', space + 1)
		if space is -1
			return text.substring(0, 100) + '...'
		return text.substring(0, space) + '...'