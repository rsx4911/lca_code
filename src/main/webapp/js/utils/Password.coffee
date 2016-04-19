define () ->

	DIGITS = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']
	LOWERCASE = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']
	UPPERCASE = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
	SPECIAL = ['!', '@']

	generate: () ->
		until @isStrong(password)
			password = @_generate()
		return password

	_generate: () ->
		chars = []
		for c in DIGITS
			chars.push c
		for c in LOWERCASE
			chars.push c
		for c in UPPERCASE
			chars.push c
		for c in SPECIAL
			chars.push c
		password = ''
		for i in [1..16]
			next = parseInt(Math.random() * chars.length)
			if next is chars.length
				next--
			password += chars[next]
		return password

	isStrong: (password) ->
		unless password
			return false
		unless @checkOccurrences(password, DIGITS, 1)
			return false
		unless @checkOccurrences(password, LOWERCASE, 2)
			return false
		unless @checkOccurrences(password, UPPERCASE, 2)
			return false
		unless @checkOccurrences(password, SPECIAL, 1)
			return false
		return true

	checkOccurrences: (password, list, minimum) ->
		occurrences = 0
		for char in list
			if password.indexOf(char) isnt -1
				occurrences++
		return occurrences >= minimum