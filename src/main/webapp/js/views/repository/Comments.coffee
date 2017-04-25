define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Labels'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'templates/views/repository/comments'
			]

	(Backbone, Events, Format, Labels, ModelTypes, Renderer, template) ->

		class CommentsView extends Backbone.View

			className: 'comments-view'

			events: 
				'click a[href]:not([href=#])': (event) -> Events.followLink event

			initialize: (options) ->
				{@repository} = options

			render: (renderOptions) ->
				@loadComments (comments) =>
					@$el.html template 
						comments: comments
						formatDate: Format.dateTime
						formatModelType: (type) -> return ModelTypes[type]
						getLabel: (path) -> return Labels.get path
					Renderer.render @, renderOptions

			loadComments: (callback) ->
				group = @repository.get 'group'
				name = @repository.get 'name'
				$.ajax 
					type: 'GET'
					url: "ws/comment/#{group}/#{name}"
					success: (data) =>
						callback @sortAndFilter data.comments

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