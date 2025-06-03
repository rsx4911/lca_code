define([
				'moment'
			] 

	(moment) ->

		timeFormat = 'h:mm a'
		dateFormat = 'MM/DD/YYYY'
		dateTimeFormat = 'MM/DD/YYYY h:mm a'

		apply = (value, format) ->
			unless value 
				return ''
			if value.length is 16 and value[10] is '+'
				value = value.substring 0, 10
			if format is dateTimeFormat and value.length is 10
				format = dateFormat
			if value.length is 16 and value[10] is '-' 
				if format is dateTimeFormat
					return moment(value, 'YYYY-MM-DD-HH:mm').format format
				value = value.substring 0, 10
			try
				return moment(value).format format
			catch e
				unless format is dateTimeFormat
					return 'Invalid date'
				try
					if value.length > 10
						value = value.substring 0, 10
					return moment(value).format dateFormat
				catch e2
					return 'Invalid date'

		commitDescription: (text) ->
			if text.length < 100
				return text
			space = -1
			while text.indexOf(' ', space + 1) < 100 and text.indexOf(' ', space + 1) isnt -1
				space = text.indexOf(' ', space + 1)
			if space is -1
				return text.substring(0, 100) + '...'
			return text.substring(0, space) + '...'

		number: (value, round) ->
			unless round
				return value
			return Math.round(value * 1000) / 1000

		relative: (value, precision = 2) ->
			isNegative = value < 0
			value = Math.abs value
			count = 0
			while count < precision
				value *= 10
				count++
			value = Math.round value
			if count > 0
				for i in [1..count]
					value /= 10
			if isNegative
				value *= -1
			return value

		scientific: (value, round) ->
			if not value and value isnt 0
				return ''
			# TODO use math lib
			if round and value isnt 0
				count = 0
				isNegative = value < 0
				value = Math.abs value
				while value < 1000
					value *= 10
					count++
				value = Math.round value
				if count > 0
					for i in [1..count]
						value /= 10
				if isNegative
					value *= -1
			value = value.toExponential()
			corrected = ''
			foundE = false
			minLength = if isNegative then 6 else 5
			for char, index in value
				if char is 'e'
					foundE = true
				if index <= minLength or foundE
					if foundE and corrected.length <= minLength
						if corrected.indexOf('.') is -1
							corrected += '.'
						for i in [corrected.length..minLength]
							corrected += '0'
					corrected += char
			return corrected

		date: (value) -> 
			return apply value, dateFormat

		time: (value) -> 
			return apply value, timeFormat

		dateTime: (value) -> 
			return apply value, dateTimeFormat

		timeOrDate: (value) -> 
			if moment(value).isBefore(new Date(), 'day')
				return apply value, dateFormat
			return apply value, timeFormat

		dateOrTime: (value) -> 
			if moment(value).isBefore(new Date(), 'day')
				return apply value, timeFormat
			return apply value, dateFormat

		moment: (value, format) ->
			return apply value, format

)