define([
				'moment'
			] 

	(moment) ->

		apply = (value, format) ->
			unless value 
				return ''
			return moment(value).format format

		formatCommitDescription: (text) ->
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

		date: (value) -> 
			return apply value, 'M/D/YY'

		time: (value) -> 
			return apply value, 'h:mm a'

		dateTime: (value) -> 
			return apply value, 'M/D/YY h:mm a'

		timeOrDate: (value) -> 
			if moment(value).isBefore(new Date(), 'day')
				return apply value, 'M/D/YY'
			return apply value, 'h:mm a'

		dateOrTime: (value) -> 
			if moment(value).isBefore(new Date(), 'day')
				return apply value, 'h:mm a'
			return apply value, 'M/D/YY'

		moment: (value, format) ->
			unless value 
				return ''
			return moment(value).format format

)