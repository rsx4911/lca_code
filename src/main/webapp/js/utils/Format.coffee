define([
				'moment'
				'numeral'
			]

	(moment, numeral) ->

		money: (value, precision = 2) ->
			pattern = '$0,0'
			if precision
				i = 0
				pattern += '.' 
				while i < precision
					pattern += '0'
					i++ 
			return numeral(value).format(pattern).replace '$', '\u20AC'

		date: (value, pattern = 'MM/DD/YYYY') ->
			if value
				return moment(value).format pattern
			return ''

		filesize: (value) ->
			return numeral(value).format '0.0 b'

		phone: (value) ->
			while value.indexOf(' ') isnt -1
				value = value.replace ' ', ''
			while value.indexOf('-') isnt -1
				value = value.replace '-', ''
			return value

)