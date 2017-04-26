define([
				'cs!utils/Events'
			]

	(Events) ->

		release: (event, comments) ->
			Events.preventDefault event
			element = $ Events.target event, 'a'
			commentId = @getCommentId element
			$.ajax
				type: 'PUT'
				url: "ws/comment/#{commentId}/release"
				success: (comment) =>
					if comments
						for c in comments
							if c.id is commentId
								c.released = comment.released
								c.approvedBy = comment.approvedBy
					if comment.released && comment.approvedBy
						while !element.hasClass('release-container')
							element = element.parent()
						element.next('.remove').remove()
						parent = element.parent()
						while !parent.hasClass('comment-entry') 
							parent = parent.parent()
						parent.addClass 'released approved'
						element.remove()
					else if comment.released
						parent = element.parent()
						parent.append '<small><i>Approval pending</i></small>'
						while !parent.hasClass('comment-entry') 
							parent = parent.parent()
						parent.addClass 'released'
						element.remove()

		setVisibility: (event, comments) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			role = target.attr 'data-role'
			commentId = @getCommentId element
			$.ajax
				type: 'PUT'
				url: "ws/comment/#{commentId}/visibility/#{role}"
				success: () =>
					comment = null
					if comments
						for c in comments
							if c.id is commentId
								comment = c
					unless comment
						return
					visibility = $ ".modal [data-comment-id=#{commentId}] .comment-visibility"
					visibility.removeClass 'glyphicon-lock glyphicon-globe'
					if role is 'null'
						comment.restrictedToRole = null
						visibility.addClass 'glyphicon-globe'
						visibility.attr 'title', 'Visible to everybody'
					else
						comment.restrictedToRole = role
						visibility.addClass 'glyphicon-lock'
						visibility.attr 'title', 'Only visible for users with role \'' + Roles[comment.restrictedToRole].name + '\' or higher';
					$('.dropdown.open > a').click()

		remove: (event, comments) ->
			Events.preventDefault event
			target = $ Events.target event, 'a'
			commentId = @getCommentId target
			$.ajax
				type: 'DELETE'
				url: "ws/comment/#{commentId}"
				success: () =>
					if comments
						for c, index in comments
							if c.id is commentId
								comments.splice(index, 1);
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

		getCommentId: (element) ->
			container = @getContainer element
			commentId = parseInt container.attr 'data-comment-id'

		getContainer: (element) ->
			container = element.parent()
			while !container.attr('data-comment-id')
				container = container.parent()
			return container

)