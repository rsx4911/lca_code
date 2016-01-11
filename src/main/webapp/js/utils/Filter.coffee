define([
				'backbone'
				'cs!utils/Events'
			]

	(Backbone, Events) ->

		class Filter extends Backbone.Model

			initialize: (options) ->
				{@url, @container, @template, @filterId} = options
				@page = 1
				@filter = ''

			init: () ->
				$('#' + @filterId).on 'keyup', (event) => @applyFilter event
				@load (result) =>
					@append result

			load: (callback) ->
				url = @url.apply @, [@page, @filter]
				$.get url, (result) => 
					callback.apply @, [result]

			append: (result) ->
				$(@container).html @template
					data: result.data
					page: result.page
					pageCount: Math.ceil(result.subTotal / result.pageSize)
				$(@container + ' .paging a').on 'click', (event) => 
					@applyFilter event

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