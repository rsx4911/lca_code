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

			init: () ->
				$('#' + @filterId).on 'keyup', (event) => @applyFilter event
				@load (result) =>
					@append result

			load: (callback) ->
				url = @url.apply @, [@page, @filter]
				$.get url, (result) => 
					if @callback
						@callback @type, result
					callback.apply @, [result]

			append: (result) ->
				model = result
				model.pageCount = Math.ceil(result.subTotal / result.pageSize)
				$(@container).html @template model
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