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

			defaultConstraints: 
				bottom: 75
				left: 15
				width: 200
				height: 250
				expandedWidth: 400
				expandedHeight: 600

			events:
				'click a[href]:not([href=#]):not([data-action])': (event) -> Events.followLink event
				'click [data-action=mark-as-reviewed]': (event) -> Util.markAsReviewed event, @reviewId
				'click [data-action=open-review]': 'openReview'
				'click [data-action=toggle-size]': 'toggleSize'
				'click [data-action=close-widget]': 'close'
				'mousedown .header-box': 'startDragging'

			openReview: () ->
				Router.navigate	"tasks/review/#{@reviewId}"

			toggleSize: () ->
				if @getVal('width') is @defaultConstraints.width
					@setVal 'width', @defaultConstraints.expandedWidth
					@setVal 'height', @defaultConstraints.expandedHeight
				else
					@setVal 'width', @defaultConstraints.width
					@setVal 'height', @defaultConstraints.height
				@update()

			startDragging: (event) ->
				@dragging = true
				$('body').css('user-select', 'none')
				startX = event.clientX
				startY = event.clientY
				onMouseMove = (event2) =>
					@setVal('left', @getVal('left') + event2.clientX - startX)
					@setVal('bottom', @getVal('bottom') - event2.clientY + startY)
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
						if !@isActive(review)
							@close()
						else
							@$el.html template 
								review: review
								references: Util.byType review.references
							@update()
							Renderer.render @, renderOptions

			isActive: (review) ->
				if review.endDate
					return false
				for assignment in review.assignments
					if !assignment.endDate and assignment.assignedTo.username is currentUser.get('username')
						return true
				return false

			update: () ->
				for key in ['bottom', 'left', 'width', 'height']
					@$el.css key, @getVal(key)
				@$('.review-title').css('width', @getVal('width') - 65)
				actualHeight = @getVal 'height'
				maxHeight = @$('.content-box').outerHeight() + @$('.header-box').outerHeight()
				if maxHeight is 0
					setTimeout () =>
						maxHeight = @$('.content-box').outerHeight() + @$('.header-box').outerHeight()
						if actualHeight > maxHeight
							@$el.css 'height', maxHeight
					, 0
				else if actualHeight > maxHeight
					@$el.css 'height', maxHeight
				@$('[data-action=toggle-size]').removeClass 'glyphicon-resize-full glyphicon-resize-small'
				if @getVal('width') is @defaultConstraints.width
					@$('[data-action=toggle-size]').addClass 'glyphicon-resize-full'
				else
					@$('[data-action=toggle-size]').addClass 'glyphicon-resize-small'

			close: () ->
				LocalStorage.setValue "#{currentUser.get('username')}-active-review-task", null
				@el.remove()
				for key in ['bottom', 'left', 'width', 'height']
					LocalStorage.setValue('review-widget-' + key, null)

			getVal: (key) ->
				val = LocalStorage.getString('review-widget-' + key)
				val = if !val and parseInt(val) isnt 0 then @defaultConstraints[key] else parseInt(val)
				if key is 'left'
					return Math.min(@maxLeft(), Math.max(0, val))
				if key is 'bottom'
					return Math.min(@maxBottom(), Math.max(0, val))
				if (key is 'width' or key is 'height') and val is 0
					return @defaultConstraints[key]
				return val

			setVal: (key, value) ->
				LocalStorage.setValue('review-widget-' + key, Math.max(0, value))

			maxLeft: () ->
				return $(window).width() - @getVal('width')

			maxBottom: () ->
				return $(window).height() - @getVal('height')

)