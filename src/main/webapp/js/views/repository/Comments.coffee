define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Labels'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!utils/Toggle'
				'cs!views/repository/CommentActions'
				'cs!models/CurrentUser'
				'templates/views/repository/comments/comments'
				'templates/views/repository/comments/comment-list'
			]

	(Backbone, Events, Filter, Format, Labels, ModelTypes, Renderer, Toggle, Actions, currentUser, template, listTemplate) ->

		class CommentsView extends Backbone.View

			className: 'comments-view'

			events: 
				'click a[href]:not([href="#"])': (event) -> Events.followLink event
				'click a.release': (event) -> Actions.release event, @renderData
				'click a.remove': (event) -> Actions.remove event

			initialize: (options) ->
				group = options.repository.get 'group'
				name = options.repository.get 'name'
				@filter = new Filter
					container: '.comments-view .content-box'
					template: listTemplate
					filterId: 'filter'
					url: "ws/comment/#{group}/#{name}?"
					beforeRender: (result) =>
						result.repository = {group: group, name: name}
						result.formatDate = Format.dateTime
						result.currentUser = {username: currentUser.get('username'), admin: currentUser.isAdmin()}
						result.canApprove = result.resultInfo.canApprove
						result.formatModelType = (type) -> return ModelTypes[type]
						result.getLabel = (field) -> return Labels.get field.modelType, field.path
						@setRenderData result.resultInfo
					afterRender: (result) =>
						Toggle.init @$el
						@$('.comment-entry[data-comment-id] a.toggle-control').on 'click', (event) =>
							Events.preventDefault event
							target = $ Events.target event
							while !target.attr('data-comment-id')
								target = target.parent()
							unless $('.replies .toggleable .comment-entry', target).length
								@loadReplies target.attr('data-comment-id'), result.resultInfo.canApprove

			render: (renderOptions) ->
				@$el.html template
				Renderer.render @, renderOptions
				@filter.init()

			setRenderData: (resultInfo) ->
				@renderData = {canApprove: resultInfo.canApprove, canComment: false}
				clickEvents = 
					'a[href]:not([href="#"])': (event) => Events.followLink event
					'a.release': (event) => Actions.release event, @renderData
					'a.remove': (event) => Actions.remove event
				@renderData.clickEvents = clickEvents

			loadReplies: (commentId, canApprove) ->
				$.ajax
					type: 'GET'
					url: "ws/comment/#{commentId}/replies"
					success: (replies) =>
						@$(".comment-entry[data-comment-id=#{commentId}] .replies .toggleable").html listTemplate 
							data: replies
							formatDate: Format.dateTime
							currentUser: {username: currentUser.get('username'), admin: currentUser.isAdmin()}
							canApprove: canApprove
							formatModelType: (type) -> return ModelTypes[type]
							getLabel: (field) -> return Labels.get field.modelType, field.path

)