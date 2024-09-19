define () ->

	getEntry = (entries, indicator) ->
		unless entries
			return null
		entries = entries.substring(1, entries.length - 1)
		entries = entries.split ';'
		return entries[indicator.position - 1]

	getColor: (system, indicator, score, entries) ->
		total = indicator.scores.length
		index = score.position
		entry = getEntry entries, indicator
		o = 0.75
		if (index == 0 or (entry and parseInt(entry) isnt score.position))
			return "rgb(255, 255, 255)"
		if (index == 1)
			return "rgba(125, 250, 125, #{o})"
		if (index == total)
			return "rgba(250, 125, 125, #{o})"
		median = total / 2 + 1
		if index is median
			return "rgba(250, 250, 125, #{o})"
		if index < median
			divisor = median - 1
			factor = index - 1
			r = parseInt(125 + (125 * factor / divisor))
			return "rgba(#{r}, 250, 125, #{o})"
		divisor = median - 1;
		factor = index - median;
		g = parseInt(250 - (125 * factor / divisor))
		return "rgba(250, #{g}, 125, #{o})"