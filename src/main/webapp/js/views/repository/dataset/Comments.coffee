define([
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Roles'
				'cs!models/CurrentUser'
			]

	(Events, Format, Layers, LocalStorage, Roles, currentUser) ->

		init: (parent, dataset) ->
			@loadComments dataset, (comments) =>
				for element in $('[data-path]', parent)
					path = $(element).attr 'data-path'
					label = @toLabel path
					title = if path then "Comment '#{label}'" else 'Comment data set'
					visible = LocalStorage.getValue('reviewMode') or comments[path]
					style = if visible then '' else 'style="display:none" '
					highlight = comments[path]
					$(element).append '<img ' + style + 'title="' + title + '" src="images/comment' + (if highlight then '_highlighted' else '') + '.png" data-action="comment"></a>'
				$('[data-path] [data-action=comment]', parent).on 'click', (event) => 
					Events.preventDefault event
					target = $ Events.target event
					while !target.attr('data-path')
						target = target.parent()
					path = target.attr 'data-path'
					unless comments[path]
						comments[path] = []
					@comments = comments
					@showComments dataset, path

		loadComments: (dataset, callback) ->
			$.ajax 
				type: 'GET'
				url: @getUrl(dataset)
				success: (data) =>
					@canComment = data.canComment
					map = {}
					for comment in data.comments
						path = comment.field.path or ''
						unless map[path]
							map[path] = []
						map[path].push comment
					callback map

		getUrl: (dataset) ->
			group = dataset.repository.get 'group'
			name = dataset.repository.get 'name'
			return "ws/comment/#{group}/#{name}/#{dataset.type}/#{dataset.refId}/#{dataset.commitId}"

		showComments: (dataset, path) ->
			comments = @sortAndFilter @comments[path]
			field = @toLabel path
			buttons = []
			buttons.push {text: 'Close', callback: -> Layers.closeActive()}
			if @canComment
				buttons.push {text: 'Add comment', className: 'btn-success', callback: => @addComment dataset, path}
			Layers.showTemplateInLayer
				title: "Comments on '#{field}'"
				template: 'repository/dataset/comment-layer'
				model: 
					path: path
					comments: comments or []
					formatDate: Format.dateTime
					currentUser: {username: currentUser.get('username'), admin: currentUser.isAdmin(), canComment: @canComment}
					roles: Roles.getAll()
					getRoleLabel: (role) -> Roles[role].name
				buttons: buttons
				callback: () =>
					@initSubMenues()
					$('.modal .reply-to').on 'click', (event) => @setReplyTo event
					$('.modal [data-comment-id] .change-visibility a[data-role]').on 'click', (event) => @setVisibility event, path
					$('.modal .new-comment-wrapper .change-visibility a[data-role]').on 'click', (event) => 
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

		setReplyTo: (event) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			textarea = $('.modal #new-comment-group')
			textarea.remove()
			isActive = target.attr 'data-active'
			$('.modal .reply-to[data-active]').removeAttr 'data-active'
			if isActive
				replyTo = null
				$('.new-comment-wrapper').append textarea
			else
				target.attr 'data-active', 'data-active'
				replyTo = target.attr 'data-comment-id'
				$(".comment-entry[data-comment-id=#{replyTo}]").append textarea
			@replyTo = replyTo

		setVisibility: (event, path) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			role = target.attr 'data-role'
			while !target.attr('data-comment-id')
				target = target.parent()
			commentId = parseInt target.attr 'data-comment-id'
			$.ajax
				type: 'PUT'
				url: "ws/comment/#{commentId}/#{role}"
				success: () =>
					visibility = $ ".modal [data-comment-id=#{commentId}] .comment-visibility"
					visibility.removeClass 'glyphicon-lock glyphicon-globe'
					for comment in @comments[path]
						if comment.id is commentId
							if role is 'null'
								comment.restrictedToRole = null
								visibility.addClass 'glyphicon-globe'
								visibility.attr 'title', 'Visible to everybody'
							else
								comment.restrictedToRole = role
								visibility.addClass 'glyphicon-lock'
								visibility.attr 'title', 'Only visible for users with role \'' + Roles[comment.restrictedToRole].name + '\' or higher';
					$('.dropdown.open > a').click()

		addComment: (dataset, path) ->
			text = $('.modal #new-comment').val()
			unless text
				return
			$.ajax
				type: 'POST'
				url: @getUrl(dataset)
				contentType: 'application/json'
				data: JSON.stringify({path: path, text: text, replyTo: @replyTo, restrictedToRole: @role})
				success: (comment) => 
					@comments[path].push comment
					Layers.closeActive()
					Backbone.history.loadUrl()

		toLabel: (path) ->
			if path.indexOf('.') isnt -1
				path = path.substring path.indexOf('.') + 1
			if path.indexOf('[') isnt -1
				path = path.substring 0, path.indexOf('[')
				if path is 'impactCategories'
					path = 'impactCategory'
				else if path is 'processes'
					path = 'process'
				else if path.charAt(path.length - 1) is 's'
					path = path.substring 0, path.length - 1
			result = ''
			for character, index in path
				if index is 0
					result = character.toUpperCase()
				else if character.toLowerCase() is character
					result += character
				else 
					result += ' ' + character.toLowerCase()
			return result

		sortAndFilter: (comments) ->
			comments.sort (a, b) -> return b.date - a.date
			added = []
			sorted = []
			for comment in comments
				if $.inArray(comment.id, added) isnt -1
					continue
				if comment.replyTo 
					continue
				sorted.push comment
				added.push comment.id
				replies = []
				for c in comments
					if c.replyTo and c.replyTo.id is comment.id
						replies.push c
						added.push c.id
				replies.sort (a, b) -> return a.date - b.date
				for reply in replies
					sorted.push reply
			return sorted

)