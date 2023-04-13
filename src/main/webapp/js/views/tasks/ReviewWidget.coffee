define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/LocalStorage'
				'cs!utils/Renderer'
				'cs!views/tasks/Util'
				'cs!models/CurrentUser'
				'templates/views/tasks/review-widget'
			]

	(Backbone, Router, Events, LocalStorage, Renderer, Util, currentUser, template) ->

		class ReviewWidget extends Backbone.View

			className: 'review-widget'

			events:
				'click a[href]:not([href="#"]):not([data-action])': (event) -> Events.followLink event
				'click [data-action=mark-as-reviewed]': (event) -> Util.markAsReviewed event, @reviewId
				'click [data-action=open-review]': 'openReview'
				'click [data-action=filter-checked]': 'filterChecked'
				'click [data-action=toggle-size]': 'toggleSize'
				'click [data-action=close-widget]': 'close'
				'mousedown .header-box': 'startDragging'

			openReview: () ->
				Router.navigate	"tasks/review/#{@reviewId}"

			filterChecked: () ->
				if @$('.content-box').hasClass 'filtered'
					@$('.content-box').removeClass 'filtered'
					@$('.glyphicon-filter').css('text-decoration', 'initial')
				else
					@$('.content-box').addClass 'filtered'
					@$('.glyphicon-filter').css('text-decoration', 'line-through')
				@update()

			startDragging: (event) ->
				@dragging = true
				$('body').css('user-select', 'none')
				startX = event.clientX
				startY = event.clientY
				onMouseMove = (event2) =>
					@setVal('left', @left() + event2.clientX - startX)
					@setVal('top', @top() + event2.clientY - startY)
					@update()
					startX = event2.clientX
					startY = event2.clientY
				$(window).on 'mousemove', onMouseMove
				onMouseUp = () =>
					$(window).off 'mousemove', onMouseMove
					$(window).off 'mouseup', onMouseUp
					$('body').css('user-select', 'initial')
					@dragging = false
				$(window).on 'mouseup', onMouseUp
					
			initialize: (options) ->
				{@reviewId} = options
				onResize = (e) =>
					unless $('.review-widget')?.length
						$(window).off 'resize', onResize
						return
					@update()
				$(window).on 'resize', onResize

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/task/review/' + @reviewId
					success: (review) =>
						unless review.references?.length
							@close()
						else
							@$el.html template 
								review: review
								references: Util.byType review.references
							@update() # height calculation depends on element being rendered once, so call update a second time after rendering
							setTimeout () => 
								@update()
							, 0
							Renderer.render @, renderOptions

			update: () ->
				@$el.css 'top', @top()
				@$el.css 'left', @left()
				@$el.css 'width', @width()
				@$el.css 'height', @height()
				@$('.review-title').css('width', @width() - 80)
				@$('[data-action=toggle-size]').removeClass 'glyphicon-resize-full glyphicon-resize-small'
				if LocalStorage.getValue('review-widget-full-size')
					@$('[data-action=toggle-size]').addClass 'glyphicon-resize-small'
				else
					@$('[data-action=toggle-size]').addClass 'glyphicon-resize-full'

			close: () ->
				LocalStorage.setValue "#{currentUser.get('username')}-active-review-task", null
				@el.remove()
				for key in ['top', 'left', 'full-size']
					LocalStorage.setValue('review-widget-' + key, null)

			getVal: (key) ->
				return LocalStorage.getString('review-widget-' + key)

			setVal: (key, value) ->
				LocalStorage.setValue('review-widget-' + key, Math.max(0, value))

			isFullSize: () ->
				return LocalStorage.getValue 'review-widget-full-size'

			toggleSize: () ->
				LocalStorage.setValue('review-widget-full-size', !@isFullSize())
				@update()

			top: () ->
				val = @getVal 'top'
				if !val and parseInt(val) isnt 0
					val = $(window).height() - 75 - @height()
				else
					val = parseInt val
				return Math.max(0, Math.min(val, $(window).height() - @height()))

			left: () ->
				val = @getVal 'left'
				if !val and parseInt(val) isnt 0
					val = 15
				else
					val = parseInt val
				return Math.max(0, Math.min(val, $(window).width() - @width()))

			width: () ->
				val = if @isFullSize() then 400 else 200
				return Math.min(val, $(window).width())

			height: () ->
				val = if @isFullSize() then 600 else 250
				val = Math.min(val, $(window).height())
				if !@isFullSize
					return val
				content = @$('.content-box')
				contentHeight = content[0].scrollHeight
				for prop in ['margin-top', 'margin-bottom', 'border-top-width', 'border-bottom-width'] 
					contentHeight += parseInt content.css(prop)
				fitToContent = contentHeight + @$('.header-box').outerHeight()
				if fitToContent isnt 0 and val > (fitToContent + 2)
					return fitToContent + 2
				return val

)