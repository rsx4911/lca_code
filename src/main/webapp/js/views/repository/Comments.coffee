define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Format'
				'cs!utils/Labels'
				'cs!utils/ModelTypes'
				'cs!utils/Renderer'
				'cs!views/repository/CommentActions'
				'cs!models/CurrentUser'
				'templates/views/repository/comments/comments'
				'templates/views/repository/comments/comment-list'
			]

	(Backbone, Events, Filter, Format, Labels, ModelTypes, Renderer, Actions, currentUser, template, listTemplate) ->

		class CommentsView extends Backbone.View

			className: 'comments-view'

			events: 
				'click a[href]:not([href=#])': (event) -> Events.followLink event
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
					callback: (result) =>
						result.repository = {group: group, name: name}
						result.formatDate = Format.dateTime
						result.currentUser = {username: currentUser.get('username'), admin: currentUser.isAdmin()}
						result.canApprove = result.resultInfo.canApprove
						result.formatModelType = (type) -> return ModelTypes[type]
						result.getLabel = (field) -> return Labels.get field.modelType, field.path
						@setRenderData result.resultInfo

			render: (renderOptions) ->
				@$el.html template
				Renderer.render @, renderOptions
				@filter.init()

			setRenderData: (resultInfo) ->
				@renderData = {canApprove: resultInfo.canApprove, canComment: false}
				clickEvents = 
					'a[href]:not([href=#])': (event) => Events.followLink event
					'a.release': (event) => Actions.release event, @renderData
					'a.remove': (event) => Actions.remove event
				@renderData.clickEvents = clickEvents

)