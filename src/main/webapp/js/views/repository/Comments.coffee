define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Labels'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!views/repository/CommentActions'
				'cs!models/CurrentUser'
				'templates/views/repository/comments'
			]

	(Backbone, Events, Format, Labels, ModelTypes, Renderer, Actions, currentUser, template) ->

		class CommentsView extends Backbone.View

			className: 'comments-view'

			events: 
				'click a[href]:not([href=#])': (event) -> Events.followLink event
				'click a.release': (event) -> Actions.release event, @renderData
				'click a.remove': (event) -> Actions.remove event, @renderData

			initialize: (options) ->
				{@repository} = options

			render: (renderOptions) ->
				@loadComments (data) =>
					@renderData = {canApprove: data.canApprove, canComment: false}
					@$el.html template 
						comments: @sortAndFilter data.comments
						canApprove: data.canApprove
						formatDate: Format.dateTime
						currentUser: {username: currentUser.get('username'), admin: currentUser.isAdmin()}
						formatModelType: (type) -> return ModelTypes[type]
						getLabel: (field) -> return Labels.get field.modelType, field.path
					Renderer.render @, renderOptions

			loadComments: (callback) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				$.ajax 
					type: 'GET'
					url: "ws/comment/#{group}/#{name}"
					success: (data) =>
						callback data

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