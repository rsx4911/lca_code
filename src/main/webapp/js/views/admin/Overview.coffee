define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/User'
				'templates/views/admin/overview'
				'templates/views/admin/overview-userlist'
			]

	(Backbone, Events, Model, Renderer, Router, User, template, usersTemplate) ->

		class AdminOverview extends Backbone.View

			filterUsers = (event) ->
				Events.preventDefault event
				target = $ Events.target event
				if target.is('input')
					@userFilter = target.val()
					@userPage = 1
				else			
					@userPage = parseInt target.attr 'data-page'
				(@_ loadUsers) (noOfUsers) =>
					(@_ appendUsers) noOfUsers

			loadUsers = (callback) ->
				page = @userPage
				filter = @userFilter
				$.get "/ws/admin/user?page=#{page}&filter=#{filter}", (result) => 
					@users = result.data
					callback?.apply @, [result.total]

			appendUsers = (noOfUsers) ->
				@$('#users').html usersTemplate
					users: @users
					page: @userPage
					pageCount: Math.ceil(noOfUsers / 10)
				@$('#users .paging a').on 'click', (event) => 
					(@_ filterUsers) (event)

			loadProjects = (callback) ->
				page = @projectPage
				filter = @projectFilter
				@projects = []
				callback?.apply @, [0]

			className: 'admin-overview two-columns'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=create-new-user]': () -> Router.navigate 'admin/user/new'
				'keyup #user-filter': filterUsers

			initialize: () ->
				@userPage = 1
				@userFilter = ''
				@projectPage = 1
				@projectFilter = ''

			render: (renderOptions) ->
				(@_ loadProjects) (noOfProjects) =>
					(@_ loadUsers) (noOfUsers) =>
						@$el.html template
							users: noOfUsers
							userPage: @userPage
							projects: noOfProjects
							projectPage: @projectPage
						(@_ appendUsers) noOfUsers
						Renderer.render @, renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)