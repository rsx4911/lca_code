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
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event
				'submit #team-form': 'saveTeam'
				'submit #active-form': 'setActiveUntil'
				'click [data-action=delete-team]': 'deleteTeam'
				'click [data-action=add-members]': 'showAddMembersLayer'
				'click [data-action=remove-member]': 'removeMember'
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
				Avatar.initCropper 'team', @team.get('teamname')

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
							url: "ws/usermanager/team/#{teamname}"
							data: JSON.stringify @team.toJSON()
							contentType: 'application/json'
							success: () -> Router.navigate 'administration/overview'
							error: (response) -> Forms.handleError 'team-form', response
				else
					Model.save @team, 
						success: () -> Router.navigate 'administration/overview'
						error: (model, response) -> Forms.handleError 'team-form', response
				return false

			setActiveUntil: (event) ->
				Events.preventDefault event
				teamname = @team.get 'teamname'
				value = @$('#active-until').val()
				$.ajax
					type: 'PUT'
					url: "ws/usermanager/team/#{teamname}/activeuntil"
					data: JSON.stringify { activeUntil: value }
					contentType: 'application/json'
					success: () -> Backbone.history.loadUrl()

			showAddMembersLayer: (event) ->
				$.ajax
					type: 'GET'
					url: 'ws/usermanager/user'
					success: (result) =>
						Layers.showTemplateInLayer
							template: 'team/add-members'
							title: 'Add team members'
							model: {users: Data.usersToOptions(result.data, @team.get('users'))}
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
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: "ws/usermanager/team/#{teamname}"
						success: () -> 
							Layers.hideProgressIndicator()
							Router.navigate 'administration/overview'

)