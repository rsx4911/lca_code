define([
				'backbone'
				'cs!utils/Events'
			]

	(Backbone, Events) ->

		class Filter

			constructor: (options) ->
				{@url, @container, @template, @filterId, @beforeRender, @type, @pageSize, @pageSizeId, @afterRender, @filterPrefix} = options
				if options.noPaging
					@page = 0
				else
					@page = 1
					unless @pageSize
						@pageSize = 10
				@filter = @filterPrefix || ''

			init: (callback) ->
				timer = { time: new Date().getTime() }
				@load (result) =>
					$('#' + @filterId).on 'blur', (event) => 
						if $('#' + @filterId).val() isnt @filter
							@applyFilter event
					$('#' + @filterId).on 'keydown', (event) =>
						if $('#' + @filterId).val() isnt @filter and Events.keyCode(event) is 13
							@applyFilter event
						else 
							timer.time = new Date().getTime()
							setTimeout(() =>
								time = new Date().getTime() - timer.time
								if time >= 333 and $('#' + @filterId).val() isnt @filter
									@applyFilter event
							, 334)
					@append result
					callback?(result)

			getUrl: () ->
				url = @url
				if $.isFunction(@url)
					url = @url()
				first = true
				if @page or @page is 0
					url += if first then '' else '&'
					url += 'page=' + @page
					first = false
				if @pageSize or @pageSize is 0
					url += if first then '' else '&'
					url += 'pageSize=' + @pageSize
					first = false
				if @filter
					url += if first then '' else '&'
					url += 'filter=' + encodeURIComponent(@filter)
					first = false
				if first
					url = url.substring 0, url.length - 1
				return url

			load: (callback) ->
				if @loading
					if @waiting
						return
					@waitForLoading callback
					return
				@loading = true
				url = @getUrl()
				$.get url, (result) => 
					@data = result.data
					@beforeRender?(result, @type)
					callback.apply @, [result]
					@loading = false

			waitForLoading: (callback) ->
				if @loading
					@waiting = true
					setTimeout () =>
						@waitForLoading callback
					, 100
					return
				@waiting = false
				@load callback


			append: (result) ->
				if result
					result.pageSizeId = @pageSizeId
				$(@container).html @template result
				$(@container + ' a.page').on 'click', (event) => @applyFilter event
				$(@container + ' #' + (@pageSizeId || 'page-size')).on 'change', (event) => @applyFilter event
				@afterRender?(result)

			applyFilter: (event) ->
				if event # if null it means the filter was triggered from outsite
					Events.preventDefault event
					target = $ Events.target event
					if target.is('input')
						@filter = (@filterPrefix || '') + target.val()
						@page = 1
					else if target.is('select')
						@page = 1
						@pageSize = parseInt target.val()
					else 	
						@page = parseInt target.attr 'data-page'
				@load (result) =>
					@append result

)