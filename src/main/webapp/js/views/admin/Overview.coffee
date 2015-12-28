define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/User'
				'templates/views/admin/overview'
				'templates/views/admin/overview-user-list'
				'templates/views/admin/overview-repository-list'
			]

	(Backbone, Events, Model, Renderer, Router, User, template, usersTemplate, repositoriesTemplate) ->

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

			filterRepositories = (event) ->
				Events.preventDefault event
				target = $ Events.target event
				if target.is('input')
					@repositoryFilter = target.val()
					@repositoryPage = 1
				else			
					@repositoryPage = parseInt target.attr 'data-page'
				(@_ loadRepositories) (total) =>
					(@_ appendRepositories) total

			loadRepositories = (callback) ->
				page = @repositoryPage
				filter = @repositoryFilter
				$.get "/ws/admin/repository?page=#{page}&filter=#{filter}&adminArea=true", (result) => 
					@repositories = result.data
					callback?.apply @, [result.total]

			appendRepositories = (total) ->
				@$('#repositories').html repositoriesTemplate
					repositories: @repositories
					page: @repositoryPage
					pageCount: Math.ceil(total / 10)
				@$('#repositories .paging a').on 'click', (event) => 
					(@_ filterRepositories) (event)

			className: 'admin-overview two-columns'

			events: 
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=create-new-user]': () -> Router.navigate 'admin/user/new'
				'click [data-action=create-new-repository]': () -> Router.navigate 'admin/repository/new'
				'keyup #user-filter': filterUsers
				'keyup #repository-filter': filterRepositories

			initialize: () ->
				@userPage = 1
				@userFilter = ''
				@repositoryPage = 1
				@repositoryFilter = ''

			render: (renderOptions) ->
				(@_ loadRepositories) (totalRepos) =>
					(@_ loadUsers) (totalUsers) =>
						@$el.html template
							users: totalUsers
							userPage: @userPage
							repositories: totalRepos
							repositoryPage: @repositoryPage
						(@_ appendUsers) totalUsers
						(@_ appendRepositories) totalRepos
						Renderer.render @, renderOptions

			_: (callback) ->
				() =>
					callback.apply @, arguments

)