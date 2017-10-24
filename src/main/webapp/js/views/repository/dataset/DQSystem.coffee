define([
				'cs!utils/Layers'
			]
	(Layers) ->

		init: (dataset) ->
			$('table.data-quality td').on 'click', (event) ->
			target = $ Events.target event
			span = $ 'span', target
			if span.css('display') is 'none'
				iIndex = target.attr('data-indicator') - 1
				sIndex = target.attr('data-score') - 1
				indicator = dataset.indicators[iIndex]
				score = indicator.scores[sIndex]
				iName = if indicator.name then indicator.name else indicator.position
				sName = if score.label then score.label else score.position
				Layers.showMessageInLayer
					title: "#{iName} - #{sName}"
					body: score.description

)