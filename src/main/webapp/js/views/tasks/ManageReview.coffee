define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!models/CurrentUser'
				'templates/views/tasks/manage-review'
			]

	(Backbone, Router, Events, Format, Forms, Layers, Renderer, Status, currentUser, template) ->

		class CreateReviewView extends Backbone.View

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
							url: "ws/task/review/#{taskId}/cancel/#{user}"
							success: (response) => 
								@userMenu.updateNoOfTasks response.activeTasks
								Router.navigate 'tasks'

			complete: (event) ->
				Events.preventDefault event
				target = $ Events.target event, 'button'
				user = target.attr('data-username') or ''
				taskId = @reviewId
				$.ajax
					type: 'PUT'
					url: "ws/task/review/#{taskId}/complete/#{user}"
					success: (response) => 
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

			markAsReviewed: (event) ->
				target = $ Events.target event, 'input'
				refId = target.attr('id') or ''
				value = target.is ':checked'
				taskId = @reviewId
				$.ajax
					type: 'PUT'
					url: "ws/task/review/#{taskId}/markAsReviewed/#{refId}/#{value}"
					success: (response) => 
						@userMenu.updateNoOfTasks response.activeTasks

			className: 'tasks-view multi-box-view'

			events: 
				'click a[href]:not([href=#]):not([data-action])': (event) -> Events.followLink event
				'click [data-action=create-task]': 'createTask'
				'click [data-action=assign-task]': 'assignTask'
				'click [data-action=cancel-assignment]': 'cancel'
				'click [data-action=complete-assignment]': 'complete'
				'click [data-action=cancel-task]': 'cancel'
				'click [data-action=complete-task]': 'complete'
				'click [data-action=select-references]': 'selectReferences'
				'click [data-action=mark-as-reviewed]': 'markAsReviewed'

			initialize: (options) ->
				{@reviewId, @userMenu} = options

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/repository?page=0&module=REVIEW'
					success: (repositories) =>
						selectable = ['']
						for repo in repositories
							selectable .push "#{repo.group}/#{repo.name}"
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
				referencesMap = {}				
				if review?.references?.length
					for ref in review.references
						forType = referencesMap[ref.type] or []
						forType.push ref
						referencesMap[ref.type] = forType
				references = []
				for type in Object.keys(referencesMap)
					@sort referencesMap[type], 'name', 'asc'
					references.push {type: type, references: referencesMap[type]}
				@$el.html template
					repositories: repositories
					review: review
					references: references
					activeAssignments: activeAssignments
					completedAssignments: completedAssignments
					canceledAssignments: canceledAssignments
					closed: review and (review.state is 'COMPLETED' or review.state is 'CANCELED')
					currentUser: currentUser.get('username')
					formatDate: Format.date
					formatDateTime: Format.dateTime
					hasAssignment: @hasAssignment(review)
				Renderer.render @, renderOptions

			hasAssignment: (review) ->
				unless review
					return false
				for assignment in review.assignments
					if assignment.assignedTo.username is currentUser.get('username') and !assignment.endDate
						return true
				return false

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