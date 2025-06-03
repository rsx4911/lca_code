define () ->

	getValue: (key) ->
		localStorage?.getItem?(key) is 'true' or window[key] is 'true'

	setValue: (key, value) ->
		if localStorage and localStorage.getItem and localStorage.setItem
			if !value and value isnt 0
				localStorage.removeItem key
			else
				localStorage.setItem key, value.toString()
		else
			if !value and value isnt 0
				delete window[key]
			else
				window[key] = value.toString()

	toggleValue: (key) ->
		value = false
		if localStorage and localStorage.getItem and localStorage.setItem
			value = !(localStorage.getItem(key) is 'true')
			localStorage.setItem key, value.toString()
		else 
			value = !(window[key] is 'true')
			window[key] = value.toString()
		return value

	getString: (key) ->
		localStorage?.getItem?(key) or window[key]
