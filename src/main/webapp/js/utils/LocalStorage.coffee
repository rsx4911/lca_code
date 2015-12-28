define () ->

	temp = []

	initialize: () ->
		unless localStorage.getItem('nexus-permanent') 
			localStorage.setItem 'nexus-permanent', {}

	set: (key, value, permanent = false) ->		
		if permanent
			if $.isPlainObject(value)
				value = 'json:' + JSON.stringify value
			else
				value = 'plain:' + value
			localStorage.getItem('nexus-permanent')[key] = value
		else
			temp[key] = value

	get: (key) ->
		value = temp[key]
		if value
			return value
		storage = localStorage.getItem 'nexus-permanent'
		value = storage?[key]
		if value
			type = value.substring 0, value.indexOf(':')
			value = value.substring(value.indexOf(':') + 1)
			if type is 'json'
				value = JSON.parse value
			else if type is 'plain'
				value = value
			else
				value = null
		return value

	remove: (key) ->
		delete temp[key]
		delete localStorage.getItem('nexus-permanent')?[key]

	_: (callback) ->
		() =>
			callback.apply @, arguments