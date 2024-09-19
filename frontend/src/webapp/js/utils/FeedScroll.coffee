define () ->

		class FeedScroll

			constructor: (options) ->
				{@url, @container, @template, @extendModel, @page, @pageSize, @afterRender, @onEmpty} = options
				@id = 'feed-scroll-' + (Math.random() * Math.pow(100, 8)).toString().replace('.', '') 
				@eventName = 'scroll.' + @id
				@page = @page or 1
				@pageSize = @pageSize or 10

			init: () ->
				if $(@container).is(':offscreen')
					setTimeout () =>
						@init()
					, 10
					return
				$(@container).append('<div id="' + @id + '"></div>')
				$(@container).append('<div id="' + @id + '-anchor"></div>')
				@results = $('#' + @id, $(@container))
				@loadInitial()
			
			bottomAnchor: () ->
				return $('#' + @id + '-anchor', $(@container))

			loadInitial: () ->
				@loadNext (result) =>
					@append result
					if result.resultInfo.totalCount is 0
						@onEmpty?()
						return
					wasLastPage = result.resultInfo.currentPage is result.resultInfo.pageCount
					bottomAnchor = @bottomAnchor()
					if bottomAnchor.length and !bottomAnchor.is(':offscreen') and !wasLastPage
						@loadInitial()
						return
					unless wasLastPage
						$(window).on @eventName, () => @onScroll()

			getUrl: () ->
				url = @url
				if $.isFunction(@url)
					url = @url()
				return url + 'page=' + @page + '&pageSize=' + @pageSize

			onScroll: () ->
				if @scrollEventRunning
					return
				# remove listener if element was removed from DOM
				if (!$(@container).closest(document.documentElement).length)
					$(window).off @eventName
					return
				bottomAnchor = @bottomAnchor()
				if bottomAnchor.length and !bottomAnchor.is(':offscreen')
					@scrollEventRunning = true
					@loadNext (result) => 
						@append result
						if result.resultInfo.currentPage is result.resultInfo.pageCount
							$(window).off @eventName			
						@scrollEventRunning = false

			loadNext: (callback) ->
				if @loading
					if @waiting
						return
					@waitForLoading callback
					return
				@loading = true
				url = @getUrl()
				$.get url, (result) => 
					callback.apply @, [result]
					@page = @page + 1
					@loading = false

			waitForLoading: (callback) ->
				if @loading
					@waiting = true
					setTimeout () =>
						@waitForLoading callback
					, 100
					return
				@waiting = false
				@loadNext callback

			append: (result) ->
				for entry in result.data
					@extendModel entry
					@results.append @template entry 
					@afterRender?(entry)

			destroy: () ->
				$(@container).empty()
				$(window).off @eventName