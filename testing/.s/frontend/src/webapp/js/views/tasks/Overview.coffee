define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/tasks/overview'
			]

	(Backbone, Events, Layers, Renderer, currentUser, template) ->

		class TaskOverview extends Backbone.View

			className: 'tasks-view multi-box-view'

			events: 
				'click a[href]:not([href="#"]):not([data-action])': (event) -> Events.followLink event
				'click button[data-route]': (event) -> Events.followRoute event

			initialize: (options) ->
				{@userMenu} = options

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/task/general'
					success: (result) =>
						todo = []
						inProgress = []
						completed = []
						canceled = []
						for task in result.tasks
							switch task.state
								when 'COMPLETED' then completed.push task
								when 'CANCELED' then canceled.push task
								else 
									if currentUser.get('username') is task.initiator.username
										if task.state is 'VERIFYING' or task.state is 'CREATED' then todo.push task else inProgress.push task
									else
										if task.state is 'VERIFYING' then inProgress.push task else todo.push task
						@userMenu.updateNoOfTasks todo.length
						@$el.html template
							canCreateTasks: result.canCreateTasks
							tasks: 
								todo: @sortByRepository(todo)
								inProgress: @sortByRepository(inProgress)
								completed: @sortByRepository(completed)
								canceled: @sortByRepository(canceled)
						Renderer.render @, renderOptions

			sortByRepository: (tasks) ->
				tasks.sort (a, b) -> 
					if a.repositoryPath < b.repositoryPath
						return -1
					else
						return 1
					return 0


)