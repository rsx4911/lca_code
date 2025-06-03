define([
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Labels'
				'cs!utils/ModelTypes'
				'cs!utils/Roles'
				'cs!models/CurrentUser'
				'templates/views/repository/comments/comment-entry'
			]

	(Events, Format, Labels, ModelTypes, Roles, currentUser, template) ->

		release: (event, renderData) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			commentId = @getCommentId target
			$.ajax
				type: 'PUT'
				url: "ws/comment/#{commentId}/release"
				success: (comment) => @rerender @getContainer(target), comment, renderData

		setVisibility: (event, renderData) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			role = target.attr 'data-role'
			commentId = @getCommentId target
			$.ajax
				type: 'PUT'
				url: "ws/comment/#{commentId}/visibility/#{role}"
				success: (comment) => @rerender @getContainer(target), comment, renderData

		remove: (event, callback) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			commentId = @getCommentId target
			$.ajax
				type: 'DELETE'
				url: "ws/comment/#{commentId}"
				success: () =>
					container = @getContainer target
					if container.next().is('hr')
						container.next().remove()
					else if container.prev().is('hr')
						container.prev().remove()
					container.remove()
					container = $('.comments')
					if container.is(':empty')
						container.prev().remove()
						container.remove()
					callback?(commentId)

		rerender: (container, comment, renderData) ->
			container.replaceWith template
				comment: comment
				roles: renderData.roles
				canComment: renderData.canComment
				canApprove: renderData.canApprove
				canEdit: renderData.canEdit
				currentUser: {username: currentUser.get('username'), admin: currentUser.isAdmin()}
				formatDate: Format.dateTime
				formatModelType: (type) -> return ModelTypes[type]
				isRoleAvailable: Roles.isAvailable
				getRoleLabel: (role) -> return Roles[role].name
				getLabel: (field) -> return Labels.get field.modelType, field.path
			for key in Object.keys(renderData.clickEvents)
				$(key, "[data-comment-id=#{comment.id}]").on 'click', renderData.clickEvents[key]
			renderData.callback?(comment.id, comment)

		getCommentId: (element) ->
			container = @getContainer element
			commentId = parseInt container.attr 'data-comment-id'

		getContainer: (element) ->
			container = element.parent()
			while !container.attr('data-comment-id')
				container = container.parent()
			return container

)