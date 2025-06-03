define([
				'cs!utils/Events'
			]

	(Events) ->

		init: (container) ->
			$('.toggle-control', container).on 'click', (event) -> 
				Events.preventDefault event
				parent = $(Events.target(event)).parent()
				$('> .toggle-control, > .toggleable, > table > tbody > tr.toggleable', parent).toggle()

)