define([
				'backbone'
				'cs!utils/Events'
			]

	(Backbone, Events) ->

		class Filter extends Backbone.Model

			initialize: (options) ->
				{@url, @container, @template, @filterId, @callback, @type} = options
				@page = 1
				@filter = ''

			init: (callback) ->
				@load (result) =>
					$('#' + @filterId).on 'keyup', (event) => @applyFilter event
					@append result
					callback?(result)

			load: (callback) ->
				url = @url.apply @, [@page, @filter]
				$.get url, (result) => 
					@callback?(@type, result)
					callback.apply @, [result]

			append: (result) ->
				$(@container).html @template result
				$(@container + ' a.page').on 'click', (event) => @applyFilter event

			applyFilter: (event) ->
				Events.preventDefault event
				target = $ Events.target event
				if target.is('input')
					@filter = target.val()
					@page = 1
				else 	
					@page = parseInt target.attr 'data-page'
				@load (result) =>
					@append result

)