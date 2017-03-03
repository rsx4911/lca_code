define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Model'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/Team'
				'cs!models/CurrentUser'
				'templates/views/team/profile'
			]

	(Backbone, Avatar, Data, Events, Forms, Layers, Model, Renderer, Router, Team, currentUser, template) ->

		class TeamProfile extends Backbone.View

			className: 'profile-view team-view multi-box-view'

			events:
				'submit #team-form': (event) -> @saveTeam event
				'click [data-action=delete-team]': (event) -> @deleteTeam event
				'click [data-action=add-members]': (event) -> @showAddMembersLayer event
				'click [data-action=remove-member]': (event) -> @removeMember event
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'team', @team.get('teamname')

			initialize: (options) ->
				{@team} = options

			render: (renderOptions) ->
				if @team.get('teamname')
					Model.fetch @team, 
						success: () =>
							@doRender renderOptions
				else
					@doRender renderOptions

			doRender: (renderOptions) ->
				team = @team.toJSON()
				@$el.html template
					team: team
				Renderer.render @, renderOptions
				Forms.fill 'team-form', team

			saveTeam: (event) ->
				Events.preventDefault event
				@team.set Forms.toJson 'team-form'
				teamname = @team.get 'teamname'
				unless teamname
					Forms.handleError 'team-form', {responseJSON: {field: 'teamname', message: 'Missing input: Teamname'}}
					return false
				if !@team.get('id')
						$.ajax
							type: 'POST'
							url: "ws/admin/team/#{teamname}"
							data: JSON.stringify @team.toJSON()
							contentType: 'application/json'
							success: () -> Router.navigate 'administration/overview'
							error: (response) -> Forms.handleError 'team-form', response
				else
					Model.save @team, 
						success: () -> Router.navigate 'administration/overview'
						error: (model, response) -> Forms.handleError 'team-form', response
				return false

			showAddMembersLayer: (event) ->
				Data.getUsers 'teams', (users) =>
					Layers.showTemplateInLayer
						template: 'team/add-members'
						title: 'Add team members'
						model: {users: Data.usersToOptions(users, @team.get('users'))}
						buttons: [{id: 'add-members', className: 'btn-success', text: 'Add to team', callback: () => @addMembers()}]

			addMembers: () ->
				users = $('#add-members-form #users').val()
				for username in users
					@team.get('users').push {username: username}
				Model.save @team, 
					success: () ->
						Layers.closeActive()
						Backbone.history.loadUrl()

			removeMember: (event) ->
				remaining = []
				toRemove = $(Events.target event, 'button').attr 'data-username'
				for user in @team.get('users')
					unless user.username is toRemove
						remaining.push user
				@team.set 'users', remaining
				Model.save @team, 
					success: () ->
						Layers.closeActive()
						Backbone.history.loadUrl()

			deleteTeam: (event) ->
				teamname = @team.get 'teamname'
				Layers.askDeleteQuestion "team #{teamname}", teamname, () =>
					$.ajax
						type: 'DELETE'
						url: "ws/admin/team/#{teamname}"
						success: () -> Router.navigate 'administration/overview'

)