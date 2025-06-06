define([
				'backbone'
				'cs!app/Controller'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/LocalStorage'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!views/tasks/Util'
				'cs!views/tasks/ReviewWidget'
				'cs!models/CurrentUser'
				'templates/views/tasks/manage-review'
			]

	(Backbone, Controller, Router, Events, Format, Forms, Layers, LocalStorage, Renderer, Status, Util, ReviewWidget, currentUser, template) ->

		class CreateReviewView extends Backbone.View

			openWidget: (event) ->
				LocalStorage.setValue "#{currentUser.get('username')}-active-review-task", @reviewId
				Controller.initializeReviewWidget()

			createTask: (event) ->
				Events.preventDefault event
				review = Forms.toJson 'review-form'
				$.ajax
					type: if @reviewId then 'PUT' else 'POST'
					url: 'ws/task/review' + (if @reviewId then '/' + @reviewId else '')
					contentType: 'application/json'
					data: JSON.stringify(review)
					success: (review) => 
						if @reviewId
							Status.success 'Successfully updated review task'
							Backbone.history.loadUrl()
						else
							Router.navigate 'tasks'
					error: (response) -> Forms.handleError 'review-form', response

			assignTask: (event) ->
				Events.preventDefault event
				assignedUsers = []
				for assignment in @review.assignments
					if assignment.endDate
						continue
					assignedUsers.push assignment.assignedTo
				Layers.selectUser
					title: 'Assign user to task'
					module: 'review'
					repository: @review.repositoryPath
					exclude: assignedUsers
					excludeSelf: true
					teams: false
					callback: (selection) =>
						taskId = @reviewId
						$.ajax
							type: 'PUT'
							url: "ws/task/review/#{taskId}/assign/#{selection.id}"
							success: (response) =>
								@userMenu.updateNoOfTasks response.activeTasks
								Backbone.history.loadUrl()

			cancel: (event) ->
				Events.preventDefault event
				target = $ Events.target event, 'button'
				user = target.attr('data-username') or ''
				displayName = target.attr 'data-user-displayname'
				taskId = @reviewId
				forUser = if displayName then " for #{displayName}" else ''
				urlPart = if user then "/#{user}" else ''
				Layers.askQuestion
					title: 'Cancel task' + forUser
					question: 'Do you really want to cancel this task' + forUser
					type: 'danger'
					answers: ['Cancel', 'Confirm']
					onAnswer: (answer) =>
						if answer isnt 1
							return
						$.ajax
							type: 'PUT'
							url: "ws/task/review/#{taskId}/cancel#{urlPart}"
							success: (response) => 
								if Controller.reviewWidget and Controller.reviewWidget.reviewId is @reviewId
									Controller.reviewWidget.close()
								@userMenu.updateNoOfTasks response.activeTasks
								Router.navigate 'tasks'

			complete: (event) ->
				Events.preventDefault event
				target = $ Events.target event, 'button'
				user = target.attr('data-username') or ''
				taskId = @reviewId
				urlPart = if user then "/#{user}" else ''
				$.ajax
					type: 'PUT'
					url: "ws/task/review/#{taskId}/complete#{urlPart}"
					success: (response) =>
						if Controller.reviewWidget and Controller.reviewWidget.reviewId is @reviewId
							Controller.reviewWidget.close()
						@userMenu.updateNoOfTasks response.activeTasks
						Router.navigate 'tasks'

			selectReferences: (event) ->
				Events.preventDefault event
				taskId = @reviewId
				Layers.selectModel
					repositoryPath: @review.repositoryPath
					multipleSelection: true
					callback: (selection) -> 
						Layers.showProgressIndicator 'Updating...'
						$.ajax
							type: 'PUT'
							url: "ws/task/review/#{taskId}/references"
							contentType: 'application/json'
							data: JSON.stringify(selection)
							success: (response) ->
								Layers.closeActive()
								Status.success 'Successfully updated review task'
								Backbone.history.loadUrl()
								Layers.hideProgressIndicator()

			className: 'tasks-view multi-box-view'

			events: 
				'click a[href]:not([href="#"]):not([data-action])': (event) -> Events.followLink event
				'click [data-action=open-widget]': 'openWidget'
				'click [data-action=create-task]': 'createTask'
				'click [data-action=assign-task]': 'assignTask'
				'click [data-action=cancel-assignment]': 'cancel'
				'click [data-action=complete-assignment]': 'complete'
				'click [data-action=cancel-task]': 'cancel'
				'click [data-action=complete-task]': 'complete'
				'click [data-action=select-references]': 'selectReferences'
				'click [data-action=mark-as-reviewed]': (event) -> Util.markAsReviewed event, @reviewId
				
			initialize: (options) ->
				{@reviewId, @userMenu} = options

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/repository?page=0&module=REVIEW'
					success: (repositories) =>
						selectable = ['']
						for repo in repositories
							repoId = "#{repo.group}/#{repo.name}"
							selectable.push [repoId, repo.label]
						if @reviewId
							$.ajax
								type: 'GET'
								url: 'ws/task/review/' + @reviewId
								success: (review) =>
									@doRender selectable, review, renderOptions
									Forms.fill 'review-form', review
						else
							@doRender selectable, null, renderOptions

			doRender: (repositories, review, renderOptions) ->
				@review = review
				activeAssignments = []
				completedAssignments = []
				canceledAssignments = []
				if review?.assignments
					for assignment in review.assignments
						if !assignment.endDate
							activeAssignments.push assignment
						else if assignment.canceled
							canceledAssignments.push assignment
						else
							completedAssignments.push assignment
				@sort activeAssignments, 'startDate'
				@sort completedAssignments, 'endDate'
				@sort canceledAssignments, 'endDate'
				@$el.html template
					repositories: repositories
					review: review
					references: Util.byType review?.references
					activeAssignments: activeAssignments
					completedAssignments: completedAssignments
					canceledAssignments: canceledAssignments
					closed: review and (review.state is 'COMPLETED' or review.state is 'CANCELED')
					currentUser: currentUser.get('username')
					formatDate: Format.date
					formatDateTime: Format.dateTime
					hasAssignment: Util.hasAssignment(review, currentUser)
				Renderer.render @, renderOptions

			sort: (elements, field, order = 'desc') ->
				factor = if order is 'asc' then -1 else 1
				elements.sort (a, b) ->
					valueA = if field then a[field] else a
					valueB = if field then b[field] else b
					if valueA > valueB
						return -1 * factor
					else if valueA < valueB
						return 1 * factor
					return 0


)