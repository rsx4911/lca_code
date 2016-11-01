define([
				'moment'
			] 

	(moment) ->

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
			unless value
				return ''
			return moment(value).format 'M/D/YYYY'

		dateTime: (value) -> 
			unless value
				return ''
			return moment(value).format 'M/D/YYYY h:mm a'

		dayOrTime: (value) -> 
			unless value
				return ''
			if moment(value).isBefore(new Date(), 'day')
				return moment(value).format 'M/D/YY'
			return moment(value).format 'h:mm a'

)