define([
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Labels'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Roles'
				'cs!views/repository/CommentActions'
				'cs!models/CurrentUser'
			]

	(Router, Events, Format, Labels, Layers, LocalStorage, Roles, Actions, currentUser) ->

		init: (parent, options) ->
			unless currentUser.isLoggedIn()
				return
			@loadComments options, () =>
				for element in $('[data-path]', parent)
					path = $(element).attr 'data-path'
					label = Labels.get options.type, path
					title = if path and path isnt 'null' then "Comment '#{label}'" else 'Comment data set'
					visible = (LocalStorage.getValue('reviewMode') and @canComment) or @comments[path]
					style = if visible then '' else 'style="display:none" '
					highlight = @comments[path]
					unless $('img[data-action=comment]', element).length
						$(element).append '<img ' + style + 'title="' + title + '" src="images/comment' + (if highlight then '_highlighted' else '') + '.png" data-action="comment"></a>'
				$('[data-path] [data-action=comment]', parent).off 'click.comment'
				$('[data-path] [data-action=comment]', parent).on 'click.comment', (event) => 
					Events.preventDefault event
					target = $ Events.target event
					while !target.attr('data-path') and !target.is('body')
						target = target.parent()
					if !target.attr('data-path') and !target.attr('data-path') is ''
						return
					path = target.attr 'data-path'
					unless @comments[path]
						@comments[path] = []
					@showComments options, path
				@openComment options.commentPath

		openComment: (path) ->
			unless path
				return
			@rewriteUrl()
			elem = $("[data-path='#{path}']", '.tab-content')
			if elem?.length
				while !elem.hasClass('tab-pane') and !elem.is('body')
					elem = elem.parent()
				unless elem.is('body')
					tabId = elem.attr 'id'
					$("[href=##{tabId}]").click()
			commentElement = $ "[data-path='#{path}'] [data-action=comment]"
			pos = commentElement.offset().top - 85
			if pos > 0
				$(window).scrollTop pos
			commentElement.click()

		rewriteUrl: () ->
			fragment = Backbone.history.fragment
			query = fragment.substring fragment.lastIndexOf('?') + 1
			parts = query.split '&'
			fragment = fragment.substring 0, fragment.lastIndexOf('?') + 1
			first = true
			for part in parts
				if part.indexOf('commentPath=') is 0
					continue
				unless first
					fragment += '&'
				first = false
				fragment += part
			Router.navigate fragment, {trigger: false, replace: true}

		loadComments: (options, callback) ->
			unless options.loadComments
				if @comments and Object.keys(@comments).length
					callback()
				return
			@comments = {}
			$.ajax 
				type: 'GET'
				url: @getUrl(options)
				success: (data) =>
					@canComment = data.canComment
					@canApprove = data.canApprove
					for comment in data.comments
						path = comment.field.path
						unless path
							path = 'null'
						unless @comments[path]
							@comments[path] = []
						@comments[path].push comment
					callback()

		getUrl: (options) ->
			group = options.repository.get 'group'
			name = options.repository.get 'name'
			return "ws/comment/#{group}/#{name}/#{options.type}/#{options.refId}"

		onEdit: (event) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			@setEdit target

		onReplyTo: (event) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			@setReplyTo target

		onRelease: (event) ->
			if @edit
				@setEdit $('.modal [data-active]')
			else if @replyTo
				@setReplyTo $('.modal [data-active]')
			Actions.release event, @renderData

		onRemove: (event) ->
			if @edit
				@setEdit $('.modal [data-active]')
			else if @replyTo
				@setReplyTo $('.modal [data-active]')
			Actions.remove event, (commentId) =>
				fieldComments = @updateComment commentId
				if fieldComments?.length is 0
					Layers.closeActive()
					Backbone.history.loadUrl()

		onChangeVisibility: (event) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			@role = target.attr 'data-role'
			if @role is 'null'
				@role = null
			visibility = $ '.modal .new-comment-wrapper .comment-visibility'
			visibility.removeClass 'glyphicon-lock glyphicon-globe'
			if @role
				visibility.addClass 'glyphicon-lock'
				visibility.attr 'title', 'Only visible for users with role \'' + Roles[@role].name + '\' or higher';
			else
				visibility.addClass 'glyphicon-globe'
				visibility.attr 'title', 'Visible to everybody'
			$('.dropdown.open > a').click()

		showComments: (options, path) ->
			@renderData = {canComment: @canComment, canApprove: @canApprove, canEdit: true, roles: Roles.getAll(), callback: (commentId, comment) => @updateComment commentId, comment}
			clickEvents = 
				'.edit': (event) => @onEdit event
				'.reply-to': (event) => @onReplyTo event
				'.release': (event) => @onRelease event
				'.remove': (event) => @onRemove event
				'[data-comment-id] .change-visibility a[data-role]': (event) => Actions.setVisibility event, @renderData
				'.new-comment-wrapper .change-visibility a[data-role]': (event) => @onChangeVisibility event
			@renderData.clickEvents = clickEvents
			comments = @sortAndFilter @comments[path]
			if path and path isnt 'null'
				field = Labels.get options.type, path
			buttons = []
			buttons.push {text: 'Close', callback: -> Layers.closeActive()}
			if @canComment
				buttons.push {text: 'Add comment', className: 'btn-success', callback: => @addComment options, path}
			Layers.showTemplateInLayer
				title: if field then "Comments on '#{field}'" else 'Comments on data set'
				template: 'repository/dataset/comment-layer'
				model: 
					path: path
					comments: comments or []
					formatDate: Format.dateTime
					currentUser: {username: currentUser.get('username'), admin: currentUser.isAdmin()}
					canComment: @canComment
					canApprove: @canApprove
					canEdit: true
					roles: Roles.getAll()
					getRoleLabel: (role) -> return Roles[role].name
					getLabel: (field) -> return Labels.get field.modelType, field.path
				buttons: buttons
				callback: () =>
					@initSubMenues()
					for key in Object.keys(clickEvents)
						$('.modal ' + key).on 'click', clickEvents[key]
					$('#new-comment').focus()

		initSubMenues: () ->
			$('.modal .dropdown > .dropdown-menu > li').mouseenter (event) ->
				element = $(@)
				$('.dropdown-submenu .dropdown-menu', element.parent()).hide()
			$('.modal .dropdown-submenu').click (event) -> 
				document.activeElement.blur()
				return false
			$('.modal .dropdown-submenu').mouseenter (event) ->
				element = $(@)
				$('.dropdown-menu:first-of-type', element).show()
				element.parent().parent().on('hide.bs.dropdown', () -> $('.dropdown-menu:first-of-type', element).hide())

		setReplyTo: (target) ->
			@replyTo = @moveTextarea target
			$('.modal-footer button:last-child').html 'Add comment'

		setEdit: (target) ->
			@edit = @moveTextarea target
			if @edit
				$('.modal-footer button:last-child').html 'Edit comment'
				$(".modal [data-comment-id=#{@edit}] .comment-text, .modal #new-comment-group label").hide()
				while !target.attr 'data-comment-id'
					target = target.parent()
				$('.modal #new-comment-group textarea').val($('.comment-text', target).text())
			else
				$('.modal-footer button:last-child').html 'Add comment'

		moveTextarea: (target) ->
			$('.modal .comment-text, .modal #new-comment-group label').show()
			textarea = $('.modal #new-comment-group')
			textarea.remove()
			wasActive = target.attr 'data-active'
			$('.modal [data-active]').removeAttr 'data-active'
			activeId = null
			if wasActive
				$('.modal .new-comment-wrapper').append textarea
			else
				target.attr 'data-active', 'data-active'
				while !target.attr 'data-comment-id'
					target = target.parent()
				activeId = target.attr 'data-comment-id'
				$(".modal .comment-entry[data-comment-id=#{activeId}]").append textarea
			return activeId

		addComment: (options, path) ->
			text = $('.modal #new-comment').val()
			unless text
				return
			fieldPath = if path is 'null' then '' else path
			$.ajax
				type: if @edit then 'PUT' else 'POST'
				url: if @edit then "ws/comment/#{@edit}" else @getUrl(options) + '/' + options.commitId
				contentType: 'application/json'
				data: JSON.stringify({path: fieldPath, text: text, replyTo: @replyTo, restrictedToRole: @role})
				success: (comment) => 
					if @edit
						@updateComment comment.id, comment
						$(".modal [data-comment-id=#{comment.id}] .comment-text").html comment.text
						@setEdit $('.modal [data-active]')
						$('.modal #new-comment-group textarea').val('')
					else
						@comments[path].push comment
						@replyTo = null
						Layers.closeActive()
						Backbone.history.loadUrl()

		updateComment: (commentId, comment) ->
			path = null
			newComments = []
			for key in Object.keys(@comments)
				for c in @comments[key]
					if c.id is commentId
						path = key
			unless path
				return []
			for c in @comments[path]
				if c.id is commentId
					if comment
						newComments.push comment
				else
					newComments.push c
			@comments[path] = newComments
			return newComments

		sortAndFilter: (comments) ->
			comments.sort (a, b) -> return b.date - a.date
			added = []
			sorted = []
			for comment in comments
				if $.inArray(comment.id, added) isnt -1
					continue
				unless comment.replyTo 
					sorted.push comment
					added.push comment.id
				replies = []
				for c in comments
					if c.replyTo is comment.id
						replies.push c
						added.push c.id
				replies.sort (a, b) -> return a.date - b.date
				for reply in replies
					sorted.push reply
			return sorted

)